package com.lrc.server.services

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class LyricsResponse(
    val id: Int? = null,
    val name: String? = null,
    val trackName: String? = null,
    val artistName: String? = null,
    val albumName: String? = null,
    val duration: Float? = null,
    val instrumental: Boolean? = null,
    val plainText: String? = null,
    val syncedLyrics: String? = null
)

class ExternalLyricsServer(
    private val httpClient: HttpClient,
    private val baseUrl: String
) {
    suspend fun getLyrics(trackId: Int): LyricsResponse? {
        return try {
            println("${baseUrl}api/get/$trackId")

            httpClient.get("${baseUrl}api/get/$trackId").body()
        } catch (e: Exception) {
            println(e.message)

            null
        }
    }

    /*suspend fun searchLyrics(query: String): List<LyricsSearchResult> {
        return httpClient.get("$baseUrl/lyrics/search") {
            parameter("q", query)
        }.body()
    }*/
}