package lk.happypaws.app.data.remote.api

import kotlinx.coroutines.runBlocking
import lk.happypaws.app.data.local.TokenManager
import lk.happypaws.app.data.remote.model.RefreshRequest
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val sessionManager: SessionManager,
    private val authApiProvider: Provider<AuthApi>
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = tokenManager.getRefreshTokenSync()
        
        if (refreshToken.isNullOrEmpty()) {
            handleSessionExpired()
            return null
        }

        synchronized(this) {
            val currentAccessToken = tokenManager.getAccessTokenSync()
            val requestAccessToken = response.request.header("Authorization")?.removePrefix("Bearer ")

            // If the token was already updated by another thread, retry with the new one
            if (currentAccessToken != null && currentAccessToken != requestAccessToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentAccessToken")
                    .build()
            }

            // Otherwise, perform the refresh
            return runBlocking {
                try {
                    val refreshResponse = authApiProvider.get().refreshToken(RefreshRequest(refreshToken))
                    
                    if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                        val newAuthResponse = refreshResponse.body()!!
                        tokenManager.saveTokensSync(
                            newAuthResponse.accessToken,
                            newAuthResponse.refreshToken
                        )
                        
                        response.request.newBuilder()
                            .header("Authorization", "Bearer ${newAuthResponse.accessToken}")
                            .build()
                    } else {
                        handleSessionExpired("Your session expired after a period of inactivity. Please log in again.")
                        null
                    }
                } catch (_: java.io.IOException) {
                    // Network connectivity issue - do NOT log out the user when offline
                    null
                } catch (_: Exception) {
                    handleSessionExpired("Your session has expired for your security. Please log in again.")
                    null
                }
            }
        }
    }

    private fun handleSessionExpired(message: String = "Your session has expired for your security. Please log in again to continue.") {
        runBlocking {
            tokenManager.clearTokensSync()
            sessionManager.notifySessionExpired(message)
        }
    }
}
