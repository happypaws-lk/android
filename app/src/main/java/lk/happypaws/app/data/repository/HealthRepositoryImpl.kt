package lk.happypaws.app.data.repository

import kotlinx.coroutines.withTimeoutOrNull
import lk.happypaws.app.data.remote.api.HealthApi
import lk.happypaws.app.domain.repository.HealthRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class HealthRepositoryImpl @Inject constructor(
    private val healthApi: HealthApi
) : HealthRepository {

    override suspend fun checkHealth(): Boolean {
        return withTimeoutOrNull(4000L) {
            try {
                val response = healthApi.checkHealth()
                response.isSuccessful
            } catch (e: IOException) {
                // Network error (no internet, server down, etc.)
                false
            } catch (e: HttpException) {
                // Server returned non-2xx status code
                false
            } catch (e: Exception) {
                // Any other exception
                false
            }
        } ?: false
    }
}
