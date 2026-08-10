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

data class SignUpUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val email: String = "",
    val isEmailValid: Boolean = false,
    val isOtpSent: Boolean = false,
    val otp: String = "",
    val isOtpError: Boolean = false,
    val isOtpVerified: Boolean = false,
    val signupToken: String = "",
    val name: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isRegistrationSuccess: Boolean = false
)

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        val isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        _uiState.update { it.copy(email = email, isEmailValid = isValid, error = null) }
    }

    fun sendOtp(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val email = _uiState.value.email
            authRepository.sendOtp(email)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isOtpSent = true) }
                    onSuccess(email)
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun onOtpChange(otp: String) {
        _uiState.update { it.copy(otp = otp, isOtpError = false, error = null) }
    }

    fun verifyOtp(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.verifySignUpCode(_uiState.value.email, _uiState.value.otp)
                .onSuccess { token ->
                    _uiState.update { it.copy(isLoading = false, isOtpVerified = true, signupToken = token) }
                    onSuccess(token)
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, isOtpError = true, error = e.message) }
                }
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, error = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun onConfirmPasswordChange(password: String) {
        _uiState.update { it.copy(confirmPassword = password, error = null) }
    }

    fun register(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(error = "Passwords do not match") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.completeSignUp(
                signupToken = state.signupToken,
                name = state.name,
                passwordHash = state.password,
                role = "Adopter"
            ).onSuccess {
                _uiState.update { it.copy(isLoading = false, isRegistrationSuccess = true) }
                onSuccess()
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun setEmailAndToken(email: String, signupToken: String) {
        _uiState.update { it.copy(email = email, signupToken = signupToken) }
    }

    fun setEmail(email: String) {
        if (_uiState.value.email.isEmpty()) {
            _uiState.update { it.copy(email = email) }
        }
    }
}
