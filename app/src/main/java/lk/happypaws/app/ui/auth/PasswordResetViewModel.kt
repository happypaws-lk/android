package lk.happypaws.app.ui.auth

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

data class PasswordResetUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val email: String = "",
    val isEmailValid: Boolean = false,
    val otp: String = "",
    val isOtpError: Boolean = false,
    val resetToken: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isPasswordValid: Boolean = false,
    val isResetSuccess: Boolean = false
)

@HiltViewModel
class PasswordResetViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PasswordResetUiState())
    val uiState: StateFlow<PasswordResetUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        val isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        _uiState.update { it.copy(email = email, isEmailValid = isValid, error = null) }
    }

    fun requestOtp(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.forgotPassword(_uiState.value.email)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess(_uiState.value.email)
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun onOtpChange(otp: String) {
        _uiState.update { it.copy(otp = otp, isOtpError = false, error = null) }
    }

    fun verifyOtp(onSuccess: (String, String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.verifyResetCode(_uiState.value.email, _uiState.value.otp)
                .onSuccess { token ->
                    _uiState.update { it.copy(isLoading = false, resetToken = token) }
                    onSuccess(_uiState.value.email, token)
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, isOtpError = true, error = e.message) }
                }
        }
    }

    fun onPasswordChange(password: String) {
        val isValid = validatePassword(password)
        _uiState.update { it.copy(newPassword = password, isPasswordValid = isValid, error = null) }
    }

    fun onConfirmPasswordChange(password: String) {
        _uiState.update { it.copy(confirmPassword = password, error = null) }
    }

    private fun validatePassword(password: String): Boolean {
        if (password.length < 8) return false
        if (!password.any { it.isUpperCase() }) return false
        if (!password.any { it.isLowerCase() }) return false
        if (!password.any { it.isDigit() }) return false
        return true
    }

    fun resetPassword(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.newPassword != state.confirmPassword) {
            _uiState.update { it.copy(error = "Passwords do not match") }
            return
        }
        if (!state.isPasswordValid) {
            _uiState.update { it.copy(error = "Password must contain at least 8 characters, 1 uppercase, 1 lowercase, and 1 number") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.resetPassword(state.email, state.resetToken, state.newPassword)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isResetSuccess = true) }
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun setEmailAndToken(email: String, token: String) {
        _uiState.update { it.copy(email = email, resetToken = token) }
    }
    
    fun setEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }
}
