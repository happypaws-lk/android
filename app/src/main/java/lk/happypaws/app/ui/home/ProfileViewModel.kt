package lk.happypaws.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import lk.happypaws.app.data.remote.model.MeProfileResponse
import lk.happypaws.app.domain.repository.AuthRepository
import lk.happypaws.app.domain.repository.UserRepository
import javax.inject.Inject

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val profile: MeProfileResponse) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
    data object Unauthenticated : ProfileUiState
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = kotlinx.coroutines.flow.MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        fetchProfile()
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            userRepository.fetchMeProfile().fold(
                onSuccess = { profile ->
                    _uiState.value = ProfileUiState.Success(profile)
                },
                onFailure = { error ->
                    _uiState.value = ProfileUiState.Error(error.message ?: "Failed to load profile")
                }
            )
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onComplete()
        }
    }
}
