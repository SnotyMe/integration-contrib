package me.snoty.contrib.node.autoscout.model

import kotlinx.serialization.json.*
import me.snoty.backend.schema.FieldDescription

data class AutoscoutSeller(
	val contactName: String?,
	val phones: List<Phone>
)

data class Phone(
	val phoneType: String,
	@FieldDescription("The number, in a pretty human-readable format")
	val formattedNumber: String,
	@FieldDescription("A callable number without any formatting")
	val callTo: String
)

fun JsonObject.parseSeller(): AutoscoutSeller {
	val contactName = this["contactName"]?.jsonPrimitive?.contentOrNull
	val phones = this["phones"]?.jsonArray?.map { phone ->
		Phone(
			phoneType = phone.jsonObject["phoneType"]!!.jsonPrimitive.content,
			formattedNumber = phone.jsonObject["formattedNumber"]!!.jsonPrimitive.content,
			callTo = phone.jsonObject["callTo"]!!.jsonPrimitive.content
		)
	} ?: emptyList()

	return AutoscoutSeller(
		contactName = contactName,
		phones = phones
	)
}

data class AutoscoutLocation(
	val countryCode: String,
	val zip: String?,
	val city: String?,
	val street: String?,
	val latitude: Double,
	val longitude: Double,
)

fun JsonObject.parseLocation(): AutoscoutLocation {
	val countryCode = this["countryCode"]!!.jsonPrimitive.contentOrNull!!
	val zip = this["zip"]?.jsonPrimitive?.contentOrNull
	val city = this["city"]?.jsonPrimitive?.contentOrNull
	val street = this["street"]?.jsonPrimitive?.contentOrNull
	val latitude = this["latitude"]!!.jsonPrimitive.double
	val longitude = this["longitude"]!!.jsonPrimitive.double

	return AutoscoutLocation(
		countryCode = countryCode,
		zip = zip,
		city = city,
		street = street,
		latitude = latitude,
		longitude = longitude
	)
}
