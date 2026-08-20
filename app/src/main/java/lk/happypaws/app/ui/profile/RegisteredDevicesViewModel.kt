package lk.happypaws.app.ui.profile

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lk.happypaws.app.data.local.TokenManager
import lk.happypaws.app.data.remote.model.DeviceResponse
import lk.happypaws.app.domain.repository.UserRepository
import javax.inject.Inject

sealed class RegisteredDevicesUiState {
    data object Loading : RegisteredDevicesUiState()
    data class Success(val devices: List<DeviceResponse>) : RegisteredDevicesUiState()
    data class Error(val message: String) : RegisteredDevicesUiState()
}

@HiltViewModel
class RegisteredDevicesViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisteredDevicesUiState>(RegisteredDevicesUiState.Loading)
    val uiState: StateFlow<RegisteredDevicesUiState> = _uiState.asStateFlow()

    private val _isRemoving = MutableStateFlow(false)
    val isRemoving: StateFlow<Boolean> = _isRemoving.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.registerDevice(
                fcmToken = tokenManager.getInstallationId(),
                deviceName = Build.MODEL
            )
            fetchDevices()
        }
    }

    fun fetchDevices() {
        viewModelScope.launch {
            _uiState.value = RegisteredDevicesUiState.Loading
            userRepository.getDevices().fold(
                onSuccess = { devices ->
                    val sorted = devices.sortedByDescending { it.isCurrent }
                    _uiState.value = RegisteredDevicesUiState.Success(sorted)
                },
                onFailure = { error ->
                    _uiState.value = RegisteredDevicesUiState.Error(error.message ?: "Failed to fetch devices")
                }
            )
        }
    }

    fun removeDevice(id: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isRemoving.value = true
            userRepository.removeDevice(id).fold(
                onSuccess = {
                    fetchDevices()
                    onComplete(true, null)
                },
                onFailure = { error ->
                    onComplete(false, error.message)
                }
            )
            _isRemoving.value = false
        }
    }
}