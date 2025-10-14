package com.lrc.server

import com.lrc.server.db.LyricsTable
import com.lrc.server.db.TracksTable
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
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils

fun main() {
    Database.connect("jdbc:sqlite:/src/main/data.db", "org.sqlite.JDBC")

    transaction {
        MigrationUtils.statementsRequiredForDatabaseMigration(
            TracksTable, LyricsTable
        ).forEach {
            println(it)
        }
    }

    /*embeddedServer(
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
    }.start(true)*/
}