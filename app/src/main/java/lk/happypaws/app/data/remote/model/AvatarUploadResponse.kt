package lk.happypaws.app.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class AvatarUploadResponse(
    val avatarKey: String,
    val avatarUrl: String
)
