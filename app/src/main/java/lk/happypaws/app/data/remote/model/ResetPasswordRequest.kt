package lk.happypaws.app.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequest(
    val email: String,
    val resetToken: String,
    val newPassword: String
)
