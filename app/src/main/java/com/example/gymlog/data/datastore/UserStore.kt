package com.example.gymlog.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.gymlog.extensions.datastore
import kotlinx.coroutines.flow.map

class UserStore(private val context: Context) {
    companion object {
        val GOOGLE_ID_TOKEN_KEY = stringPreferencesKey("google_id_token")
    }

    val getAccessToken =
        context.datastore.data.map { preferences -> preferences[GOOGLE_ID_TOKEN_KEY] }

    suspend fun saveToken(token: String) {
        context.datastore.edit { userInfo ->
            userInfo[GOOGLE_ID_TOKEN_KEY] = token
        }
    }

    suspend fun cleanToken() {
        context.datastore.edit { userInfo ->
            userInfo[GOOGLE_ID_TOKEN_KEY] = ""
        }
    }
}