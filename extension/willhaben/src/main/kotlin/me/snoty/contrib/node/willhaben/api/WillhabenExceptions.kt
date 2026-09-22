package me.snoty.contrib.node.willhaben.api

import io.ktor.client.plugins.*

class WillhabenBlockedException : Exception("Access to Willhaben was blocked. Try again later or use authentication.")
class WillhabenRequestException(cause: ClientRequestException) : Exception("Error while accessing Willhaben: ${cause.response.status}", cause)
