package me.snoty.integration.contrib.willhaben.api

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
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
		val mappedUrl = parseWillhabenUrl(url)
		if (mappedUrl.segments.firstOrNull() != "iad" || !mappedUrl.segments.contains("d")) throw IllegalArgumentException("Not a listing URL: $url")

		val response = try {
			noRedirectClient.get(mappedUrl)
		} catch (redirect: RedirectResponseException) {
			val location = redirect.response.headers[HttpHeaders.Location] ?: throw redirect

			if (location.contains("?fromExpiredAdId=")) {
				logger.warn { "Listing $url is expired" }
				return null
			}
			if (!location.contains("/d/")) throw IllegalArgumentException("Invalid redirect location: $location")

			// redirect may be valid (http -> https?) - try the new URL
			noRedirectClient.get(location)
		}

		val props = response.parsePageProps(json)
		val advertDetails = props.el("advertDetails").jsonObject

		return advertDetails.parseListing()
	}

	override suspend fun fetchWishlist(creds: WillhabenCredentials): WillhabenWishlist {
		val response = httpClient.getAuthenticated(WISHLIST_PATH, creds)

		val props = response.parsePageProps(json)
		val listings = props.el("pageProps").jsonObject
			.el("pageProps").jsonObject
			.el("currentSavedAds").jsonObject
			.el("advertFolderItemList").jsonObject
			.el("advertFolderItems").jsonArray
			.map { it.jsonObject.parseListing() }

		return listings
	}
}
