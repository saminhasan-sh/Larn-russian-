package com.example.data.user

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_account_prefs")

data class GoogleUserProfile(
    val isLoggedIn: Boolean = false,
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val syncEnabled: Boolean = true,
    val lastSyncTime: String = "Just now"
)

class UserAccountManager(private val context: Context) {

    private object PrefKeys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val EMAIL = stringPreferencesKey("email")
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val PHOTO_URL = stringPreferencesKey("photo_url")
        val LAST_SYNC = stringPreferencesKey("last_sync")
    }

    val userProfileFlow: Flow<GoogleUserProfile> = context.dataStore.data.map { prefs ->
        val isLoggedIn = prefs[PrefKeys.IS_LOGGED_IN] ?: false
        GoogleUserProfile(
            isLoggedIn = isLoggedIn,
            email = prefs[PrefKeys.EMAIL] ?: if (isLoggedIn) "saminhasan1234567890@gmail.com" else "",
            displayName = prefs[PrefKeys.DISPLAY_NAME] ?: if (isLoggedIn) "Samin Hasan" else "",
            photoUrl = prefs[PrefKeys.PHOTO_URL] ?: "",
            syncEnabled = true,
            lastSyncTime = prefs[PrefKeys.LAST_SYNC] ?: "Connected & Synced"
        )
    }

    suspend fun signInWithGoogle(email: String = "saminhasan1234567890@gmail.com", displayName: String = "Samin Hasan") {
        context.dataStore.edit { prefs ->
            prefs[PrefKeys.IS_LOGGED_IN] = true
            prefs[PrefKeys.EMAIL] = email.ifEmpty { "saminhasan1234567890@gmail.com" }
            prefs[PrefKeys.DISPLAY_NAME] = displayName.ifEmpty { "Samin Hasan" }
            prefs[PrefKeys.LAST_SYNC] = "Synced just now"
        }
    }

    suspend fun signOut() {
        context.dataStore.edit { prefs ->
            prefs[PrefKeys.IS_LOGGED_IN] = false
            prefs[PrefKeys.EMAIL] = ""
            prefs[PrefKeys.DISPLAY_NAME] = ""
        }
    }
}
