package com.lrc.server.routes

import com.lrc.server.db.LyricsEntity
import com.lrc.server.db.TrackEntity
import com.lrc.server.services.ExternalLyricsServer
import com.lrc.server.tool.cacheControl
import com.lrc.server.tool.minToMs
import com.lrc.server.tool.minToSec
import com.lrc.server.tool.partOfSecToMs
import com.lrc.server.tool.secToMs
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*
import kotlin.time.Duration.Companion.minutes

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
        get("api/v1/lyrics/{id}") {
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
                    call.cacheControl(30.minToSec())
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

                        call.cacheControl(30.minToSec())
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
        return Regex("""\[(\d{2}:\d{2}\.\d{2})](.+)\n""").findAll(testSyncedLyrics).map {
            val parsedMs = it.groups[1]?.value?.split(":", ".")?.map { it.toLong() }?.let {
                it[0].minToMs() + it[1].secToMs() + it[2].partOfSecToMs()
            }
            (parsedMs ?: 0) to (it.groups[2]?.value?.trim() ?: "")
        }.toMap()
    }

    return null
}