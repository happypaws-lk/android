package lk.happypaws.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lk.happypaws.app.data.remote.model.MeProfileResponse
import lk.happypaws.app.domain.repository.HealthRepository
import lk.happypaws.app.domain.repository.UserRepository
import javax.inject.Inject

enum class ConnectionState {
    LOADING,
    CONNECTED,
    ERROR
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val healthRepository: HealthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _connectionState = MutableStateFlow(ConnectionState.LOADING)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    val meProfile: StateFlow<MeProfileResponse?> = userRepository.getMeProfileStream()

    fun checkConnectivity() {
        viewModelScope.launch {
            _connectionState.value = ConnectionState.LOADING
            val isHealthy = healthRepository.checkHealth()
            _connectionState.value = if (isHealthy) ConnectionState.CONNECTED else ConnectionState.ERROR
        }
    }
}
