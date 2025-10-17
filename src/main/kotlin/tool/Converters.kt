package com.lrc.server.tool

fun Int.minToSec(): Int {
	return this * 60
}

fun Long.minToMs(): Long {
	return this * 60L.secToMs()
}

fun Long.secToMs(): Long {
	return this * 1000L
}

fun Long.partOfSecToMs(): Long {
	return this * 10L
}