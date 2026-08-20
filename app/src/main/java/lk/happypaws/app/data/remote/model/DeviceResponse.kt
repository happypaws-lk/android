package lk.happypaws.app.data.remote.model

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class DeviceResponse(
    val id: String,
    val fcmToken: String,
    val deviceName: String? = null,
    val platform: String,
    val lastActiveAt: String
)