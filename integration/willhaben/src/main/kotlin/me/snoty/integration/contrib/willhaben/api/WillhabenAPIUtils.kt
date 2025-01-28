package me.snoty.integration.contrib.willhaben.api

import io.ktor.client.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

const val WILLHABEN_HOST = "www.willhaben.at"
const val LOGIN_URL = "https://$WILLHABEN_HOST/iad/myprofile/login"
const val WISHLIST_PATH = "/iad/myprofile/myfindings"

fun parseWillhabenUrl(url: String): Url {
	val mappedUrl = parseUrl(url) ?: throw IllegalArgumentException("Invalid URL: $url")
	if (mappedUrl.host != WILLHABEN_HOST) throw IllegalArgumentException("Not a willhaben URL: $url (are you missing the www?)")
	return mappedUrl
}

fun JsonObject.el(name: String) = this[name] ?: throw IllegalArgumentException("Missing required field: $name in $this")

suspend fun HttpResponse.parsePageProps(json: Json): JsonObject {
	val html = bodyAsText()
	val jsonRaw = html.substringAfter("<script id=\"__NEXT_DATA__\" type=\"application/json\">").substringBefore("</script>")
	return json.parseToJsonElement(jsonRaw).jsonObject
		.el("props").jsonObject
		.el("pageProps").jsonObject
}

suspend fun HttpClient.getAuthenticated(url: String, credentials: WillhabenCredentials): HttpResponse = config {
	install(HttpCookies) {
		storage = AcceptAllCookiesStorage()

		runBlocking {
			mapOf(
				"KEYCLOAK_IDENTITY" to credentials.kcIdentity,
				"KEYCLOAK_SESSION" to credentials.kcSession,
			).map { (name, value) ->
				Cookie(
					name = name,
					value = value,
					domain = "sso.willhaben.at",
					path = "/auth/realms/willhaben/",
				)
			}.forEach { cookie -> storage.addCookie(cookie.domain!!, cookie) }
		}
	}
}.run {
	val url = URLBuilder(LOGIN_URL).apply {
		parameters["r"] = url
	}.build()

	return get(url)
}
