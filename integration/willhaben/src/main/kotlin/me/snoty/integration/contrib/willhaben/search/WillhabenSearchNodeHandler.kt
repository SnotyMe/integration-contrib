package me.snoty.integration.contrib.willhaben.search

import kotlinx.serialization.Serializable
import me.snoty.integration.common.annotation.RegisterNode
import me.snoty.integration.common.model.NodePosition
import me.snoty.integration.common.model.metadata.FieldDescription
import me.snoty.integration.common.wiring.Node
import me.snoty.integration.common.wiring.NodeHandleContext
import me.snoty.integration.common.wiring.data.IntermediateData
import me.snoty.integration.common.wiring.data.NodeOutput
import me.snoty.integration.common.wiring.data.iterableStructOutput
import me.snoty.integration.common.wiring.node.NodeHandler
import me.snoty.integration.common.wiring.node.NodeSettings
import me.snoty.integration.contrib.willhaben.api.WILLHABEN_HOST
import me.snoty.integration.contrib.willhaben.api.WillhabenAPI
import me.snoty.integration.contrib.willhaben.api.dto.WillhabenSearchResult
import org.koin.core.annotation.Single

@Serializable
data class WillhabenSearchSettings(
	override val name: String,
	@FieldDescription("Path after `https://${WILLHABEN_HOST}/iad/`")
	val query: String,
) : NodeSettings

@RegisterNode(
	name = "willhaben_search",
	displayName = "Willhaben Suche",
	settingsType = WillhabenSearchSettings::class,
	outputType = WillhabenSearchResult::class,
	position = NodePosition.START,
)
@Single
class WillhabenSearchNodeHandler(
	private val willhabenAPI: WillhabenAPI,
) : NodeHandler {
	context(ctx: NodeHandleContext)
	override suspend fun process(
		node: Node,
		input: Collection<IntermediateData>
	): NodeOutput {
		val settings = node.settings as WillhabenSearchSettings
		val result = willhabenAPI.search(settings.query)
		return iterableStructOutput(result)
	}
}
