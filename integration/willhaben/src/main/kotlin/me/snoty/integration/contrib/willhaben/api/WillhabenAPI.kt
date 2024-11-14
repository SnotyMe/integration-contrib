package me.snoty.integration.contrib.willhaben.api

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.*
import org.koin.core.Koin
import org.koin.core.annotation.Single

interface WillhabenAPI {
	suspend fun fetchListing(url: String): WillhabenListing
}

@Single
class WillhabenAPIImpl(private val httpClient: HttpClient, koin: Koin) : WillhabenAPI {
	private val json by koin.inject<Json>()

	companion object {
		private val pattern = "https://(www\\.)?willhaben\\.at/.*".toRegex()
	}

	private fun JsonObject.el(name: String) = this[name] ?: throw IllegalArgumentException("Missing required field: $name in $this")

	override suspend fun fetchListing(url: String): WillhabenListing {
		if (!url.matches(pattern)) throw IllegalArgumentException("Not a willhaben url: $url")

		val html = httpClient.get(url).bodyAsText()
		val jsonRaw = html.substringAfter("<script id=\"__NEXT_DATA__\" type=\"application/json\">").substringBefore("</script>")
		val props = json.parseToJsonElement(jsonRaw).jsonObject.el("props").jsonObject.el("pageProps").jsonObject
		val advertDetails = props.el("advertDetails").jsonObject

		val id = advertDetails["id"]
		val title = advertDetails.el("description").jsonPrimitive.content
		val attributes = advertDetails.el("attributes").jsonObject.el("attribute").jsonArray.associate {
			val key = it.jsonObject.el("name").jsonPrimitive.content
			val value = it.jsonObject.el("values").jsonArray.toList()
			key to value
		}

		val description = attributes["DESCRIPTION"]
		val price = attributes["PRICE"]?.firstOrNull()?.jsonPrimitive?.doubleOrNull ?: -1.0

		return WillhabenListing(
			id = id!!.jsonPrimitive.content,
			title = title,
			description = description!!.joinToString("\n"),
			price = price,
			attributes = attributes.mapValues { (_, values) ->
				values.map { it.jsonPrimitive.content }
			},
		)
	}
}
