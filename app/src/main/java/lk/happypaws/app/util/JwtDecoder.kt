package lk.happypaws.app.util

import android.util.Base64
import kotlinx.serialization.json.Json
import java.util.Collections.emptyMap

object JwtDecoder {

    fun decodeClaims(token: String): Map<String, String> {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return emptyMap()

            val payloadBytes = Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
            val payload = String(payloadBytes, Charsets.UTF_8)
            
            val json = Json { ignoreUnknownKeys = true }
            val rawMap = json.decodeFromString<Map<String, String>>(payload)

            val claims = mutableMapOf<String, String>()
            
            // Extract Subject (User ID)
            rawMap["sub"]?.let { claims["sub"] = it }
            
            // Extract Role (Standard JWT claim or Microsoft URI claim)
            val role = rawMap["role"] 
                ?: rawMap["http://schemas.microsoft.com/ws/2008/06/identity/claims/role"]
            role?.let { claims["role"] = it }
            
            claims
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
