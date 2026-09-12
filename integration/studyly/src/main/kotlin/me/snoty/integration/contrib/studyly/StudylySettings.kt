package me.snoty.integration.contrib.studyly

import kotlinx.serialization.Serializable
import me.snoty.integration.common.model.metadata.FieldCensored
import me.snoty.integration.common.wiring.node.NodeSettings

@Serializable
data class StudylySettings(
	@FieldCensored
	val sessionId: String,
) : NodeSettings
