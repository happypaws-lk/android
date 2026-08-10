package lk.happypaws.app.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lk.happypaws.app.data.local.UserManager
import lk.happypaws.app.data.remote.api.UserApi
import lk.happypaws.app.data.remote.model.UserProfileResponse
import lk.happypaws.app.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val userManager: UserManager
) : UserRepository {

    private val _currentUser = MutableStateFlow<UserProfileResponse?>(null)
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    init {
        repositoryScope.launch {
            userManager.userProfileFlow.collect { profile ->
                _currentUser.value = profile
            }
        }
    }

    override fun getCurrentUser(): StateFlow<UserProfileResponse?> {
        // Trigger background refresh silently
        repositoryScope.launch {
            refreshUser()
        }
        return _currentUser.asStateFlow()
    }

    override suspend fun refreshUser() {
        try {
            val response = userApi.getCurrentUser()
            if (response.isSuccessful && response.body() != null) {
                userManager.saveUserProfile(response.body()!!)
            }
        } catch (_: Exception) {
            // Silently fail or log
        }
    }
}
