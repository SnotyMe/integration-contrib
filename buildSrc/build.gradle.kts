plugins {
	alias(libs.plugins.kotlin.dsl)
	alias(libs.plugins.kotlin.serialization)
}

dependencies {
	implementation(kotlin("serialization"))
	libs.plugins.kotlin.jvm.get().apply {
		implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$version")
	}
	libs.plugins.ksp.get().apply {
		implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:$version")
	}
	snoty.versions.snoty.get().let { version ->
		implementation("me.snoty:conventions:$version")
	}
}
