package lk.happypaws.app.ui.post

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import lk.happypaws.app.ui.post.model.CommunityPostType
import javax.inject.Inject

data class CreatePostWizardState(
    val selectedPostType: CommunityPostType? = null,
    val title: String = "",
    val description: String = "",
    val locationName: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class CreatePostViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePostWizardState())
    val uiState: StateFlow<CreatePostWizardState> = _uiState.asStateFlow()

    fun selectPostType(postType: CommunityPostType) {
        _uiState.update { it.copy(selectedPostType = postType, errorMessage = null) }
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun updateLocation(locationName: String) {
        _uiState.update { it.copy(locationName = locationName) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun reset() {
        _uiState.value = CreatePostWizardState()
    }
}
