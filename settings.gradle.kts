apply(from = "gradle/repositories.gradle.kts")

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}
rootProject.name = "extension-contrib"

include("utils")

// include all extensions per default
File(rootDir, "extension")
	.listFiles()!!
	.filter { it.resolve("build.gradle.kts").exists() }
	.filterNot { it.name == "buildSrc" }
	.forEach {
		include(":extension:${it.name}")
	}
