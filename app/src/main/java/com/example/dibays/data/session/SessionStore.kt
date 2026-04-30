package com.example.dibays.data.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.authDataStore by preferencesDataStore(name = "auth_session")

class SessionStore(private val context: Context) {
    private val dataStore = context.authDataStore

    val sessionFlow: Flow<AuthSession?> = dataStore.data.map { prefs ->
        val accessToken = prefs[ACCESS_TOKEN] ?: ""
        val refreshToken = prefs[REFRESH_TOKEN] ?: ""
        val userId = prefs[USER_ID] ?: ""
        val email = prefs[EMAIL] ?: ""
        if (accessToken.isBlank()) {
            null
        } else {
            AuthSession(
                accessToken = accessToken,
                refreshToken = refreshToken,
                userId = userId,
                email = email,
            )
        }
    }

    suspend fun save(session: AuthSession) {
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = session.accessToken
            prefs[REFRESH_TOKEN] = session.refreshToken
            prefs[USER_ID] = session.userId
            prefs[EMAIL] = session.email
        }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }

    private companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
        val EMAIL = stringPreferencesKey("email")
    }
}
