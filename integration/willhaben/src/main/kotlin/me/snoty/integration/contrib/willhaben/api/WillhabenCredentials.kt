package me.snoty.integration.contrib.willhaben.api

import kotlinx.serialization.Serializable
import me.snoty.backend.wiring.credential.Credential
import me.snoty.backend.wiring.credential.RegisterCredential
import me.snoty.integration.common.model.metadata.FieldCensored

@Serializable
@RegisterCredential("Willhaben")
data class WillhabenCredentials(
	@FieldCensored
	val kcIdentity: String,
	@FieldCensored
	val kcSession: String,
) : Credential()
