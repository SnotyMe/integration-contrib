package me.snoty.integration.contrib.utils

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

suspend fun HttpResponse.parseNextPageProps(json: Json): JsonObject {
	val html = bodyAsText()
	val jsonRaw = html.substringAfter("<script id=\"__NEXT_DATA__\" type=\"application/json\">").substringBefore("</script>")
	return json.parseToJsonElement(jsonRaw).jsonObject
		.getOrThrow("props").jsonObject
		.getOrThrow("pageProps").jsonObject
}

fun JsonObject.getOrThrow(name: String) = this[name] ?: throw IllegalArgumentException("Missing required field: $name in $this")
