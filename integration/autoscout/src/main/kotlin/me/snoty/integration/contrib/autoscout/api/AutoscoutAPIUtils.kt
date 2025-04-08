package me.snoty.integration.contrib.autoscout.api
import io.ktor.http.*

// yes, this is subject to "different TLD" attacks, but that's fine since we're not exposing anything but our own IP
val AUTOSCOUT_HOST = "^(www\\.)?autoscout24.[a-z]+$".toRegex()

fun parseAutoscoutUrl(url: String): Url {
	val mappedUrl = parseUrl(url) ?: throw IllegalArgumentException("Invalid URL: $url")
	if (!AUTOSCOUT_HOST.matches(mappedUrl.host)) throw IllegalArgumentException("Not an autoscout URL: $url")
	return mappedUrl
}
