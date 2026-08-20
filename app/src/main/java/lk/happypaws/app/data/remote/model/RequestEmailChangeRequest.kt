package lk.happypaws.app.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class RequestEmailChangeRequest(
    val newEmail: String,
    val currentPassword: String
)
