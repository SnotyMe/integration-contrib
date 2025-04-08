package me.snoty.integration.contrib.autoscout.model

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.snoty.integration.contrib.utils.getOrThrow

data class AutoscoutListing(
	val id: String,
	val model: String,
	val description: String,
	val price: Double,
	val seller: AutoscoutSeller,
	val location: AutoscoutLocation,
)

fun JsonObject.parseListing(): AutoscoutListing {
	val id = getOrThrow("id").jsonPrimitive.content
	val model = getOrThrow("vehicle").jsonObject.getOrThrow("modelVersionInput").jsonPrimitive.content
	val description = getOrThrow("description").jsonPrimitive.content
	val price = getOrThrow("prices").jsonObject
		.getOrThrow("public").jsonObject
		.getOrThrow("priceRaw").jsonPrimitive.double

	val seller = getOrThrow("seller").jsonObject.parseSeller()
	val location = getOrThrow("location").jsonObject.parseLocation()

	return AutoscoutListing(
		id = id,
		model = model,
		description = description,
		price = price,
		seller = seller,
		location = location,
	)
}
