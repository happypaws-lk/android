package lk.happypaws.app.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmEmailChangeRequest(
    val newEmail: String,
    val code: String
)
