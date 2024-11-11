plugins {
	kotlin("jvm")
	id("com.google.devtools.ksp")
}

apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
apply(plugin = "snoty.koin-conventions")

dependencies {
	val libs = project.rootProject.extensions
		.getByType<VersionCatalogsExtension>()
		.named("snoty")

	val snotyVersion = libs.findVersion("snoty").get().displayName

	implementation("me.snoty:api:$snotyVersion")
	ksp("me.snoty:integration-plugin:$snotyVersion")
}
