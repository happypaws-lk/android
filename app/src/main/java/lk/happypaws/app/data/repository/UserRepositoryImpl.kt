package lk.happypaws.app.data.repository

import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lk.happypaws.app.data.local.TokenManager
import lk.happypaws.app.data.local.UserManager
import lk.happypaws.app.data.remote.api.UserApi
import lk.happypaws.app.data.remote.model.DeviceRegistrationRequest
import lk.happypaws.app.data.remote.model.MeProfileResponse
import lk.happypaws.app.data.remote.model.UserProfileResponse
import lk.happypaws.app.domain.repository.UserRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val userManager: UserManager,
    private val tokenManager: TokenManager
) : UserRepository {

    private val _currentUser = MutableStateFlow<UserProfileResponse?>(null)
    private val _currentMeProfile = MutableStateFlow<lk.happypaws.app.data.remote.model.MeProfileResponse?>(null)
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    init {
        repositoryScope.launch {
            userManager.userProfileFlow.collect { profile ->
                _currentUser.value = profile
            }
        }
        repositoryScope.launch {
            userManager.meProfileFlow.collect { profile ->
                _currentMeProfile.value = profile
            }
        }
    }

    override fun getCurrentUser(): StateFlow<UserProfileResponse?> {
        // Trigger background refresh silently
        repositoryScope.launch {
            refreshUser()
        }
        return _currentUser.asStateFlow()
    }

    override fun getMeProfileStream(): StateFlow<lk.happypaws.app.data.remote.model.MeProfileResponse?> {
        repositoryScope.launch {
            fetchMeProfile()
        }
        return _currentMeProfile.asStateFlow()
    }

    override suspend fun refreshUser() {
        try {
            val response = userApi.getCurrentUser()
            if (response.isSuccessful && response.body() != null) {
                userManager.saveUserProfile(response.body()!!)
            }
        } catch (_: Exception) {
            // Silently fail or log
        }
        try {
            val meResponse = userApi.getMeProfile()
            if (meResponse.isSuccessful && meResponse.body() != null) {
                val profile = meResponse.body()!!
                userManager.saveMeProfile(profile)
                _currentMeProfile.value = profile
            }
        } catch (_: Exception) {
            // Silently fail or log
        }
    }

    override suspend fun fetchMeProfile(): Result<lk.happypaws.app.data.remote.model.MeProfileResponse> {
        return try {
            val response = userApi.getMeProfile()
            if (response.isSuccessful && response.body() != null) {
                val profile = response.body()!!
                userManager.saveMeProfile(profile)
                _currentMeProfile.value = profile
                Result.success(profile)
            } else {
                Result.failure(Exception("Failed to fetch profile: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMeProfile(name: String): Result<lk.happypaws.app.data.remote.model.MeProfileResponse> {
        return try {
            val request = lk.happypaws.app.data.remote.model.UpdateMeProfileRequest(name = name)
            val response = userApi.updateMeProfile(request)
            if (response.isSuccessful && response.body() != null) {
                val updated = response.body()!!
                userManager.saveMeProfile(updated)
                _currentMeProfile.value = updated
                refreshUser()
                Result.success(updated)
            } else {
                Result.failure(Exception("Failed to update profile: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadAvatar(
        bytes: ByteArray,
        filename: String,
        mimeType: String
    ): Result<lk.happypaws.app.data.remote.model.AvatarUploadResponse> {
        return try {
            val mediaType = mimeType.toMediaTypeOrNull()
            val requestBody = bytes.toRequestBody(mediaType)
            val part = MultipartBody.Part.createFormData("file", filename, requestBody)
            val response = userApi.uploadAvatar(part)
            if (response.isSuccessful && response.body() != null) {
                val uploadResult = response.body()!!
                fetchMeProfile()
                refreshUser()
                Result.success(uploadResult)
            } else {
                Result.failure(Exception("Failed to upload avatar: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAvatar(): Result<Unit> {
        return try {
            val response = userApi.deleteAvatar()
            if (response.isSuccessful) {
                _currentMeProfile.value?.let { current ->
                    val updated = current.copy(avatarKey = null)
                    userManager.saveMeProfile(updated)
                    _currentMeProfile.value = updated
                }
                refreshUser()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete avatar: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun requestEmailChange(newEmail: String, currentPassword: String): Result<Unit> {
        return try {
            val request = lk.happypaws.app.data.remote.model.RequestEmailChangeRequest(newEmail, currentPassword)
            val response = userApi.requestEmailChange(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = parseErrorMessage(response) ?: "Failed to send verification code (${response.code()})"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun confirmEmailChange(newEmail: String, code: String): Result<MeProfileResponse> {
        return try {
            val request = lk.happypaws.app.data.remote.model.ConfirmEmailChangeRequest(newEmail, code)
            val response = userApi.confirmEmailChange(request)
            if (response.isSuccessful && response.body() != null) {
                val updated = response.body()!!
                userManager.saveMeProfile(updated)
                _currentMeProfile.value = updated
                refreshUser()
                Result.success(updated)
            } else {
                val errorMsg = parseErrorMessage(response) ?: "Invalid or expired verification code (${response.code()})"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLifestyleProfile(): Result<lk.happypaws.app.data.remote.model.LifestyleProfileResponse?> {
        return try {
            val response = userApi.getLifestyleProfile()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body())
            } else if (response.code() == 404) {
                // Profile not created yet
                Result.success(null)
            } else {
                val errorMsg = parseErrorMessage(response) ?: "Failed to load lifestyle profile (${response.code()})"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun upsertLifestyleProfile(
        request: lk.happypaws.app.data.remote.model.LifestyleProfileRequest
    ): Result<lk.happypaws.app.data.remote.model.LifestyleProfileResponse> {
        return try {
            val response = userApi.upsertLifestyleProfile(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else if (response.code() == 403) {
                Result.failure(Exception("Identity verification (KYC) is required to save your lifestyle profile."))
            } else {
                val errorMsg = parseErrorMessage(response) ?: "Failed to save lifestyle profile (${response.code()})"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDevices(): Result<List<lk.happypaws.app.data.remote.model.DeviceResponse>> = withContext(Dispatchers.IO) {
        try {
            val response = userApi.getDevices()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                val errorMsg = parseErrorMessage(response) ?: "Failed to fetch devices (${response.code()})"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerDevice(fcmToken: String, deviceName: String?): Result<lk.happypaws.app.data.remote.model.DeviceResponse> = withContext(Dispatchers.IO) {
        try {
            val request = DeviceRegistrationRequest(fcmToken, deviceName, "Android")
            val response = userApi.registerDevice(request)
            if (response.isSuccessful && response.body() != null) {
                val device = response.body()!!
                tokenManager.saveCurrentDeviceId(device.id)
                Result.success(device)
            } else {
                val errorMsg = parseErrorMessage(response) ?: "Failed to register device (${response.code()})"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeDevice(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = userApi.removeDevice(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = parseErrorMessage(response) ?: "Failed to remove device (${response.code()})"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getKycDocuments(): Result<List<lk.happypaws.app.data.remote.model.KycDocumentResponse>> {
        return try {
            val response = userApi.getKycDocuments()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                val errorMsg = parseErrorMessage(response) ?: "Failed to fetch KYC documents (${response.code()})"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadKycDocument(
        documentType: lk.happypaws.app.domain.model.DocumentType,
        bytes: ByteArray,
        filename: String,
        mimeType: String
    ): Result<lk.happypaws.app.data.remote.model.KycDocumentResponse> {
        return try {
            val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("document", filename, requestBody)
            val response = userApi.uploadKycDocument(documentType.apiValue, part)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = parseErrorMessage(response) ?: "Failed to upload document (${response.code()})"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRoleRequests(): Result<List<lk.happypaws.app.data.remote.model.RoleRequestResponse>> {
        return try {
            val response = userApi.getRoleRequests()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                val errorMsg = parseErrorMessage(response) ?: "Failed to fetch role requests (${response.code()})"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitRoleRequest(
        role: lk.happypaws.app.domain.model.UserRole,
        documentType: lk.happypaws.app.domain.model.DocumentType,
        justification: String?,
        bytes: ByteArray,
        filename: String,
        mimeType: String
    ): Result<lk.happypaws.app.data.remote.model.RoleRequestResponse> {
        return try {
            val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("document", filename, requestBody)
            val justificationBody = justification?.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = userApi.submitRoleRequest(
                role = role.name.lowercase().replaceFirstChar { it.uppercase() },
                documentType = documentType.apiValue,
                document = part,
                justification = justificationBody
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = parseErrorMessage(response) ?: "Failed to submit role request (${response.code()})"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseErrorMessage(response: retrofit2.Response<*>): String? {
        return try {
            val errorJson = response.errorBody()?.string() ?: return null
            if (errorJson.startsWith("\"") && errorJson.endsWith("\"")) {
                errorJson.trim('"')
            } else {
                val json = org.json.JSONObject(errorJson)
                val detail = if (json.has("detail")) json.getString("detail") else null
                val title = if (json.has("title")) json.getString("title") else null
                detail ?: title
            }
        } catch (e: Exception) {
            null
        }
    }
}
