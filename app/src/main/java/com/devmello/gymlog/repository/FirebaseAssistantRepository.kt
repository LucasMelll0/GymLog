package com.devmello.gymlog.repository

import android.R.attr.text
import com.devmello.gymlog.core.model.Response
import com.devmello.gymlog.core.model.repositories.AssistantRepository
import com.devmello.gymlog.data.GeneratedTrainingDto
import com.devmello.gymlog.utils.TrainingTypes
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.serialization.json.Json

class FirebaseAssistantRepository : AssistantRepository {

    private val modelName = "gemini-2.5-flash" // TODO implement remote config

    val schema = Schema.obj(
        mapOf(
            "exercises" to Schema.array(
                Schema.obj(
                    mapOf(
                        "title" to Schema.string(),
                        "observations" to Schema.string(nullable = true),
                        "series" to Schema.integer(minimum = 1.0),
                        "repetitions" to Schema.integer(minimum = 1.0),
                        "filters" to Schema.array(Schema.enumeration(TrainingTypes.entries.map { it.name })),
                    ),
                    optionalProperties = listOf("observations", "filters")
                )
            )
        )
    )
    private val aiBackend = Firebase.ai(backend = GenerativeBackend.googleAI())

    private val systemInstruction =
        content { text("Você é um personal trainer experiente. Crie uma lista de exercícios com base no pedido do usuário.") }

    private val model = aiBackend.generativeModel(
        modelName = modelName,
        systemInstruction = systemInstruction,
        generationConfig = generationConfig {
            responseMimeType = "application/json"
            responseSchema = schema
        },
    )


    override suspend fun sendMessage(query: String): Response<GeneratedTrainingDto> {
        return try {
            val response = model.generateContent(query)
            val jsonString =
                response.text ?: return Response.Error("IA retornou uma resposta vazia")

            val dto = Json.decodeFromString<GeneratedTrainingDto>(jsonString)
            return Response.Success(dto)
        } catch (e: Exception) {
            e.printStackTrace()
            Response.Error(e.message)
        }

    }
}