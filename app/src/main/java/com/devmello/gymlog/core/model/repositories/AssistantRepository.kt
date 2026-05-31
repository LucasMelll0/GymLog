package com.devmello.gymlog.core.model.repositories

import com.devmello.gymlog.core.model.Response
import com.devmello.gymlog.data.GeneratedTrainingDto

interface AssistantRepository {
    suspend fun sendMessage(query: String): Response<GeneratedTrainingDto>
}