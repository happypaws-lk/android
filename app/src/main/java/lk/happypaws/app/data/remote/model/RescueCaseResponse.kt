package lk.happypaws.app.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class RescueCaseResponse(
    val id: String,
    val reporterId: String,
    val reporterName: String,
    val assignedFosterId: String?,
    val assignedFosterName: String?,
    val title: String? = null,
    val tags: List<String> = emptyList(),
    val latitude: Double,
    val longitude: Double,
    val locationName: String,
    val description: String,
    val photoUrl: String,
    val conditionNotes: String?,
    val urgency: String,
    val originalAiUrgency: String?,
    val urgencySource: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)
