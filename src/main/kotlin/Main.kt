package com.lrc.server

import com.lrc.server.routes.getLyrics
import com.lrc.server.services.ExternalLyricsServer
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.jdbc.Database
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

fun main() {
    embeddedServer(
        factory = Netty,
        port = 8080,
        host = "0.0.0.0"
    ) {
        configure()
        getLyrics(inject<ExternalLyricsServer>().value)
    }.start(true)
}

private fun Application.configure() {
    Database.connect("jdbc:sqlite:src/main/data.db", "org.sqlite.JDBC")

    install(AutoHeadResponse)
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
        )
    }
    install(Koin) {
        module {
            single<HttpClient> {
                HttpClient(CIO) {
                    install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                        json(Json {
                            ignoreUnknownKeys = true
                        })
                    }
                    install(HttpTimeout) {
                        requestTimeoutMillis = 10000
                        connectTimeoutMillis = 5000
                    }
                    expectSuccess = false
                }
            }

            single<ExternalLyricsServer> {
                ExternalLyricsServer(
                    get(),
                    "https://lrclib.net/"
                )
            }
        }
    }
}