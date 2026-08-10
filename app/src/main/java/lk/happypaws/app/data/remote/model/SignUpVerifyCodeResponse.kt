package lk.happypaws.app.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class SignUpVerifyCodeResponse(
    val signupToken: String
)
