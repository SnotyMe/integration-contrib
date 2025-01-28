package me.snoty.integration.contrib.willhaben.api

import kotlinx.serialization.Serializable
import me.snoty.integration.common.model.metadata.FieldCensored

@Serializable
data class WillhabenCredentials(
	@FieldCensored
	val kcIdentity: String,
	@FieldCensored
	val kcSession: String,
)
