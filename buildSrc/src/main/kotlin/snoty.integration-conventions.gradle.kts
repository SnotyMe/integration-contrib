import com.github.jengelman.gradle.plugins.shadow.ShadowJavaPlugin.Companion.shadowJar

plugins {
	kotlin("jvm")
	id("com.google.devtools.ksp")
	id("com.gradleup.shadow")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xcontext-parameters")
		optIn.addAll("kotlin.uuid.ExperimentalUuidApi")
	}
}

apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
apply(plugin = "snoty.koin-conventions")
apply(plugin = "com.gradleup.shadow")

dependencies {
	val libs = project.rootProject.extensions
		.getByType<VersionCatalogsExtension>()
		.named("snoty")

	val snotyVersion = libs.findVersion("snoty").get().displayName

	compileOnly("me.snoty:api:$snotyVersion")
	if (project.name != "utils") {
		implementation(project(":utils"))
	}
	implementation("me.snoty:integration-utils:$snotyVersion")

	ksp("me.snoty:integration-plugin:$snotyVersion")

	// compileOnly dependencies aren't part of the test classpath
	testImplementation("me.snoty:api:${snotyVersion}")
}

tasks.jar {
	archiveClassifier = "nodeps"
	finalizedBy(tasks.shadowJar)
}

tasks.shadowJar {
	archiveClassifier = ""

	dependencies {
		fun Set<ResolvedDependency>.anyRecursive(block: (ResolvedDependency) -> Boolean): Boolean {
			return this.any(block) || this.any { dep ->
				dep.parents.anyRecursive(block)
			}
		}

		exclude {
			// all transitive dependencies of the `utils` module are removed, as if it was part of this module
			it.parents.anyRecursive { parent ->
				parent.moduleGroup == rootProject.name && parent.moduleName == "utils"
			}
		}
	}

	enableRelocation = true
	// names have to be unique anyway
	relocationPrefix = project.name

	mergeServiceFiles()
}

tasks.register("version") {
	println(version)
}
