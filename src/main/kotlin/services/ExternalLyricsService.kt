package com.lrc.server.services

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
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
            log.info("${baseUrl}api/get/$trackId")

            httpClient.get("${baseUrl}api/get/$trackId").body()
        } catch (ex: Exception) {
            log.info(ex.toString())
            null
        }
    }

    suspend fun searchLyrics(trackName: String, artistName: String, albumName: String = ""): List<LyricsResponse>? {
        return try {
            val url = "${baseUrl}api/search?" +
                    "track_name=${trackName.split(" ").joinToString("+")}&" +
                    "artist_name=${artistName.split(" ").joinToString("+")}&" +
                    "album_name=${albumName}"
            log.info(url)

            httpClient.get(url).body()
        } catch (ex: Exception) {
            log.info(ex.toString())
            null
        }
    }
}