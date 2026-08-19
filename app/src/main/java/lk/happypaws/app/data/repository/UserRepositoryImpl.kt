package lk.happypaws.app.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lk.happypaws.app.data.local.UserManager
import lk.happypaws.app.data.remote.api.UserApi
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
    private val userManager: UserManager
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
}
