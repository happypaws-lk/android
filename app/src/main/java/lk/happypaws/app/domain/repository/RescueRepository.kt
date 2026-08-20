package lk.happypaws.app.domain.repository

import android.net.Uri
import lk.happypaws.app.data.remote.model.RescueCaseResponse
import lk.happypaws.app.util.Resource

interface RescueRepository {
    suspend fun createRescue(
        photoUri: Uri,
        title: String,
        description: String,
        conditionNotes: String?,
        tags: List<String>?,
        latitude: Double,
        longitude: Double,
        locationName: String
    ): Resource<RescueCaseResponse>
}
