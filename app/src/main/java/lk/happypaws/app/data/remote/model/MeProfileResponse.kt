package lk.happypaws.app.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class RoleResponse(
    val role: String,
    val assignedAt: String
)

@Serializable
data class MeProfileResponse(
    val id: String,
    val name: String,
    val email: String,
    val avatarKey: String? = null,
    val isVerified: Boolean,
    val reputationPoints: Int,
    val isSuspended: Boolean,
    val suspendedAt: String? = null,
    val suspendedReason: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val roles: List<RoleResponse> = emptyList()
)
