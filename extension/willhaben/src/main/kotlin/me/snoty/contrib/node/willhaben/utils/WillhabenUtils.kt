package me.snoty.contrib.node.willhaben.utils

fun <T> List<T>.mapIf(predicate: Boolean, transform: (T) -> T): List<T> =
	if (predicate) map(transform)
	else this
