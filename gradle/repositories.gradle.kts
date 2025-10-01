dependencyResolutionManagement {
	repositories {
		mavenCentral()
		maven("https://maven.snoty.me/snapshots")
		gradlePluginPortal()
	}
	versionCatalogs {
		create("snoty") {
			from("me.snoty:versions:0.7.0-alpha.1")
		}
	}
}

buildscript {
	repositories {
		gradlePluginPortal()
	}
	dependencies {
		classpath("com.gradleup.shadow:shadow-gradle-plugin:9.2.2")
	}
}
