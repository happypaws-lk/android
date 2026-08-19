package lk.happypaws.app.data.remote.api

import lk.happypaws.app.data.remote.model.AvatarUploadResponse
import lk.happypaws.app.data.remote.model.MeProfileResponse
import lk.happypaws.app.data.remote.model.UpdateMeProfileRequest
import lk.happypaws.app.data.remote.model.UserProfileResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface UserApi {

    @GET("api/v1/users/me")
    suspend fun getCurrentUser(): Response<UserProfileResponse>

    @GET("api/v1/users/me/profile")
    suspend fun getMeProfile(): Response<MeProfileResponse>

    @PUT("api/v1/users/me/profile")
    suspend fun updateMeProfile(@Body request: UpdateMeProfileRequest): Response<MeProfileResponse>

    @Multipart
    @POST("api/v1/users/me/avatar")
    suspend fun uploadAvatar(@Part avatar: MultipartBody.Part): Response<AvatarUploadResponse>

    @DELETE("api/v1/users/me/avatar")
    suspend fun deleteAvatar(): Response<Unit>

}
