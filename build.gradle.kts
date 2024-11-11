group = "me.snoty.integration.contrib"
version = "UNSET"

subprojects {
	apply(plugin = "snoty.kotlin-conventions")
	if (path.startsWith(":integration:")) {
		apply(plugin = "snoty.integration-conventions")
	}
}
