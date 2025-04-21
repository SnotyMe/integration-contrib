dependencyResolutionManagement {
	repositories {
		mavenCentral()
		maven("https://maven.snoty.me/releases")
		gradlePluginPortal()
	}
	versionCatalogs {
		create("snoty") {
			from("me.snoty:versions:0.6.1")
		}
	}
}

buildscript {
	repositories {
		gradlePluginPortal()
	}
	dependencies {
		classpath("com.gradleup.shadow:shadow-gradle-plugin:9.0.0-beta12")
	}
}
