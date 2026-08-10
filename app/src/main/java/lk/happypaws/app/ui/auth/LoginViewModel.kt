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
import android.content.Context

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isEmailError: Boolean = false,
    val isPasswordError: Boolean = false,
    val isSuccess: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private var hasAutoSignedIn = false

    fun autoSignIn(context: Context, onCredentialsLoaded: (String, String) -> Unit) {
        if (hasAutoSignedIn) return
        hasAutoSignedIn = true
        
        viewModelScope.launch {
            authRepository.getSavedCredentials(context).onSuccess { (email, password) ->
                onCredentialsLoaded(email, password)
                login(email, password, context)
            }
        }
    }

    fun login(email: String, passwordHash: String, context: Context? = null) {
        val trimmedEmail = email.trim()
        val trimmedPassword = passwordHash.trim()

        _uiState.update { 
            it.copy(
                emailError = null, 
                passwordError = null,
                isEmailError = false,
                isPasswordError = false,
                error = null 
            ) 
        }

        if (trimmedEmail.isEmpty() && trimmedPassword.isEmpty()) {
            _uiState.update { 
                it.copy(
                    isEmailError = true,
                    isPasswordError = true,
                    passwordError = "Please enter your email and password."
                ) 
            }
            return
        }

        if (trimmedEmail.isEmpty()) {
            _uiState.update { 
                it.copy(
                    isEmailError = true,
                    emailError = "Please enter your email address."
                ) 
            }
            return
        }

        if (trimmedPassword.isEmpty()) {
            _uiState.update { 
                it.copy(
                    isPasswordError = true,
                    passwordError = "Please enter your password."
                ) 
            }
            return
        }

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result = authRepository.login(trimmedEmail, trimmedPassword)
            if (result.isSuccess) {
                if (context != null) {
                    authRepository.saveCredentials(trimmedEmail, trimmedPassword, context)
                }
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Login failed"
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isEmailError = true,
                        isPasswordError = true,
                        passwordError = errorMessage
                    )
                }
            }
        }
    }
    
    fun errorDismissed() {
        _uiState.update { it.copy(error = null) }
    }
}
