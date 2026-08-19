package lk.happypaws.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import lk.happypaws.app.data.remote.model.MeProfileResponse
import lk.happypaws.app.data.remote.model.UserProfileResponse
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userContainer: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val dataStore = context.userContainer

    val userProfileFlow: Flow<UserProfileResponse?> = dataStore.data.map { preferences ->
        preferences[USER_PROFILE_KEY]?.let { json ->
            try {
                Json.decodeFromString<UserProfileResponse>(json)
            } catch (e: Exception) {
                null
            }
        }
    }

    val meProfileFlow: Flow<MeProfileResponse?> = dataStore.data.map { preferences ->
        preferences[ME_PROFILE_KEY]?.let { json ->
            try {
                Json.decodeFromString<MeProfileResponse>(json)
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun saveUserProfile(profile: UserProfileResponse) {
        dataStore.edit { preferences ->
            preferences[USER_PROFILE_KEY] = Json.encodeToString(profile)
        }
    }

    suspend fun saveMeProfile(profile: MeProfileResponse) {
        dataStore.edit { preferences ->
            preferences[ME_PROFILE_KEY] = Json.encodeToString(profile)
        }
    }

    suspend fun clearUserData() {
        dataStore.edit { preferences ->
            preferences.remove(USER_PROFILE_KEY)
            preferences.remove(ME_PROFILE_KEY)
        }
    }

    companion object {
        private val USER_PROFILE_KEY = stringPreferencesKey("user_profile_json")
        private val ME_PROFILE_KEY = stringPreferencesKey("me_profile_json")
    }
}
