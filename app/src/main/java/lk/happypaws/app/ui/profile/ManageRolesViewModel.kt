package lk.happypaws.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lk.happypaws.app.data.remote.model.RoleRequestResponse
import lk.happypaws.app.data.remote.model.RoleResponse
import lk.happypaws.app.domain.repository.UserRepository
import javax.inject.Inject

data class ManageRolesUiState(
    val isLoading: Boolean = true,
    val activeRoles: List<RoleResponse> = emptyList(),
    val roleRequests: List<RoleRequestResponse> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class ManageRolesViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageRolesUiState())
    val uiState: StateFlow<ManageRolesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.getMeProfileStream().collect { profile ->
                _uiState.update { it.copy(activeRoles = profile?.roles ?: emptyList()) }
            }
        }
        loadRoleRequests()
    }

    private fun loadRoleRequests() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = userRepository.getRoleRequests()
            result.fold(
                onSuccess = { requests ->
                    _uiState.update { it.copy(isLoading = false, roleRequests = requests) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    fun refresh() {
        viewModelScope.launch {
            userRepository.fetchMeProfile()
        }
        loadRoleRequests()
    }
}
