package me.snoty.integration.contrib.autoscout.api

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import me.snoty.integration.contrib.autoscout.model.AutoscoutListing
import me.snoty.integration.contrib.autoscout.model.parseListing
import me.snoty.integration.contrib.utils.getOrThrow
import me.snoty.integration.contrib.utils.parseNextPageProps
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent

interface AutoscoutAPI {
	suspend fun fetchListing(url: String): AutoscoutListing?
}

@Single
class AutoscoutAPIImpl(httpClient: HttpClient, private val json: Json) : AutoscoutAPI, KoinComponent {
	private val noRedirectClient = httpClient.config {
		followRedirects = false
		// failures are handled explicitly
		expectSuccess = false
	}

	val logger = KotlinLogging.logger {}

	override suspend fun fetchListing(url: String): AutoscoutListing? {
		val mappedUrl = parseAutoscoutUrl(url)

		val response = noRedirectClient.get(mappedUrl)

		fun logListingExpired() {
			logger.warn { "Listing $url is expired" }
		}

		when (response.status) {
			HttpStatusCode.MovedPermanently -> {
				val location = response.headers[HttpHeaders.Location] ?: throw IllegalArgumentException("Redirect without a Location")
				if (location.startsWith("/lst/")) {
					logListingExpired()
					return null
				}
				throw IllegalArgumentException("Invalid redirect location: $location")
			}
			HttpStatusCode.NotFound, HttpStatusCode.Gone -> {
				logListingExpired()
				return null
			}
			HttpStatusCode.OK -> Unit
			else -> throw IllegalArgumentException("Invalid response status: ${response.status}")
		}

		val props = response.parseNextPageProps(json)
		val advertDetails = props.getOrThrow("listingDetails").jsonObject

		return advertDetails.parseListing()
	}
}
