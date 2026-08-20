package lk.happypaws.app.ui.post.rescue

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lk.happypaws.app.data.remote.model.RescueCaseResponse
import lk.happypaws.app.domain.repository.RescueRepository
import lk.happypaws.app.util.Resource
import javax.inject.Inject

data class CreateRescueState(
    val photoUri: Uri? = null,
    val title: String = "",
    val description: String = "",
    val conditionNotes: String = "",
    val tags: List<String> = emptyList(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationName: String = "",
    val isLoading: Boolean = false,
    val successResponse: RescueCaseResponse? = null,
    val error: String? = null
)

@HiltViewModel
class CreateRescueViewModel @Inject constructor(
    private val repository: RescueRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateRescueState())
    val uiState: StateFlow<CreateRescueState> = _uiState.asStateFlow()

    fun updatePhoto(uri: Uri) {
        _uiState.update { it.copy(photoUri = uri, error = null) }
    }

    fun updateLocation(lat: Double, lng: Double, name: String) {
        _uiState.update { it.copy(latitude = lat, longitude = lng, locationName = name) }
    }

    fun updateDetails(title: String, description: String, conditionNotes: String) {
        _uiState.update { it.copy(title = title, description = description, conditionNotes = conditionNotes) }
    }

    fun toggleTag(tag: String) {
        _uiState.update { state ->
            val newTags = if (state.tags.contains(tag)) {
                state.tags - tag
            } else {
                state.tags + tag
            }
            state.copy(tags = newTags)
        }
    }

    fun submitRescueCase() {
        val state = _uiState.value
        if (state.photoUri == null) {
            _uiState.update { it.copy(error = "Photo is required") }
            return
        }
        if (state.latitude == null || state.longitude == null || state.locationName.isBlank()) {
            _uiState.update { it.copy(error = "Location is required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.createRescue(
                photoUri = state.photoUri,
                title = state.title.ifBlank { "Emergency Rescue" },
                description = state.description,
                conditionNotes = state.conditionNotes.takeIf { it.isNotBlank() },
                tags = state.tags.takeIf { it.isNotEmpty() },
                latitude = state.latitude,
                longitude = state.longitude,
                locationName = state.locationName
            )
            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, successResponse = result.data) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                else -> {
                    _uiState.update { it.copy(isLoading = false, error = "Unknown error") }
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = CreateRescueState()
    }
}
