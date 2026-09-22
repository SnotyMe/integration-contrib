package me.snoty.contrib.node.ai.openai.client

import kotlinx.serialization.Serializable

@Serializable
data class OpenAIMessage(
	val role: String,
	val content: String,
)
