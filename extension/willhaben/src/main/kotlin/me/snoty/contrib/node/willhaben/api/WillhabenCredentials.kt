package me.snoty.contrib.node.willhaben.api

import kotlinx.serialization.Serializable
import me.snoty.backend.schema.FieldCensored
import me.snoty.backend.wiring.credential.Credential
import me.snoty.backend.wiring.credential.RegisterCredential

@Serializable
@RegisterCredential("Willhaben")
data class WillhabenCredentials(
	@FieldCensored
	val kcIdentity: String,
	@FieldCensored
	val kcSession: String,
) : Credential()
