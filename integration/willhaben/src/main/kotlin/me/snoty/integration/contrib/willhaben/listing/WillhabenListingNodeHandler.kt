package me.snoty.integration.contrib.willhaben.listing

import kotlinx.serialization.Serializable
import me.snoty.integration.common.annotation.RegisterNode
import me.snoty.integration.common.model.NodePosition
import me.snoty.integration.common.wiring.Node
import me.snoty.integration.common.wiring.NodeHandleContext
import me.snoty.integration.common.wiring.data.IntermediateData
import me.snoty.integration.common.wiring.data.NodeOutput
import me.snoty.integration.common.wiring.data.impl.SimpleIntermediateData
import me.snoty.integration.common.wiring.get
import me.snoty.integration.common.wiring.iterableStructOutput
import me.snoty.integration.common.wiring.node.NodeHandler
import me.snoty.integration.common.wiring.node.NodeSettings
import me.snoty.integration.common.wiring.node.Subsystem
import me.snoty.integration.contrib.willhaben.api.WillhabenAPI
import me.snoty.integration.contrib.willhaben.api.WillhabenListing
import org.koin.core.annotation.Single

data class ListingInput(
	val url: String
)

@Serializable
data class WillhabenListingSettings(
	override val name: String = "Willhaben Anzeige",
	val listings: List<String>
) : NodeSettings

@RegisterNode(
	subsystem = Subsystem.INTEGRATION,
	type = "willhaben_listing",
	displayName = "Willhaben Anzeige",
	position = NodePosition.START,
	settingsType = WillhabenListingSettings::class,
	inputType = ListingInput::class,
	outputType = WillhabenListing::class,
)
@Single
class WillhabenListingNodeHandler(private val willhabenAPI: WillhabenAPI) : NodeHandler {
	override suspend fun NodeHandleContext.process(node: Node, input: Collection<IntermediateData>): NodeOutput {
		val settings = node.settings as WillhabenListingSettings

		val mappedFromInput = input.mapNotNull {
			// this node is also a start node, so the input may be the job context, in which case it is not parsed and used to fetch listings
			if (it is SimpleIntermediateData) return@mapNotNull null

			val data = get<ListingInput>(it)
			willhabenAPI.fetchListing(data.url)
		}

		val mappedFromSettings = settings.listings.mapNotNull {
			willhabenAPI.fetchListing(it)
		}

		return iterableStructOutput(mappedFromInput + mappedFromSettings)
	}
}
