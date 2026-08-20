package lk.happypaws.app.data.remote.model

import androidx.annotation.Keep

@Keep
data class DeviceRegistrationRequest(
    val fcmToken: String,
    val deviceName: String?,
    val platform: String
)
