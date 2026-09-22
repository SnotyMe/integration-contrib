package me.snoty.contrib.node.studyly

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudylyHomework(
	val id: Long,
	val dateDue: Instant,
	val examplesToSolve: Int,
	@SerialName("bundleName")
	val name: String,
)
