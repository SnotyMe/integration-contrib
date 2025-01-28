package me.snoty.integration.contrib.willhaben.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
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
	val title = el("description").jsonPrimitive.content
	val attributes = el("attributes").jsonObject.el("attribute").jsonArray.associate {
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
