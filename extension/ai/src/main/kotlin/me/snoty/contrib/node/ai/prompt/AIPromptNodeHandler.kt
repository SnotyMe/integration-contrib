package me.snoty.contrib.node.ai.prompt

import io.ktor.client.*
import kotlinx.serialization.Serializable
import me.snoty.backend.schema.FieldDefaultValue
import me.snoty.backend.wiring.data.NodeInput
import me.snoty.backend.wiring.data.NodeOutput
import me.snoty.backend.wiring.data.get
import me.snoty.backend.wiring.data.iterableStructOutput
import me.snoty.backend.wiring.node.*
import me.snoty.backend.wiring.node.metadata.NodeStereotype
import me.snoty.contrib.node.ai.openai.client.OpenAIClient
import me.snoty.contrib.node.ai.openai.client.OpenAIClientImpl
import me.snoty.contrib.node.ai.openai.client.OpenAIMessage
import me.snoty.contrib.node.ai.openai.client.OpenAIRoles
import me.snoty.core.node.NodeWithSettings
import org.bson.Document
import org.koin.core.annotation.Single

@Serializable
data class AIPromptSettings(
	val systemPrompt: String,
	@FieldDefaultValue("userPrompt")
	val inputKey: String = "userPrompt",
	@FieldDefaultValue("aiResponse")
	val outputKey: String = "aiResponse",
) : NodeSettings

data class AIPromptInput(
	val userPrompt: String,
)

data class AIPromptOutput(
	val aiResponse: String,
)

@RegisterNode(
	name = "ai_prompt",
	displayName = "AI Prompt",
	stereotype = NodeStereotype.MIDDLE,
	settingsType = AIPromptSettings::class,
	inputType = AIPromptInput::class,
	outputType = AIPromptOutput::class,
)
@Single
class AIPromptNodeHandler(
	config: AIPromptConfig,
	httpClient: HttpClient,
	private val openAIClient: OpenAIClient = OpenAIClientImpl(config.toOpenAIConfig(), httpClient)
) : NodeHandler {
	context(ctx: NodeHandleContext)
	override suspend fun process(node: NodeWithSettings, input: NodeInput): NodeOutput {
		val settings = node.settings as AIPromptSettings

		return iterableStructOutput(input.map { raw ->
			val doc: Document = raw.get()

			val prompts = listOf(
				OpenAIMessage(
					role = OpenAIRoles.SYSTEM,
					content = settings.systemPrompt
				),
				OpenAIMessage(
					role = OpenAIRoles.USER,
					content = doc.getString(settings.inputKey)
				)
			)

			logger.info("Sending prompts to AI: $prompts")
			val response = openAIClient.getResponse(prompts)
			logger.info("AI responded with: $response")

			doc[settings.outputKey] = response
			doc
		})
	}
}
