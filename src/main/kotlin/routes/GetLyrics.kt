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
    val plainText: String? = null,
    val syncedText: Map<Long, String>? = null,
    val provider: String? = null
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
                        it.plainText,
                        parseSyncedLyrics(it.syncedText),
                        provider = "lrclib.net"
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
                            lyrics.plainText,
                            parseSyncedLyrics(lyrics.syncedText),
                            provider = "lrclib.net"
                        ))
                    }
                } catch (ex: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf(
                            "error" to "${ex.message}"
                        )
                    )
                }
            }

            parseSyncedLyrics("")

            call.respond(
                HttpStatusCode.BadRequest,
                mapOf(
                    "error" to "track not found"
                )
            )
        }
    }
}

fun parseSyncedLyrics(testSyncedLyrics: String?): Map<Long, String>? {
    testSyncedLyrics?.let {
        return Regex("""\[(\d{2}:\d{2}\.\d{2})]\s(\W*)\n""").findAll(testSyncedLyrics).map {
            val parsedMs = it.groups[1]?.value?.split(":", ".")?.map { it.toLong() }?.let {
                it[0] * 60000 + it[1] * 1000 + it[2] * 10
            }
            (parsedMs ?: 0) to (it.groups[2]?.value ?: "")
        }.toMap()
    }

    return null
}