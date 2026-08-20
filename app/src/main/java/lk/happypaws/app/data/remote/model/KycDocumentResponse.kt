package lk.happypaws.app.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class KycDocumentResponse(
    val id: String,
    val documentType: String,
    val status: String,
    val rejectionReason: String? = null,
    val uploadedAt: String,
    val reviewedAt: String? = null
)
