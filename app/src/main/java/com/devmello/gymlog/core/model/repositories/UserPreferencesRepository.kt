package com.devmello.gymlog.core.model.repositories

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val googleIdToken: Flow<String?>
    suspend fun saveToken(token: String)
    suspend fun clearToken()
}