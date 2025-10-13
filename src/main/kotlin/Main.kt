package com.lrc.server

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

fun main() {
	embeddedServer(
		factory = Netty,
		port = 8080,
		host = "0.0.0.0"
	) {
		install(AutoHeadResponse)

		install(ContentNegotiation) {
			json()
		}

		routing {
			get("status") {
				call.respond(
					mapOf(
						"status" to "exists"
					)
				)
			}
		}
	}.start(true)
}