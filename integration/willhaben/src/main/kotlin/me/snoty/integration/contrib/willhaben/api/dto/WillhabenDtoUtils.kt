package me.snoty.integration.contrib.willhaben.api.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.snoty.integration.contrib.utils.getOrThrow

@Serializable
data class WillhabenStatus(
	val id: String,
	val description: String,
	val statusId: Int,
)

fun JsonObject.parseAttributes() = getOrThrow("attributes")
	.jsonObject
	.getOrThrow("attribute")
	.jsonArray
	.associate {
		val key = it.jsonObject.getOrThrow("name").jsonPrimitive.content
		val value = it.jsonObject.getOrThrow("values").jsonArray.map { item -> item.jsonPrimitive }
		key to value
	}
