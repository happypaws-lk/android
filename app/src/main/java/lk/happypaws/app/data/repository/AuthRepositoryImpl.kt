package lk.happypaws.app.data.repository

import android.content.Context
import android.util.Log
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.CreateCredentialException
import lk.happypaws.app.data.local.TokenManager
import lk.happypaws.app.data.local.UserManager
import lk.happypaws.app.data.remote.api.AuthApi
import lk.happypaws.app.data.remote.model.AuthResponse
import lk.happypaws.app.data.remote.model.ForgotPasswordRequest
import lk.happypaws.app.data.remote.model.LoginRequest
import lk.happypaws.app.data.remote.model.OtpRequest
import lk.happypaws.app.data.remote.model.OtpVerifyRequest
import lk.happypaws.app.data.remote.model.ResetPasswordRequest
import lk.happypaws.app.data.remote.model.RevokeRequest
import lk.happypaws.app.data.remote.model.SignUpCompleteRequest
import lk.happypaws.app.data.remote.model.VerifyResetCodeRequest
import lk.happypaws.app.domain.repository.AuthRepository
import lk.happypaws.app.util.findActivity
import lk.happypaws.app.util.JwtDecoder
import lk.happypaws.app.data.remote.util.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager,
    private val userManager: UserManager
) : AuthRepository {

    override suspend fun login(email: String, passwordHash: String): Result<AuthResponse> {
        val result = safeApiCall(defaultAuthError = "Incorrect email or password. Please try again.") {
            authApi.login(LoginRequest(email = email, password = passwordHash))
        }
        result.onSuccess { authResponse ->
            val claims = JwtDecoder.decodeClaims(authResponse.accessToken)
            val userId = claims["sub"] ?: ""
            val role = claims["role"] ?: ""
            
            tokenManager.saveTokens(authResponse.accessToken, authResponse.refreshToken)
            tokenManager.saveAuthDetails(userId, role)
        }
        return result
    }

    override suspend fun completeSignUp(signupToken: String, name: String, passwordHash: String, role: String): Result<AuthResponse> {
        val result = safeApiCall(
            defaultAuthError = "Registration failed. Please try again."
        ) {
            authApi.completeSignUp(SignUpCompleteRequest(signupToken, name, passwordHash, role))
        }
        result.onSuccess { authResponse ->
            val claims = JwtDecoder.decodeClaims(authResponse.accessToken)
            val userId = claims["sub"] ?: ""
            val roleClaim = claims["role"] ?: ""

            tokenManager.saveTokens(authResponse.accessToken, authResponse.refreshToken)
            tokenManager.saveAuthDetails(userId, roleClaim)
        }
        return result
    }

    override suspend fun sendOtp(email: String): Result<Unit> {
        return safeApiCall(
            defaultAuthError = "Failed to send verification code. Please try again.",
            conflictError = "Email in use. Log in or use a different email."
        ) {
            authApi.sendOtp(OtpRequest(email))
        }
    }

    override suspend fun verifySignUpCode(email: String, code: String): Result<String> {
        val result = safeApiCall(defaultAuthError = "Invalid verification code.") {
            authApi.verifySignUpCode(OtpVerifyRequest(email, code))
        }
        return result.map { it.signupToken }
    }

    override suspend fun forgotPassword(email: String): Result<Unit> {
        return safeApiCall(defaultAuthError = "Failed to send reset code. Please try again.") {
            authApi.forgotPassword(ForgotPasswordRequest(email))
        }
    }

    override suspend fun verifyResetCode(email: String, code: String): Result<String> {
        val result = safeApiCall(defaultAuthError = "Invalid reset code.") {
            authApi.verifyResetCode(VerifyResetCodeRequest(email, code))
        }
        return result.map { it.resetToken }
    }

    override suspend fun resetPassword(email: String, resetToken: String, newPassword: String): Result<Unit> {
        return safeApiCall(defaultAuthError = "Failed to reset password.") {
            authApi.resetPassword(ResetPasswordRequest(email, resetToken, newPassword))
        }
    }

    override fun logout() {
        // Clear local tokens synchronously to avoid race conditions
        tokenManager.clearTokensSync()
        
        // Fire and forget revocation call and async cache clearing
        val refreshToken = tokenManager.getRefreshTokenSync()
        CoroutineScope(Dispatchers.IO).launch {
            if (!refreshToken.isNullOrEmpty()) {
                try {
                    authApi.revokeToken(RevokeRequest(refreshToken))
                } catch (_: Exception) {
                }
            }
            tokenManager.clearAll()
            userManager.clearUserData()
        }
    }

    override suspend fun saveCredentials(email: String, password: String, context: Context) {
        val activity = context.findActivity() ?: run {
            Log.e(TAG, "saveCredentials failed: No Activity context found")
            return
        }
        
        val credentialManager = CredentialManager.create(activity)
        val request = CreatePasswordRequest(email, password)
        
        try {
            Log.d(TAG, "Attempting to create credential for $email")
            credentialManager.createCredential(activity, request)
            Log.d(TAG, "Credential creation successful")
        } catch (e: CreateCredentialException) {
            Log.e(TAG, "saveCredentials failed: ${e.message}", e)
        } catch (e: Exception) {
            Log.e(TAG, "saveCredentials encountered an unexpected error", e)
        }
    }

    override suspend fun getSavedCredentials(context: Context): Result<Pair<String, String>> {
        val activity = context.findActivity() ?: return Result.failure(Exception("No Activity context found"))
        val credentialManager = CredentialManager.create(activity)
        val getPasswordOption = GetPasswordOption()
        val request = GetCredentialRequest(listOf(getPasswordOption))

        return try {
            val result = credentialManager.getCredential(activity, request)
            when (val credential = result.credential) {
                is PasswordCredential -> Result.success(Pair(credential.id, credential.password))
                else -> Result.failure(Exception("Unexpected credential type"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isLoggedIn(): Boolean {
        return !tokenManager.getAccessTokenSync().isNullOrEmpty()
    }

    override fun hasCompletedOnboarding(): Boolean {
        return tokenManager.hasCompletedOnboardingSync()
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        tokenManager.setOnboardingCompleted(completed)
    }

    companion object {
        private const val TAG = "AuthRepo"
    }
}
