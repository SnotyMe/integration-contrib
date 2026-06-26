plugins {
	`kotlin-dsl`
	kotlin("plugin.serialization") version snoty.versions.kotlin.get()
}

dependencies {
	implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

	implementation(kotlin("serialization"))
	snoty.versions.kotlin.get().let {
		implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$it")
	}
	snoty.versions.ksp.get().let {
		implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:$it")
	}
	libs.plugins.shadow.get().let {
		implementation("com.gradleup.shadow:shadow-gradle-plugin:${it.version}")
	}
	snoty.versions.snoty.get().let { version ->
		implementation("me.snoty:conventions:$version")
	}
}
