package lk.happypaws.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lk.happypaws.app.domain.repository.AuthRepository
import javax.inject.Inject

data class ChangePasswordUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val currentPasswordError: String? = null,
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null,
    val serverError: String? = null,
    val isSaving: Boolean = false
)

private val PASSWORD_REGEX = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    fun onCurrentPasswordChange(value: String) {
        val error = if (value.isNotEmpty() && value.isBlank()) "Password cannot be blank" else null
        _uiState.update { it.copy(currentPassword = value, currentPasswordError = error, serverError = null) }
    }

    fun onNewPasswordChange(value: String) {
        val error = if (value.isNotEmpty() && !PASSWORD_REGEX.matches(value)) {
            "Must be 8+ characters with uppercase, lowercase, and a number"
        } else null
        val confirmError = if (_uiState.value.confirmPassword.isNotEmpty() && _uiState.value.confirmPassword != value) {
            "Passwords do not match"
        } else null
        _uiState.update { it.copy(newPassword = value, newPasswordError = error, confirmPasswordError = confirmError, serverError = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        val error = if (value.isNotEmpty() && value != _uiState.value.newPassword) "Passwords do not match" else null
        _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = error, serverError = null) }
    }

    fun isFormValid(state: ChangePasswordUiState): Boolean =
        state.currentPassword.isNotBlank()
            && PASSWORD_REGEX.matches(state.newPassword)
            && state.confirmPassword == state.newPassword

    fun changePassword(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.currentPassword.isBlank()) {
            _uiState.update { it.copy(currentPasswordError = "Enter your current password") }
            return
        }
        if (!PASSWORD_REGEX.matches(state.newPassword)) {
            _uiState.update { it.copy(newPasswordError = "Must be 8+ characters with uppercase, lowercase, and a number") }
            return
        }
        if (state.newPassword != state.confirmPassword) {
            _uiState.update { it.copy(confirmPasswordError = "Passwords do not match") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, serverError = null) }
            authRepository.changePassword(state.currentPassword, state.newPassword).fold(
                onSuccess = {
                    _uiState.update { it.copy(isSaving = false) }
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isSaving = false, serverError = error.message ?: "Failed to update password") }
                }
            )
        }
    }
}
