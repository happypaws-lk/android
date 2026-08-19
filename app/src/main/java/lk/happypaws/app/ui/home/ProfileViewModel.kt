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

    val uiState: StateFlow<ProfileUiState> = userRepository.getMeProfileStream().map { profile ->
        if (profile != null) {
            ProfileUiState.Success(profile)
        } else {
            ProfileUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState.Loading
    )

    fun refreshProfile() {
        viewModelScope.launch {
            userRepository.fetchMeProfile()
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onComplete()
        }
    }
}
