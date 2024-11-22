package me.snoty.integration.ai.prompt

import io.ktor.client.*
import kotlinx.serialization.Serializable
import me.snoty.integration.ai.AI
import me.snoty.integration.ai.openai.client.OpenAIClient
import me.snoty.integration.ai.openai.client.OpenAIClientImpl
import me.snoty.integration.ai.openai.client.OpenAIMessage
import me.snoty.integration.ai.openai.client.OpenAIRoles
import me.snoty.integration.common.annotation.RegisterNode
import me.snoty.integration.common.model.NodePosition
import me.snoty.integration.common.model.metadata.FieldDefaultValue
import me.snoty.integration.common.wiring.Node
import me.snoty.integration.common.wiring.NodeHandleContext
import me.snoty.integration.common.wiring.data.IntermediateData
import me.snoty.integration.common.wiring.data.NodeOutput
import me.snoty.integration.common.wiring.get
import me.snoty.integration.common.wiring.iterableStructOutput
import me.snoty.integration.common.wiring.node.NodeHandler
import me.snoty.integration.common.wiring.node.NodeSettings
import org.bson.Document
import org.koin.core.annotation.Single

@Serializable
data class AIPromptSettings(
	override val name: String = "AI Prompt",
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
	subsystem = AI,
	type = "ai_prompt",
	displayName = "AI Prompt",
	position = NodePosition.MIDDLE,
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
	override suspend fun NodeHandleContext.process(node: Node, input: Collection<IntermediateData>): NodeOutput {
		val settings = node.settings as AIPromptSettings

		return iterableStructOutput(input.map { raw ->
			val doc = get<Document>(raw)

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
