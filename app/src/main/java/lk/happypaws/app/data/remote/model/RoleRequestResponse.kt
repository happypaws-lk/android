package lk.happypaws.app.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class RoleRequestResponse(
    val id: String,
    val role: String,
    val status: String,
    val justification: String? = null,
    val rejectionReason: String? = null,
    val createdAt: String,
    val reviewedAt: String? = null
)
