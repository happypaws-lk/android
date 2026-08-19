package lk.happypaws.app.data.remote.api

import retrofit2.Response
import retrofit2.http.GET

interface HealthApi {
    @GET("/health")
    suspend fun checkHealth(): Response<Unit>
}
