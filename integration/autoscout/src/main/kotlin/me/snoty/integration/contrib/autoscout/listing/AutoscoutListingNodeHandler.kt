package me.snoty.integration.contrib.autoscout.listing

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
import me.snoty.integration.contrib.autoscout.api.AutoscoutAPI
import me.snoty.integration.contrib.autoscout.model.AutoscoutListing
import org.koin.core.annotation.Single

data class ListingInput(
	val url: String
)

@Serializable
data class AutoscoutListingInput(
	override val name: String = "Autoscout Listing",
	val listings: List<String>
) : NodeSettings

@RegisterNode(
	name = "autoscout_listing",
	displayName = "Autoscout Listing",
	position = NodePosition.START,
	settingsType = AutoscoutListingInput::class,
	inputType = ListingInput::class,
	outputType = AutoscoutListing::class,
)
@Single
class AutoscoutListingNodeHandler(private val autoscoutAPI: AutoscoutAPI) : NodeHandler {
	override suspend fun NodeHandleContext.process(node: Node, input: Collection<IntermediateData>): NodeOutput {
		val settings = node.settings as AutoscoutListingInput

		val mappedFromInput = input.mapNotNull {
			// this node is also a start node, so the input may be the job context, in which case it is not parsed and used to fetch listings
			if (it is SimpleIntermediateData) return@mapNotNull null

			val data = get<ListingInput>(it)
			autoscoutAPI.fetchListing(data.url)
		}

		val mappedFromSettings = settings.listings.mapNotNull {
			autoscoutAPI.fetchListing(it)
		}

		return iterableStructOutput(mappedFromInput + mappedFromSettings)
	}
}
