package me.snoty.integration.contrib.willhaben.wishlist

import kotlinx.serialization.Serializable
import me.snoty.integration.common.annotation.RegisterNode
import me.snoty.integration.common.model.NodePosition
import me.snoty.integration.common.model.metadata.FieldDefaultValue
import me.snoty.integration.common.model.metadata.FieldDescription
import me.snoty.integration.common.wiring.Node
import me.snoty.integration.common.wiring.NodeHandleContext
import me.snoty.integration.common.wiring.data.IntermediateData
import me.snoty.integration.common.wiring.data.NodeOutput
import me.snoty.integration.common.wiring.iterableStructOutput
import me.snoty.integration.common.wiring.node.NodeHandler
import me.snoty.integration.common.wiring.node.NodeSettings
import me.snoty.integration.contrib.willhaben.api.WillhabenAPI
import me.snoty.integration.contrib.willhaben.api.WillhabenCredentials
import me.snoty.integration.contrib.willhaben.api.WillhabenListing
import org.koin.core.annotation.Single

@Serializable
data class WillhabenWishlistSettings(
	override val name: String = "Willhaben Merkliste",
	val credentials: WillhabenCredentials,
	@FieldDescription("Vehicle listings contain the year, odometer, price and an optional status in the title. This setting will attempt to strip it to just the vehicle name, removing all additional metadata. May resolve side-effects when computing differences.")
	@FieldDefaultValue("true")
	val cleanTitle: Boolean = false, // set to false for backwards compatibility
) : NodeSettings

@RegisterNode(
	name = "willhaben_wishlist",
	displayName = "Willhaben Merkliste",
	position = NodePosition.START,
	settingsType = WillhabenWishlistSettings::class,
	outputType = WillhabenListing::class,
)
@Single
class WillhabenWishlistNodeHandler(private val willhabenAPI: WillhabenAPI) : NodeHandler {
	override suspend fun NodeHandleContext.process(node: Node, input: Collection<IntermediateData>): NodeOutput {
		val settings = node.settings as WillhabenWishlistSettings

		val mapped = willhabenAPI.fetchWishlist(settings.credentials, settings.cleanTitle)

		return iterableStructOutput(mapped)
	}
}
