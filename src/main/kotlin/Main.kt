package com.lrc.server

import com.lrc.server.routes.getLyrics
import com.lrc.server.services.ExternalLyricsServer
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.jdbc.Database
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin

fun main() {
    embeddedServer(
        factory = Netty,
        port = 8080,
        host = "0.0.0.0"
    ) {
        configure()

        val lyricsServer by inject<ExternalLyricsServer>()
        getLyrics(lyricsServer)
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
        modules(module {
            single<HttpClient> {
                HttpClient(CIO) {
                    install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                        json(Json {
                            ignoreUnknownKeys = true
                            coerceInputValues = true
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
        })
    }
}