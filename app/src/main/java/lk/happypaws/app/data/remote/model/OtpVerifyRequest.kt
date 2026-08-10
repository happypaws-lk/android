package lk.happypaws.app.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class OtpVerifyRequest(
    val email: String,
    val code: String
)
