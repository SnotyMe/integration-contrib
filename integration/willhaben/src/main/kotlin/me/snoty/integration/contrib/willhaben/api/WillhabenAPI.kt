package me.snoty.integration.contrib.willhaben.api

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.HttpHeaders
import io.ktor.http.parseUrl
import kotlinx.serialization.json.*
import org.koin.core.Koin
import org.koin.core.annotation.Single

interface WillhabenAPI {
	suspend fun fetchListing(url: String): WillhabenListing?
}

@Single
class WillhabenAPIImpl(snotyClient: HttpClient, koin: Koin) : WillhabenAPI {
	private val json by koin.inject<Json>()
	private val httpClient = snotyClient.config {
		followRedirects = false
	}

	val logger = KotlinLogging.logger {}

	private fun JsonObject.el(name: String) = this[name] ?: throw IllegalArgumentException("Missing required field: $name in $this")

	override suspend fun fetchListing(url: String): WillhabenListing? {
		val mappedUrl = parseUrl(url) ?: throw IllegalArgumentException("Invalid URL: $url")
		if (mappedUrl.host != "www.willhaben.at") throw IllegalArgumentException("Not a willhaben URL: $url (are you missing the www?)")
		if (mappedUrl.segments.firstOrNull() != "iad" || !mappedUrl.segments.contains("d")) throw IllegalArgumentException("Not a listing URL: $url")

		val response = try {
			httpClient.get(mappedUrl)
		} catch (redirect: RedirectResponseException) {
			val location = redirect.response.headers[HttpHeaders.Location] ?: throw redirect

			if (location.contains("?fromExpiredAdId=")) {
				logger.warn { "Listing $url is expired" }
				return null
			}
			if (!location.contains("/d/")) throw IllegalArgumentException("Invalid redirect location: $location")

			// redirect may be valid (http -> https?) - try the new URL
			httpClient.get(location)
		}

		val html = response.bodyAsText()
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
