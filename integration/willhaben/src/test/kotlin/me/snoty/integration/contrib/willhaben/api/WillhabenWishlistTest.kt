package me.snoty.integration.contrib.willhaben.api

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WillhabenWishlistTest {
	fun listing(
		title: String,
		attribute: Pair<String, String>,
		status: WillhabenStatus = WillhabenStatus(
			id = "active",
			description = "Aktiv",
			statusId = 1
		),
	) = WillhabenListing(
		id = "",
		title = title,
		description = "",
		price = 1337.0,
		status = status,
		attributes = mapOf(attribute.first to listOf(attribute.second))
	)

	@Test
	fun cleanDescription_active() {
		val listing = listing(
			title = "Mitsubishi Pajero 2.8 TD GLS SUV / Geländewagen, 1997, 69.420 km, € 7.450,-",
			attribute = "CAR_MODEL/MODEL_SPECIFICATION" to "2.8 TD GLS",
		)
		val cleaned = listing.cleanTitleFromWishlist()
		assertEquals("Mitsubishi Pajero 2.8 TD GLS", cleaned.title)
	}

	@Test
	fun cleanDescription_reserved() {
		val listing = listing(
			title = "(reserviert) Volvo Serie 800 T-5R Kombi / Family Van, 1995, 300.000 km, € 7.000,-",
			attribute = "CAR_MODEL/MODEL_SPECIFICATION" to "T-5R",
			status = WillhabenStatus(
				id = "reserved",
				description = "reserviert",
				statusId = 50
			),
		)
		val cleaned = listing.cleanTitleFromWishlist()
		assertEquals("Volvo Serie 800 T-5R", cleaned.title)
	}

	@Test
	fun cleanDescription_marketplace() {
		val listing = listing(
			// actually a marketplace listing that looks similar to a car listing
			title = "Kotlin T-Shirt, 100% Baumwolle, Größe M, 19,99€",
			attribute = "PRODUCT_CATEGORY" to "Kleidung",
		)
		val cleaned = listing.cleanTitleFromWishlist()
		assertEquals(listing.title, cleaned.title)
	}
}
