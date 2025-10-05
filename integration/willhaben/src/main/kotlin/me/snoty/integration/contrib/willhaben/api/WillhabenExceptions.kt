package me.snoty.integration.contrib.willhaben.api

import io.ktor.client.plugins.*

class WillhabenBlockedException : Exception("Access to Willhaben was blocked. Try again later or use authentication.")
class WillhabenRequestException(cause: ClientRequestException) : Exception("Error while accessing Willhaben: ${cause.response.status}", cause)
