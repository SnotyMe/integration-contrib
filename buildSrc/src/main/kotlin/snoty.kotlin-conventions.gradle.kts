import org.gradle.accessors.dm.LibrariesForLibs

plugins {
	kotlin("jvm")
}

java {
	withSourcesJar()
}

dependencies {
	val libs = project.rootProject.extensions.getByType<LibrariesForLibs>()
	testImplementation(libs.junit.jupiter)
	testImplementation(kotlin("test"))
}

tasks.test {
	useJUnitPlatform()
}
