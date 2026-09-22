package me.snoty.contrib.node.willhaben.wishlist

import kotlinx.serialization.Serializable
import me.snoty.backend.schema.FieldDefaultValue
import me.snoty.backend.schema.FieldDescription
import me.snoty.backend.utils.proxy.ProxyCredential
import me.snoty.backend.wiring.credential.CredentialRef
import me.snoty.backend.wiring.credential.resolve
import me.snoty.backend.wiring.credential.resolveOrNull
import me.snoty.backend.wiring.data.NodeInput
import me.snoty.backend.wiring.data.NodeOutput
import me.snoty.backend.wiring.data.iterableStructOutput
import me.snoty.backend.wiring.node.*
import me.snoty.backend.wiring.node.metadata.NodeStereotype
import me.snoty.core.node.NodeWithSettings
import me.snoty.contrib.node.willhaben.api.WillhabenAPI
import me.snoty.contrib.node.willhaben.api.WillhabenCredentials
import me.snoty.contrib.node.willhaben.api.dto.WillhabenListing
import org.koin.core.annotation.Single

@Serializable
data class WillhabenWishlistSettings(
	val credentials: CredentialRef<WillhabenCredentials>? = null,
	@FieldDescription("Vehicle listings contain the year, odometer, price and an optional status in the title. This setting will attempt to strip it to just the vehicle name, removing all additional metadata. May resolve side-effects when computing differences.")
	@FieldDefaultValue("true")
	val cleanTitle: Boolean = false, // set to false for backwards compatibility
	val proxy: CredentialRef<ProxyCredential>? = null,
) : NodeSettings

@RegisterNode(
	name = "willhaben_wishlist",
	displayName = "Willhaben Merkliste",
	icon = Icon(name = "arcticons-willhaben", color = "#00A4E8"),
	stereotype = NodeStereotype.START,
	settingsType = WillhabenWishlistSettings::class,
	outputType = WillhabenListing::class,
)
@Single
class WillhabenWishlistNodeHandler(private val willhabenAPI: WillhabenAPI) : NodeHandler {
	context(ctx: NodeHandleContext)
	override suspend fun process(node: NodeWithSettings, input: NodeInput): NodeOutput {
		val settings = node.settings as WillhabenWishlistSettings
		val proxy = settings.proxy.resolveOrNull(node.userId)
		val credentials = settings.credentials.resolve(node.userId)

		val mapped = willhabenAPI.fetchWishlist(proxy, credentials, settings.cleanTitle)

		return iterableStructOutput(mapped)
	}
}
