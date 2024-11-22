package me.snoty.integration.ai.prompt

import me.snoty.backend.config.ConfigLoader
import me.snoty.backend.config.load
import me.snoty.integration.ai.openai.client.OpenAIConfig
import org.koin.core.annotation.Single

data class AIPromptConfig(
	val apiUrl: String,
	val apiKey: String,
	val model: String,
) {
	fun toOpenAIConfig() = OpenAIConfig(
		apiUrl = apiUrl,
		model = model,
		apiKey = apiKey,
	)
}

@Single
fun provideAIPromptConfig(configLoader: ConfigLoader): AIPromptConfig = configLoader.load(prefix = "ai.prompt")
