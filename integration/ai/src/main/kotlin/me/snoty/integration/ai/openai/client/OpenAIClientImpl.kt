package me.snoty.integration.ai.openai.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Single
import kotlin.time.Duration.Companion.seconds

@Single
class OpenAIClientImpl(private val aiConfig: OpenAIConfig, client: HttpClient) : OpenAIClient {
	private val httpClient = client.config {
		install(HttpTimeout)
	}

	@Serializable
	data class CompletionRequest(val model: String, val messages: List<OpenAIMessage>)

	@Serializable
	data class CompletionResponse(val choices: List<Choice>)
	@Serializable
	data class Choice(val message: OpenAIMessage)

	override suspend fun getResponse(prompts: List<OpenAIMessage>) = httpClient.post(aiConfig.apiUrl) {
		bearerAuth(aiConfig.apiKey)
		contentType(ContentType.Application.Json)
		setBody(CompletionRequest(model = aiConfig.model, messages = prompts))
		timeout {
			connectTimeoutMillis = 20.seconds.inWholeMilliseconds
			requestTimeoutMillis = 30.seconds.inWholeMilliseconds
			socketTimeoutMillis = 30.seconds.inWholeMilliseconds
		}
	}.body<CompletionResponse>().choices.single().message.content
}
