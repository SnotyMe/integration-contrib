package me.snoty.contrib.node.ai.openai.client

interface OpenAIClient {
	suspend fun getResponse(prompts: List<OpenAIMessage>): String
}
