package lk.happypaws.app.domain.repository

interface HealthRepository {
    suspend fun checkHealth(): Boolean
}
