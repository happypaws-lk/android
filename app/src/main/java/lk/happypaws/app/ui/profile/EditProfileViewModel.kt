package lk.happypaws.app.ui.profile

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lk.happypaws.app.data.remote.model.MeProfileResponse
import lk.happypaws.app.domain.repository.UserRepository
import javax.inject.Inject

import lk.happypaws.app.ui.profile.components.EmailChangeStep

data class EditProfileUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val profile: MeProfileResponse? = null,
    val name: String = "",
    val nameError: String? = null,
    val selectedImageBytes: ByteArray? = null,
    val selectedImageBitmap: Bitmap? = null,
    val selectedImageMime: String = "image/jpeg",
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val showEditEmailSheet: Boolean = false,
    val emailChangeStep: EmailChangeStep = EmailChangeStep.ENTER_DETAILS,
    val newEmail: String = "",
    val currentPassword: String = "",
    val emailOtpCode: String = "",
    val isEmailSubmitting: Boolean = false,
    val emailSheetError: String? = null
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            userRepository.fetchMeProfile().fold(
                onSuccess = { profile ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            profile = profile,
                            name = profile.name
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to load profile"
                        )
                    }
                }
            )
        }
    }

    fun onNameChange(name: String) {
        val error = when {
            name.isBlank() -> "Name cannot be empty"
            name.trim().length < 2 -> "Name must be at least 2 characters"
            name.length > 100 -> "Name cannot exceed 100 characters"
            else -> null
        }
        _uiState.update { it.copy(name = name, nameError = error, errorMessage = null) }
    }

    fun onImageSelected(bytes: ByteArray, bitmap: Bitmap, mimeType: String) {
        _uiState.update {
            it.copy(
                selectedImageBytes = bytes,
                selectedImageBitmap = bitmap,
                selectedImageMime = mimeType,
                errorMessage = null
            )
        }
    }

    fun clearSelectedImage() {
        _uiState.update {
            it.copy(
                selectedImageBytes = null,
                selectedImageBitmap = null
            )
        }
    }

    fun saveChanges(onSuccess: () -> Unit) {
        val state = _uiState.value
        val trimmedName = state.name.trim()

        if (trimmedName.length < 2) {
            _uiState.update { it.copy(nameError = "Name must be at least 2 characters") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            // 1. Upload avatar if selected
            if (state.selectedImageBytes != null) {
                val ext = if (state.selectedImageMime.contains("png")) "png" else "jpg"
                val avatarResult = userRepository.uploadAvatar(
                    bytes = state.selectedImageBytes,
                    filename = "avatar.$ext",
                    mimeType = state.selectedImageMime
                )
                if (avatarResult.isFailure) {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = avatarResult.exceptionOrNull()?.message ?: "Failed to upload avatar"
                        )
                    }
                    return@launch
                }
            }

            // 2. Update name if changed
            if (trimmedName != state.profile?.name) {
                val updateResult = userRepository.updateMeProfile(trimmedName)
                if (updateResult.isFailure) {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = updateResult.exceptionOrNull()?.message ?: "Failed to update profile"
                        )
                    }
                    return@launch
                }
            }

            _uiState.update {
                it.copy(
                    isSaving = false,
                    isSuccess = true,
                    selectedImageBytes = null,
                    selectedImageBitmap = null
                )
            }
            onSuccess()
        }
    }

    fun deleteAvatar(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            userRepository.deleteAvatar().fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            selectedImageBytes = null,
                            selectedImageBitmap = null,
                            profile = it.profile?.copy(avatarKey = null)
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.message ?: "Failed to delete avatar"
                        )
                    }
                }
            )
        }
    }

    fun openEditEmailSheet() {
        _uiState.update {
            it.copy(
                showEditEmailSheet = true,
                emailChangeStep = EmailChangeStep.ENTER_DETAILS,
                newEmail = "",
                currentPassword = "",
                emailOtpCode = "",
                isEmailSubmitting = false,
                emailSheetError = null
            )
        }
    }

    fun dismissEditEmailSheet() {
        _uiState.update {
            it.copy(
                showEditEmailSheet = false,
                emailSheetError = null
            )
        }
    }

    fun onNewEmailChange(email: String) {
        _uiState.update { it.copy(newEmail = email, emailSheetError = null) }
    }

    fun onCurrentPasswordChange(password: String) {
        _uiState.update { it.copy(currentPassword = password, emailSheetError = null) }
    }

    fun onEmailOtpCodeChange(code: String) {
        _uiState.update { it.copy(emailOtpCode = code, emailSheetError = null) }
    }

    fun requestEmailChange() {
        val state = _uiState.value
        if (state.newEmail.isBlank() || state.currentPassword.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isEmailSubmitting = true, emailSheetError = null) }
            userRepository.requestEmailChange(state.newEmail.trim(), state.currentPassword).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isEmailSubmitting = false,
                            emailChangeStep = EmailChangeStep.VERIFY_CODE,
                            emailSheetError = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isEmailSubmitting = false,
                            emailSheetError = error.message ?: "Failed to send verification code"
                        )
                    }
                }
            )
        }
    }

    fun confirmEmailChange(onSuccess: (String) -> Unit) {
        val state = _uiState.value
        if (state.newEmail.isBlank() || state.emailOtpCode.length != 6) return

        viewModelScope.launch {
            _uiState.update { it.copy(isEmailSubmitting = true, emailSheetError = null) }
            userRepository.confirmEmailChange(state.newEmail.trim(), state.emailOtpCode).fold(
                onSuccess = { updatedProfile ->
                    _uiState.update {
                        it.copy(
                            isEmailSubmitting = false,
                            showEditEmailSheet = false,
                            profile = updatedProfile,
                            emailSheetError = null
                        )
                    }
                    onSuccess(updatedProfile.email)
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isEmailSubmitting = false,
                            emailSheetError = error.message ?: "Invalid or expired verification code"
                        )
                    }
                }
            )
        }
    }
}
