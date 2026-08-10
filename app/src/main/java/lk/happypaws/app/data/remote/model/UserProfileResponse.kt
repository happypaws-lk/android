package lk.happypaws.app.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponse(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String? = null,
    val isVerified: Boolean,
    val reputationPoints: Int,
    val badges: List<BadgeResponse> = emptyList()
)
