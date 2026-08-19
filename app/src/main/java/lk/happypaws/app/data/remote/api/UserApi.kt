package lk.happypaws.app.data.remote.api

import lk.happypaws.app.data.remote.model.UserProfileResponse
import retrofit2.Response
import retrofit2.http.GET

interface UserApi {

    @GET("api/v1/users/me")
    suspend fun getCurrentUser(): Response<UserProfileResponse>

    @GET("api/v1/users/me/profile")
    suspend fun getMeProfile(): Response<lk.happypaws.app.data.remote.model.MeProfileResponse>

}
