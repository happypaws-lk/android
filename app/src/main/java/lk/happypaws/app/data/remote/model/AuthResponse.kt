package lk.happypaws.app.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: String
)
