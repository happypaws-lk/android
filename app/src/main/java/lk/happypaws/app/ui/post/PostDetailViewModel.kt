package lk.happypaws.app.ui.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lk.happypaws.app.ui.home.CommunityRepository
import lk.happypaws.app.ui.post.model.CommunityPost
import javax.inject.Inject

sealed class PostDetailUiState {
    data object Loading : PostDetailUiState()
    data class Success(val post: CommunityPost) : PostDetailUiState()
    data class Error(val message: String) : PostDetailUiState()
}

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val repository: CommunityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PostDetailUiState>(PostDetailUiState.Loading)
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    fun loadPost(type: String, id: String) {
        viewModelScope.launch {
            _uiState.value = PostDetailUiState.Loading
            try {
                val post = repository.getPostById(type, id)
                _uiState.value = PostDetailUiState.Success(post)
            } catch (e: Exception) {
                _uiState.value = PostDetailUiState.Error(e.message ?: "Failed to load post")
            }
        }
    }

    fun toggleUpvote(type: String, id: String) {
        viewModelScope.launch {
            try {
                val response = repository.toggleUpvote(type, id)
                val currentState = _uiState.value
                if (currentState is PostDetailUiState.Success) {
                    _uiState.value = PostDetailUiState.Success(
                        currentState.post.copy(
                            upvotes = response.upvotes,
                            isUpvotedByMe = response.isUpvotedByMe
                        )
                    )
                }
            } catch (e: Exception) {
                // Ignore or show error
            }
        }
    }
    
    fun deletePost(type: String, id: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.deletePost(type, id)
                onSuccess()
            } catch (e: Exception) {
                // Ignore or show error
            }
        }
    }
}
