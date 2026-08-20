package lk.happypaws.app.ui.profile

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import lk.happypaws.app.domain.model.DocumentType
import lk.happypaws.app.domain.model.UserRole
import lk.happypaws.app.domain.repository.UserRepository
import javax.inject.Inject

data class RequestRoleUiState(
    val role: UserRole = UserRole.FOSTER,
    val availableDocumentTypes: List<DocumentType> = emptyList(),
    val selectedDocumentType: DocumentType? = null,
    val selectedDocumentBytes: ByteArray? = null,
    val selectedDocumentBitmap: Bitmap? = null,
    val selectedDocumentMime: String = "image/jpeg",
    val justification: String = "",
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class RequestRoleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RequestRoleUiState())
    val uiState: StateFlow<RequestRoleUiState> = _uiState.asStateFlow()

    init {
        val roleValue = savedStateHandle.get<Int>("roleValue") ?: UserRole.FOSTER.value
        val role = UserRole.entries.firstOrNull { it.value == roleValue } ?: UserRole.FOSTER
        val types = DocumentType.validFor(role)
        _uiState.update {
            it.copy(
                role = role,
                availableDocumentTypes = types,
                selectedDocumentType = if (types.size == 1) types.first() else null
            )
        }
    }

    fun setJustification(text: String) {
        _uiState.update { it.copy(justification = text) }
    }

    fun setDocumentType(type: DocumentType) {
        _uiState.update { it.copy(selectedDocumentType = type) }
    }

    fun setDocument(bytes: ByteArray, bitmap: Bitmap, mime: String) {
        _uiState.update { it.copy(selectedDocumentBytes = bytes, selectedDocumentBitmap = bitmap, selectedDocumentMime = mime) }
    }

    fun clearDocument() {
        _uiState.update { it.copy(selectedDocumentBytes = null, selectedDocumentBitmap = null) }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun submit() {
        val state = _uiState.value
        val type = state.selectedDocumentType ?: return
        val bytes = state.selectedDocumentBytes ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val ext = if (state.selectedDocumentMime.contains("png")) "png" else "jpg"
            val result = userRepository.submitRoleRequest(
                role = state.role,
                documentType = type,
                justification = state.justification.trim().ifEmpty { null },
                bytes = bytes,
                filename = "role-request.$ext",
                mimeType = state.selectedDocumentMime
            )
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isSubmitting = false, isSuccess = true) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isSubmitting = false, errorMessage = error.message) }
                }
            )
        }
    }
}
