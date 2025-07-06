package me.snoty.integration.contrib.willhaben.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import me.snoty.integration.contrib.utils.getOrThrow

@Serializable
data class WillhabenStatus(
	val id: String,
	val description: String,
	val statusId: Int,
)

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
	val attributes = getOrThrow("attributes").jsonObject.getOrThrow("attribute").jsonArray.associate {
		val key = it.jsonObject.getOrThrow("name").jsonPrimitive.content
		val value = it.jsonObject.getOrThrow("values").jsonArray.toList()
		key to value
	}

	val title = getOrThrow("description").jsonPrimitive.content
	val description = attributes["DESCRIPTION"]
		?: throw IllegalArgumentException("Description not found in listing")
	val price = attributes["PRICE"]?.firstOrNull()?.jsonPrimitive?.doubleOrNull
		?: throw IllegalArgumentException("Price not found in listing")
	val status: WillhabenStatus = json.decodeFromJsonElement(getOrThrow("advertStatus"))

	return WillhabenListing(
		id = id!!.jsonPrimitive.content,
		title = title,
		description = description.joinToString("\n"),
		price = price,
		attributes = attributes.mapValues { (_, values) ->
			values.map { it.jsonPrimitive.content }
		},
		status = status,
	)
}
