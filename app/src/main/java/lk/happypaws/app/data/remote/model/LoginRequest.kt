package lk.happypaws.app.data.remote.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerializedName("email")
    @SerialName("email")
    val email: String,

    @SerializedName("password")
    @SerialName("password")
    val password: String
)
