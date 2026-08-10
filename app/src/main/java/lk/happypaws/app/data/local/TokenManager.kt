package lk.happypaws.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")

@Singleton
class TokenManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val dataStore = context.dataStore

    val accessTokenFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_KEY]
    }

    val refreshTokenFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN_KEY]
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    suspend fun saveAuthDetails(userId: String, role: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_ROLE_KEY] = role
        }
    }

    fun getUserIdSync(): String? = runBlocking {
        dataStore.data.map { it[USER_ID_KEY] }.first()
    }

    fun getUserRoleSync(): String? = runBlocking {
        dataStore.data.map { it[USER_ROLE_KEY] }.first()
    }

    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
            preferences.remove(USER_ROLE_KEY)
            preferences.remove(ONBOARDING_COMPLETED_KEY)
        }
    }

    suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
        }
    }

    // Synchronous helper methods for OkHttp Interceptors/Authenticators
    fun getAccessTokenSync(): String? = runBlocking {
        dataStore.data.map { it[ACCESS_TOKEN_KEY] }.first()
    }

    fun getRefreshTokenSync(): String? = runBlocking {
        dataStore.data.map { it[REFRESH_TOKEN_KEY] }.first()
    }

    fun saveTokensSync(accessToken: String, refreshToken: String) = runBlocking {
        saveTokens(accessToken, refreshToken)
    }

    fun clearTokensSync() = runBlocking {
        clearTokens()
    }

    // Legacy support for migration
    @Deprecated("Use getAccessTokenSync", ReplaceWith("getAccessTokenSync()"))
    fun getAccessToken(): String? = getAccessTokenSync()

    @Deprecated("Use getRefreshTokenSync", ReplaceWith("getRefreshTokenSync()"))
    fun getRefreshToken(): String? = getRefreshTokenSync()

    @Deprecated("Use getAccessTokenSync", ReplaceWith("getAccessTokenSync()"))
    fun getToken(): String? = getAccessTokenSync()

    @Deprecated("Use clearTokensSync", ReplaceWith("clearTokensSync()"))
    fun clearToken() = clearTokensSync()

    val onboardingCompletedFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[ONBOARDING_COMPLETED_KEY] ?: false
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] = completed
        }
    }

    fun hasCompletedOnboardingSync(): Boolean = runBlocking {
        dataStore.data.map { it[ONBOARDING_COMPLETED_KEY] ?: false }.first()
    }

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_ROLE_KEY = stringPreferencesKey("user_role")
        private val ONBOARDING_COMPLETED_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("onboarding_completed")
    }
}
