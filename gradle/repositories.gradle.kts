dependencyResolutionManagement {
	repositories {
		mavenCentral()
		maven("https://maven.snoty.me/snapshots")
		gradlePluginPortal()
	}
	versionCatalogs {
		create("snoty") {
			from("me.snoty:versions:0.0.2")
		}
	}
}
