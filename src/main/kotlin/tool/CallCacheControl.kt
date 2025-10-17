package com.lrc.server.tool

import io.ktor.server.routing.RoutingCall

fun RoutingCall.cacheControl(seconds: Int) {
	this.response.headers.append("Cache-Control", "max-age=$seconds")
}