package lk.happypaws.app.domain.repository

import kotlinx.coroutines.flow.StateFlow
import lk.happypaws.app.data.remote.model.AvatarUploadResponse
import lk.happypaws.app.data.remote.model.LifestyleProfileRequest
import lk.happypaws.app.data.remote.model.LifestyleProfileResponse
import lk.happypaws.app.data.remote.model.MeProfileResponse
import lk.happypaws.app.data.remote.model.UserProfileResponse

interface UserRepository {
    fun getCurrentUser(): StateFlow<UserProfileResponse?>
    fun getMeProfileStream(): StateFlow<MeProfileResponse?>
    suspend fun refreshUser()
    suspend fun fetchMeProfile(): Result<MeProfileResponse>
    suspend fun updateMeProfile(name: String): Result<MeProfileResponse>
    suspend fun uploadAvatar(bytes: ByteArray, filename: String, mimeType: String): Result<AvatarUploadResponse>
    suspend fun deleteAvatar(): Result<Unit>
    suspend fun requestEmailChange(newEmail: String, currentPassword: String): Result<Unit>
    suspend fun confirmEmailChange(newEmail: String, code: String): Result<MeProfileResponse>
    suspend fun getLifestyleProfile(): Result<LifestyleProfileResponse?>
    suspend fun upsertLifestyleProfile(request: LifestyleProfileRequest): Result<LifestyleProfileResponse>
    suspend fun getDevices(): Result<List<lk.happypaws.app.data.remote.model.DeviceResponse>>
    suspend fun removeDevice(id: String): Result<Unit>
}
