dependencyResolutionManagement {
	repositories {
		mavenLocal()
		mavenCentral()
		maven("https://maven.simulatan.me/releases")
		gradlePluginPortal()
	}
	versionCatalogs {
		create("snoty") {
			from("me.snoty:versions:0.0.1")
		}
	}
}
