package me.snoty.contrib.node.willhaben.listing

import kotlinx.serialization.Serializable
import me.snoty.backend.utils.proxy.ProxyCredential
import me.snoty.backend.wiring.credential.CredentialRef
import me.snoty.backend.wiring.credential.resolveOrNull
import me.snoty.backend.wiring.data.NodeInput
import me.snoty.backend.wiring.data.NodeOutput
import me.snoty.backend.wiring.data.get
import me.snoty.backend.wiring.data.impl.SimpleIntermediateData
import me.snoty.backend.wiring.data.iterableStructOutput
import me.snoty.backend.wiring.node.*
import me.snoty.backend.wiring.node.metadata.NodeStereotype
import me.snoty.core.node.NodeWithSettings
import me.snoty.contrib.node.willhaben.api.WillhabenAPI
import me.snoty.contrib.node.willhaben.api.dto.WillhabenListing
import org.koin.core.annotation.Single

data class ListingInput(
	val url: String
)

@Serializable
data class WillhabenListingSettings(
	val listings: List<String>,

	val proxy: CredentialRef<ProxyCredential>? = null,
) : NodeSettings

@RegisterNode(
	name = "willhaben_listing",
	displayName = "Willhaben Anzeige",
	icon = Icon(name = "arcticons-willhaben", color = "#00A4E8"),
	stereotype = NodeStereotype.START,
	settingsType = WillhabenListingSettings::class,
	inputType = ListingInput::class,
	outputType = WillhabenListing::class,
)
@Single
class WillhabenListingNodeHandler(private val willhabenAPI: WillhabenAPI) : NodeHandler {
	context(ctx: NodeHandleContext)
	override suspend fun process(node: NodeWithSettings, input: NodeInput): NodeOutput {
		val settings = node.settings as WillhabenListingSettings
		val proxy = settings.proxy.resolveOrNull(node.userId)

		val mappedFromInput = input.mapNotNull {
			// this node is also a start node, so the input may be the job context, in which case it is not parsed and used to fetch listings
			if (it is SimpleIntermediateData) return@mapNotNull null

			val data: ListingInput = it.get()
			willhabenAPI.fetchListing(proxy, data.url)
		}

		val mappedFromSettings = settings.listings.mapNotNull {
			willhabenAPI.fetchListing(proxy, it)
		}

		return iterableStructOutput(mappedFromInput + mappedFromSettings)
	}
}
