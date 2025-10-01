package me.snoty.integration.contrib.willhaben.api.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import me.snoty.integration.contrib.utils.getOrThrow

@Serializable
data class WillhabenSearchResult(
	val id: String,
	val title: String,
	val description: String?,
	val price: Double,
	val status: WillhabenStatus,
	val attributes: Map<String, List<String>>,
)

fun JsonObject.parseSearchResult(json: Json): WillhabenSearchResult {
	val id = this["id"]!!.jsonPrimitive.content
	val attributes = parseAttributes()
	
	val title = getOrThrow("description").jsonPrimitive.content
	val description = attributes["BODY_DYN"]?.joinToString("\n") { it.content }
	val price = attributes["PRICE"]?.firstOrNull()?.doubleOrNull
		?: throw IllegalArgumentException("Price not found in listing")
	val status: WillhabenStatus = json.decodeFromJsonElement(getOrThrow("advertStatus"))
	
	return WillhabenSearchResult(
		id = id,
		title = title,
		description = description,
		price = price,
		attributes = attributes.mapValues { (_, values) ->
			values.map { it.content }
		},
		status = status,
	)
}