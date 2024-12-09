apply(from = "gradle/repositories.gradle.kts")

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}
rootProject.name = "integration-contrib"

// include all integrations per default
File(rootDir, "integration")
	.listFiles()!!
	.filter { it.resolve("build.gradle.kts").exists() }
	.filterNot { it.name == "buildSrc" }
	.forEach {
		include(":integration:${it.name}")
	}
