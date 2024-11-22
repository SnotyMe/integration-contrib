package me.snoty.integration.ai.openai.client

interface OpenAIClient {
	suspend fun getResponse(prompts: List<OpenAIMessage>): String
}
