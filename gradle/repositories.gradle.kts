dependencyResolutionManagement {
	repositories {
		mavenCentral()
		maven("https://maven.snoty.me/snapshots")
		// TODO: remove when upgrading to Snoty 0.8.0
		maven("https://redirector.kotlinlang.org/maven/ktor-eap")
		gradlePluginPortal()
	}
	versionCatalogs {
		create("snoty") {
			from("me.snoty:versions:0.8.0-alpha.1")
		}
	}
}
