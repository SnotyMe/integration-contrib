dependencyResolutionManagement {
	repositories {
		mavenCentral()
		maven("https://maven.snoty.me/releases")
		gradlePluginPortal()
	}
	versionCatalogs {
		create("snoty") {
			from("me.snoty:versions:0.7.0")
		}
	}
}
