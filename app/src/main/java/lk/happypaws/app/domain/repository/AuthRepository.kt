package lk.happypaws.app.domain.repository

import android.content.Context
import lk.happypaws.app.data.remote.model.AuthResponse

interface AuthRepository {
    suspend fun login(email: String, passwordHash: String): Result<AuthResponse>
    suspend fun completeSignUp(signupToken: String, name: String, passwordHash: String, role: String): Result<AuthResponse>
    suspend fun sendOtp(email: String): Result<Unit>
    suspend fun verifySignUpCode(email: String, code: String): Result<String>
    suspend fun forgotPassword(email: String): Result<Unit>
    suspend fun verifyResetCode(email: String, code: String): Result<String>
    suspend fun resetPassword(email: String, resetToken: String, newPassword: String): Result<Unit>
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit>
    fun logout()
    fun isLoggedIn(): Boolean
    fun hasCompletedOnboarding(): Boolean
    suspend fun setOnboardingCompleted(completed: Boolean)

    suspend fun saveCredentials(email: String, password: String, context: Context)
    suspend fun getSavedCredentials(context: Context): Result<Pair<String, String>>
}
