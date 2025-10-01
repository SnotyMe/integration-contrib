package me.snoty.integration.contrib.willhaben.api.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import me.snoty.integration.contrib.utils.getOrThrow

@Serializable
data class WillhabenListing(
	val id: String,
	val title: String,
	val description: String,
	val price: Double,
	val status: WillhabenStatus,
	val attributes: Map<String, List<String>>,
)

fun JsonObject.parseListing(json: Json): WillhabenListing {
	val id = this["id"]
	val attributes = parseAttributes()

	val title = getOrThrow("description").jsonPrimitive.content
	val description = attributes["DESCRIPTION"]?.joinToString("\n") { it.content }
		?: throw IllegalArgumentException("Description not found in listing")
	val price = attributes["PRICE"]?.firstOrNull()?.doubleOrNull
		?: throw IllegalArgumentException("Price not found in listing")
	val status: WillhabenStatus = json.decodeFromJsonElement(getOrThrow("advertStatus"))

	return WillhabenListing(
		id = id!!.jsonPrimitive.content,
		title = title,
		description = description,
		price = price,
		attributes = attributes.mapValues { (_, values) ->
			values.map { it.content }
		},
		status = status,
	)
}
