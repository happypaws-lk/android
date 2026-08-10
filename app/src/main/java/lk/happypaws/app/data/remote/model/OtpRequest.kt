package lk.happypaws.app.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class OtpRequest(
    val email: String
)
