package me.snoty.integration.contrib.willhaben.api

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import me.snoty.integration.contrib.utils.getOrThrow
import me.snoty.integration.contrib.utils.parseNextPageProps
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface WillhabenAPI {
	suspend fun fetchListing(url: String): WillhabenListing?
	suspend fun fetchWishlist(creds: WillhabenCredentials): List<WillhabenListing>
}

@Single
class WillhabenAPIImpl(private val httpClient: HttpClient) : WillhabenAPI, KoinComponent {
	private val json by inject<Json>()
	private val noRedirectClient = httpClient.config {
		followRedirects = false
	}

	val logger = KotlinLogging.logger {}

	override suspend fun fetchListing(url: String): WillhabenListing? {
		var mappedUrl = parseWillhabenUrl(url)
		if (
			mappedUrl.segments.firstOrNull() != "iad"
			|| (
				!mappedUrl.segments.contains("d")
				&& mappedUrl.segments.last() != "object"
			)
		) throw IllegalArgumentException("Not a listing URL: $url")

		lateinit var response: HttpResponse

		for (attempt in 0..3) {
			try {
				logger.debug { "Attempting to fetch $mappedUrl - attempt $attempt" }
				response = noRedirectClient.get(mappedUrl)
				break
			} catch (redirect: RedirectResponseException) {
				val location = redirect.response.headers[HttpHeaders.Location] ?: throw redirect

				if (location.contains("?fromExpiredAdId=")) {
					logger.warn { "Listing $url is expired" }
					return null
				}
				if (!location.contains("/d/")) throw IllegalArgumentException("Invalid redirect location: $location")

				// redirect may be valid (/iad/object?adId=... -> /iad/.../d/...) - try the new URL
				val newUrlBuilder = URLBuilder()
				newUrlBuilder.protocol = URLProtocol.HTTPS
				// since we manually follow the redirect, use WILLHABEN_HOST as the default
				newUrlBuilder.host = WILLHABEN_HOST
				// and override the path with the relative path gotten from the original request
				newUrlBuilder.path(location)
				val newUrl = newUrlBuilder.build()

				logger.debug { "Redirected to $newUrl" }
				mappedUrl = newUrl
			}
		}

		val props = response.parseNextPageProps(json)
		val advertDetails = props.getOrThrow("advertDetails").jsonObject

		return advertDetails.parseListing()
	}

	override suspend fun fetchWishlist(creds: WillhabenCredentials): WillhabenWishlist {
		val response = httpClient.getAuthenticated(WISHLIST_PATH, creds)

		val props = response.parseNextPageProps(json)
		val listings = props
			.getOrThrow("pageProps").jsonObject
			.getOrThrow("pageProps").jsonObject
			.getOrThrow("currentSavedAds").jsonObject
			.getOrThrow("advertFolderItemList").jsonObject
			.getOrThrow("advertFolderItems").jsonArray
			.map { it.jsonObject.parseListing() }

		return listings
	}
}
