package me.snoty.integration.contrib.willhaben.wishlist

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import me.snoty.integration.contrib.willhaben.api.WillhabenAPI
import me.snoty.integration.contrib.willhaben.api.WillhabenAPIImpl
import me.snoty.integration.contrib.willhaben.api.WillhabenCredentials
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
fun main(): Unit = runBlocking {
	val creds: WillhabenCredentials = Json.decodeFromStream(File("credentials.json").inputStream())
	val json = Json

	val koin = startKoin {
		modules(module {
			single { json } bind Json::class
			single {
				WillhabenAPIImpl(httpClient = HttpClient {
					install(ContentNegotiation) {
						json(get())
					}
				})
			} bind WillhabenAPI::class
		})
	}.koin

	println(koin.get<WillhabenAPI>().fetchWishlist(null, creds, false))
}
