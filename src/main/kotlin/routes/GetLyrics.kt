package com.lrc.server.routes

import com.lrc.server.db.LyricsEntity
import com.lrc.server.db.TrackEntity
import com.lrc.server.db.TracksTable
import com.lrc.server.services.ExternalLyricsServer
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.lang.Exception
import java.util.UUID

@Serializable
data class LyricsResponse(
    val plainText: String,
    val syncedText: String,
    val provider: String
)

fun Application.getLyrics(
    externalLyricsServer: ExternalLyricsServer
) {
    routing {
        get("api/v1/tracks/{id}/lyrics") {
            val trackId = call.parameters["id"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "error" to "id is null"
            )

            transaction {
                TrackEntity.findById(UUID.fromString(trackId))
            }?.let { fetchedTrack ->
                transaction {
                    fetchedTrack.lyrics.firstOrNull()
                }?.let {
                    return@get call.respond(LyricsResponse(
                        it.plainText ?: "",
                        it.syncedText ?: "",
                        "lrclib.net"
                    ))
                }

                try {
                    log.info("calling external api")

                    externalLyricsServer.searchLyrics(
                        fetchedTrack.name,
                        transaction {
                            fetchedTrack.artists.joinToString(", ") {
                                it.name
                            }
                        }
                    )!!.let {
                        val lyrics = transaction {
                            LyricsEntity.new {
                                plainText = it.firstOrNull()?.plainLyrics
                                syncedText = it.firstOrNull()?.syncedLyrics

                                track = fetchedTrack
                            }
                        }

                        call.respond(LyricsResponse(
                            lyrics.plainText ?: "",
                            lyrics.syncedText ?: "",
                            "lrclib.net"
                        ))
                    }
                } catch (ex: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf(
                            "error" to "error was happened, after send request on external api\n${ex.message}"
                        )
                    )
                }
            }

            call.respond(
                HttpStatusCode.BadRequest,
                mapOf(
                    "error" to "track not found"
                )
            )
        }
    }
}