package lk.happypaws.app.domain.repository

import kotlinx.coroutines.flow.StateFlow
import lk.happypaws.app.data.remote.model.AvatarUploadResponse
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
}
