package me.snoty.integration.contrib.willhaben.api

import kotlinx.serialization.Serializable

@Serializable
data class WillhabenListing(
	val id: String,
	val title: String,
	val description: String,
	val price: Double,
	val attributes: Map<String, List<String>>,
)
