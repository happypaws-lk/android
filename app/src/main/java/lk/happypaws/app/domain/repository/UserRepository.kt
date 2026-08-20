package lk.happypaws.app.domain.repository

import kotlinx.coroutines.flow.StateFlow
import lk.happypaws.app.data.remote.model.AvatarUploadResponse
import lk.happypaws.app.data.remote.model.KycDocumentResponse
import lk.happypaws.app.data.remote.model.LifestyleProfileRequest
import lk.happypaws.app.data.remote.model.LifestyleProfileResponse
import lk.happypaws.app.data.remote.model.MeProfileResponse
import lk.happypaws.app.data.remote.model.RoleRequestResponse
import lk.happypaws.app.data.remote.model.UserProfileResponse
import lk.happypaws.app.domain.model.DocumentType
import lk.happypaws.app.domain.model.UserRole

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
    suspend fun registerDevice(fcmToken: String, deviceName: String?): Result<lk.happypaws.app.data.remote.model.DeviceResponse>
    suspend fun removeDevice(id: String): Result<Unit>
    suspend fun getKycDocuments(): Result<List<KycDocumentResponse>>
    suspend fun uploadKycDocument(documentType: DocumentType, bytes: ByteArray, filename: String, mimeType: String): Result<KycDocumentResponse>
    suspend fun getRoleRequests(): Result<List<RoleRequestResponse>>
    suspend fun submitRoleRequest(role: UserRole, documentType: DocumentType, justification: String?, bytes: ByteArray, filename: String, mimeType: String): Result<RoleRequestResponse>
}
