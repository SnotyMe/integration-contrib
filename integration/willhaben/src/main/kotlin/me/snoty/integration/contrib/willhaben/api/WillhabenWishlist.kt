package me.snoty.integration.contrib.willhaben.api

typealias WillhabenWishlist = List<WillhabenListing>

fun WillhabenListing.cleanTitleFromWishlist(): WillhabenListing {
	val model = attributes["CAR_MODEL/MODEL_SPECIFICATION"]
		?.firstOrNull()
		?: return this

	// optional prefix containing the current status - observed for reserved listings
	val statusPrefix = "(${status.description})"

	// strip everything after the model name
	val title = "${title.substringBefore(model).removePrefix(statusPrefix).trim()} $model"

	return this.copy(title = title)
}
