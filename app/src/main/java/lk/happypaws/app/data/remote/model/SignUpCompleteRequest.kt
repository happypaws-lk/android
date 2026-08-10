package lk.happypaws.app.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class SignUpCompleteRequest(
    val signupToken: String,
    val name: String,
    val password: String,
    val role: String = "Adopter"
)
