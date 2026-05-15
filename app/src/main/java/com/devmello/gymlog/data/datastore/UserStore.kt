package com.devmello.gymlog.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.devmello.gymlog.core.model.repositories.UserPreferencesRepository
import com.devmello.gymlog.extensions.datastore
import kotlinx.coroutines.flow.map

class UserStore(private val context: Context) : UserPreferencesRepository {
    private object PreferencesKeys {
        val GOOGLE_ID_TOKEN_KEY = stringPreferencesKey("google_id_token")
    }

    override val googleIdToken =
        context.datastore.data.map { preferences -> preferences[PreferencesKeys.GOOGLE_ID_TOKEN_KEY] }

    override suspend fun saveToken(token: String) {
        context.datastore.edit { userInfo ->
            userInfo[PreferencesKeys.GOOGLE_ID_TOKEN_KEY] = token
        }
    }

    override suspend fun clearToken() {
        context.datastore.edit { preferences ->
            preferences.remove(PreferencesKeys.GOOGLE_ID_TOKEN_KEY)
        }
    }
}