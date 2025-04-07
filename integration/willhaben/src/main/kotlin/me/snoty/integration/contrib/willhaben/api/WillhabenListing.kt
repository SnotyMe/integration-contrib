package me.snoty.integration.contrib.willhaben.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.snoty.integration.contrib.utils.getOrThrow
import kotlin.collections.firstOrNull
import kotlin.collections.joinToString

@Serializable
data class WillhabenListing(
	val id: String,
	val title: String,
	val description: String,
	val price: Double,
	val attributes: Map<String, List<String>>,
)

fun JsonObject.parseListing(): WillhabenListing {
	val id = this["id"]
	val title = getOrThrow("description").jsonPrimitive.content
	val attributes = getOrThrow("attributes").jsonObject.getOrThrow("attribute").jsonArray.associate {
		val key = it.jsonObject.getOrThrow("name").jsonPrimitive.content
		val value = it.jsonObject.getOrThrow("values").jsonArray.toList()
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
