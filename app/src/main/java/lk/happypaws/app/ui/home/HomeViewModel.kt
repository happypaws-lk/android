package lk.happypaws.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import lk.happypaws.app.data.local.TokenManager
import lk.happypaws.app.data.remote.model.UserProfileResponse
import lk.happypaws.app.domain.repository.AuthRepository
import lk.happypaws.app.domain.repository.UserRepository
import javax.inject.Inject

data class HomeUiState(
    val userProfile: UserProfileResponse? = null,
    val role: String = "",
    val isLoading: Boolean = false
)

sealed interface HomeUiEvent {
    object LogoutSuccess : HomeUiEvent
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _events = MutableSharedFlow<HomeUiEvent>()
    val events: SharedFlow<HomeUiEvent> = _events.asSharedFlow()

    val uiState: StateFlow<HomeUiState> = userRepository.getCurrentUser().map { profile ->
        HomeUiState(
            userProfile = profile,
            role = tokenManager.getUserRoleSync() ?: ""
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _events.emit(HomeUiEvent.LogoutSuccess)
        }
    }
}
