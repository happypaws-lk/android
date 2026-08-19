package lk.happypaws.app.domain.repository

import kotlinx.coroutines.flow.StateFlow
import lk.happypaws.app.data.remote.model.UserProfileResponse

interface UserRepository {
    fun getCurrentUser(): StateFlow<UserProfileResponse?>
    suspend fun refreshUser()
    suspend fun fetchMeProfile(): Result<lk.happypaws.app.data.remote.model.MeProfileResponse>
}
