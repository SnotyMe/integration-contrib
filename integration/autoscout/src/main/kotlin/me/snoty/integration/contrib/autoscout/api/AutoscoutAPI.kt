package me.snoty.integration.contrib.autoscout.api

import io.ktor.client.*
import io.ktor.client.request.*
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
class AutoscoutAPIImpl(private val httpClient: HttpClient, private val json: Json) : AutoscoutAPI, KoinComponent {
	override suspend fun fetchListing(url: String): AutoscoutListing? {
		val mappedUrl = parseAutoscoutUrl(url)

		val response = httpClient.get(mappedUrl)

		val props = response.parseNextPageProps(json)
		val advertDetails = props.getOrThrow("listingDetails").jsonObject

		return advertDetails.parseListing()
	}
}
