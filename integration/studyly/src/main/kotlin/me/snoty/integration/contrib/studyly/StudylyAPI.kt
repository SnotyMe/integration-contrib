package me.snoty.integration.contrib.studyly

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.koin.core.annotation.Single

@Single
class StudylyAPI(private val httpClient: HttpClient) {
	suspend fun getHomework(settings: StudylySettings): List<StudylyHomework> = httpClient
		.get("https://api.studyly.com/student/homework") {
			header("Origin", "https://app.studyly.com")
			cookie(name = "SESSION", value = settings.sessionId)
		}.body()
}
