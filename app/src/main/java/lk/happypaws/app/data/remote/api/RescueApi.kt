package lk.happypaws.app.data.remote.api

import lk.happypaws.app.data.remote.model.RescueCaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface RescueApi {
    @Multipart
    @POST("api/v1/rescues")
    suspend fun createRescue(
        @Part photo: MultipartBody.Part,
        @Part("Title") title: RequestBody,
        @Part("Tags") tags: RequestBody?,
        @Part("Latitude") latitude: RequestBody,
        @Part("Longitude") longitude: RequestBody,
        @Part("LocationName") locationName: RequestBody,
        @Part("Description") description: RequestBody,
        @Part("ConditionNotes") conditionNotes: RequestBody?
    ): Response<RescueCaseResponse>
}
