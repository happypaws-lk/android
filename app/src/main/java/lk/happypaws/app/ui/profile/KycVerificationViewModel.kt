package lk.happypaws.app.ui.profile

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lk.happypaws.app.data.remote.model.KycDocumentResponse
import lk.happypaws.app.domain.model.DocumentType
import lk.happypaws.app.domain.repository.UserRepository
import javax.inject.Inject

sealed interface KycUiState {
    data object Loading : KycUiState
    data class UploadForm(
        val selectedType: DocumentType? = null,
        val bytes: ByteArray? = null,
        val bitmap: Bitmap? = null,
        val mime: String = "image/jpeg",
        val isSubmitting: Boolean = false,
        val errorMessage: String? = null
    ) : KycUiState
    data class Pending(val doc: KycDocumentResponse) : KycUiState
    data class Verified(val doc: KycDocumentResponse) : KycUiState
    data class Rejected(val doc: KycDocumentResponse) : KycUiState
}

@HiltViewModel
class KycVerificationViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<KycUiState>(KycUiState.Loading)
    val uiState: StateFlow<KycUiState> = _uiState.asStateFlow()

    init {
        loadKycStatus()
    }

    private fun loadKycStatus() {
        viewModelScope.launch {
            _uiState.value = KycUiState.Loading
            val result = userRepository.getKycDocuments()
            result.fold(
                onSuccess = { docs ->
                    val latest = docs.firstOrNull()
                    _uiState.value = when {
                        latest == null -> KycUiState.UploadForm()
                        latest.status == "Approved" -> KycUiState.Verified(latest)
                        latest.status == "Rejected" -> KycUiState.Rejected(latest)
                        else -> KycUiState.Pending(latest)
                    }
                },
                onFailure = {
                    _uiState.value = KycUiState.UploadForm(errorMessage = it.message)
                }
            )
        }
    }

    fun setDocumentType(type: DocumentType) {
        val current = _uiState.value as? KycUiState.UploadForm ?: return
        _uiState.value = current.copy(selectedType = type)
    }

    fun setDocument(bytes: ByteArray, bitmap: Bitmap, mime: String) {
        val current = _uiState.value as? KycUiState.UploadForm ?: return
        _uiState.value = current.copy(bytes = bytes, bitmap = bitmap, mime = mime)
    }

    fun clearDocument() {
        val current = _uiState.value as? KycUiState.UploadForm ?: return
        _uiState.value = current.copy(bytes = null, bitmap = null)
    }

    fun retryUpload() {
        _uiState.value = KycUiState.UploadForm()
    }

    fun submit() {
        val current = _uiState.value as? KycUiState.UploadForm ?: return
        val type = current.selectedType ?: return
        val bytes = current.bytes ?: return

        viewModelScope.launch {
            _uiState.value = current.copy(isSubmitting = true, errorMessage = null)
            val ext = if (current.mime.contains("png")) "png" else "jpg"
            val result = userRepository.uploadKycDocument(
                documentType = type,
                bytes = bytes,
                filename = "kyc.$ext",
                mimeType = current.mime
            )
            result.fold(
                onSuccess = { doc ->
                    _uiState.value = KycUiState.Pending(doc)
                },
                onFailure = { error ->
                    _uiState.value = current.copy(isSubmitting = false, errorMessage = error.message)
                }
            )
        }
    }
}
