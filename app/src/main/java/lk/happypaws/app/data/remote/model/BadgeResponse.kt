package lk.happypaws.app.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class BadgeResponse(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val unlockedAt: String
)
