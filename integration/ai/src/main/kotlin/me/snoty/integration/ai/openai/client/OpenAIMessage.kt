package me.snoty.integration.ai.openai.client

import kotlinx.serialization.Serializable

@Serializable
data class OpenAIMessage(
	val role: String,
	val content: String,
)
