package me.snoty.contrib.node.willhaben.search

import kotlinx.serialization.Serializable
import me.snoty.backend.schema.FieldDescription
import me.snoty.backend.utils.proxy.ProxyCredential
import me.snoty.backend.wiring.credential.CredentialRef
import me.snoty.backend.wiring.credential.resolveOrNull
import me.snoty.backend.wiring.data.NodeInput
import me.snoty.backend.wiring.data.NodeOutput
import me.snoty.backend.wiring.data.iterableStructOutput
import me.snoty.backend.wiring.node.*
import me.snoty.backend.wiring.node.metadata.NodeStereotype
import me.snoty.core.node.NodeWithSettings
import me.snoty.contrib.node.willhaben.api.WILLHABEN_HOST
import me.snoty.contrib.node.willhaben.api.WillhabenAPI
import me.snoty.contrib.node.willhaben.api.dto.WillhabenSearchResult
import org.koin.core.annotation.Single

@Serializable
data class WillhabenSearchSettings(
	@FieldDescription("Path after `https://${WILLHABEN_HOST}/iad/`")
	val query: String,
	val proxy: CredentialRef<ProxyCredential>? = null,
) : NodeSettings

@RegisterNode(
	name = "willhaben_search",
	displayName = "Willhaben Suche",
	icon = Icon(name = "arcticons-willhaben", color = "#00A4E8"),
	settingsType = WillhabenSearchSettings::class,
	outputType = WillhabenSearchResult::class,
	stereotype = NodeStereotype.START,
)
@Single
class WillhabenSearchNodeHandler(
	private val willhabenAPI: WillhabenAPI,
) : NodeHandler {
	context(ctx: NodeHandleContext)
	override suspend fun process(node: NodeWithSettings, input: NodeInput): NodeOutput {
		val settings = node.settings as WillhabenSearchSettings
		val proxy = settings.proxy.resolveOrNull(node.userId)
		val result = willhabenAPI.search(proxy, settings.query)
		return iterableStructOutput(result)
	}
}
