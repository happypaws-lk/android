package lk.happypaws.app.data.remote.api

import lk.happypaws.app.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // Skip adding auth header if the No-Authentication header is present
        if (request.header("No-Authentication") != null) {
            val newRequest = request.newBuilder()
                .removeHeader("No-Authentication")
                .build()
            return chain.proceed(newRequest)
        }

        val requestBuilder = request.newBuilder()
        val token = tokenManager.getAccessTokenSync()
        
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        
        return chain.proceed(requestBuilder.build())
    }
}
