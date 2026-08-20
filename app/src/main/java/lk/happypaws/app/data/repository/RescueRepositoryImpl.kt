package lk.happypaws.app.data.repository

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import lk.happypaws.app.data.remote.api.RescueApi
import lk.happypaws.app.data.remote.model.RescueCaseResponse
import lk.happypaws.app.domain.repository.RescueRepository
import lk.happypaws.app.util.Resource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RescueRepositoryImpl @Inject constructor(
    private val api: RescueApi,
    @ApplicationContext private val context: Context
) : RescueRepository {

    override suspend fun createRescue(
        photoUri: Uri,
        title: String,
        description: String,
        conditionNotes: String?,
        tags: List<String>?,
        latitude: Double,
        longitude: Double,
        locationName: String
    ): Resource<RescueCaseResponse> {
        return try {
            val file = getFileFromUri(context, photoUri)
            if (file == null) {
                return Resource.Error("Failed to read photo file")
            }
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val photoPart = MultipartBody.Part.createFormData("photo", file.name, requestFile)
            
            val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val descPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val latPart = latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val lngPart = longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val locNamePart = locationName.toRequestBody("text/plain".toMediaTypeOrNull())
            
            val conditionPart = conditionNotes?.toRequestBody("text/plain".toMediaTypeOrNull())
            val tagsPart = tags?.joinToString(",")?.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = api.createRescue(
                photo = photoPart,
                title = titlePart,
                description = descPart,
                latitude = latPart,
                longitude = lngPart,
                locationName = locNamePart,
                conditionNotes = conditionPart,
                tags = tagsPart
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "An error occurred")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Network error")
        }
    }

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("rescue_photo", ".jpg", context.cacheDir)
            val outputStream = FileOutputStream(tempFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            tempFile
        } catch (e: Exception) {
            null
        }
    }
}
