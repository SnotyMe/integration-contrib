package me.snoty.contrib.node.autoscout.listing

import kotlinx.serialization.Serializable
import me.snoty.backend.wiring.data.NodeInput
import me.snoty.backend.wiring.data.NodeOutput
import me.snoty.backend.wiring.data.get
import me.snoty.backend.wiring.data.impl.SimpleIntermediateData
import me.snoty.backend.wiring.data.iterableStructOutput
import me.snoty.backend.wiring.node.NodeHandleContext
import me.snoty.backend.wiring.node.NodeHandler
import me.snoty.backend.wiring.node.NodeSettings
import me.snoty.backend.wiring.node.RegisterNode
import me.snoty.backend.wiring.node.metadata.NodeStereotype
import me.snoty.core.node.NodeWithSettings
import me.snoty.contrib.node.autoscout.api.AutoscoutAPI
import me.snoty.contrib.node.autoscout.model.AutoscoutListing
import org.koin.core.annotation.Single

data class ListingInput(
	val url: String
)

@Serializable
data class AutoscoutListingInput(
	val listings: List<String>
) : NodeSettings

@RegisterNode(
	name = "autoscout_listing",
	displayName = "Autoscout Listing",
	stereotype = NodeStereotype.START,
	settingsType = AutoscoutListingInput::class,
	inputType = ListingInput::class,
	outputType = AutoscoutListing::class,
)
@Single
class AutoscoutListingNodeHandler(private val autoscoutAPI: AutoscoutAPI) : NodeHandler {
	context(ctx: NodeHandleContext)
	override suspend fun process(node: NodeWithSettings, input: NodeInput): NodeOutput {
		val settings = node.settings as AutoscoutListingInput

		val mappedFromInput = input.mapNotNull {
			// this node is also a start node, so the input may be the job context, in which case it is not parsed and used to fetch listings
			if (it is SimpleIntermediateData) return@mapNotNull null

			val data: ListingInput = it.get()
			autoscoutAPI.fetchListing(data.url)
		}

		val mappedFromSettings = settings.listings.mapNotNull {
			autoscoutAPI.fetchListing(it)
		}

		return iterableStructOutput(mappedFromInput + mappedFromSettings)
	}
}
