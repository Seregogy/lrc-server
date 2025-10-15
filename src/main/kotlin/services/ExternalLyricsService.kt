package com.lrc.server.services

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.Serializable
import org.slf4j.Logger

@Serializable
data class LyricsResponse(
    val id: Int? = null,
    val name: String? = null,
    val trackName: String? = null,
    val artistName: String? = null,
    val albumName: String? = null,
    val duration: Float? = null,
    val instrumental: Boolean? = null,
    val plainLyrics: String? = null,
    val syncedLyrics: String? = null
)

class ExternalLyricsServer(
    private val httpClient: HttpClient,
    private val log: Logger,
    private val baseUrl: String
) {
    suspend fun getLyrics(trackId: Int): LyricsResponse? {
        return try {
            httpClient.get("${baseUrl}api/get/$trackId") {
                log.info(url.toString())
            }.body()
        } catch (ex: Exception) {
            log.info(ex.toString())
            null
        }
    }

    suspend fun searchLyrics(trackName: String, artistName: String, albumName: String = ""): List<LyricsResponse>? {
        return try {
            httpClient.get("${baseUrl}api/search") {
                parameter("q", listOf(trackName, artistName, albumName).joinToString(" "))

                log.info(url.toString())
            }.body()
        } catch (ex: Exception) {
            log.info(ex.toString())
            null
        }
    }
}