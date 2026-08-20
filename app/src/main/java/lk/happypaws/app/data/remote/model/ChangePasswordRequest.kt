package lk.happypaws.app.data.remote.model

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)
