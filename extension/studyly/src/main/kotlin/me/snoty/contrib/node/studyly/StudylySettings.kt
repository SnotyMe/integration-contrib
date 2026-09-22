package me.snoty.contrib.node.studyly

import kotlinx.serialization.Serializable
import me.snoty.backend.schema.FieldCensored
import me.snoty.backend.wiring.node.NodeSettings

@Serializable
data class StudylySettings(
	@FieldCensored
	val sessionId: String,
) : NodeSettings
