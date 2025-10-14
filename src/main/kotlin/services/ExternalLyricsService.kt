package com.lrc.server.services

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import java.util.UUID

data class LyricsResponse(
    val id: Int,
    val name: String,
    val trackName: String,
    val artistName: String,
    val albumName: String,
    val duration: Long,
    val instrumental: Boolean,
    val plainText: String,
    val syncedLyrics: String
)

class ExternalLyricsServer(
    private val httpClient: HttpClient,
    private val baseUrl: String
) {
    suspend fun getLyrics(trackId: Int): LyricsResponse? {
        return try {
            httpClient.get("${baseUrl}get/$trackId").body()
        } catch (e: Exception) {
            null
        }
    }

    /*suspend fun searchLyrics(query: String): List<LyricsSearchResult> {
        return httpClient.get("$baseUrl/lyrics/search") {
            parameter("q", query)
        }.body()
    }*/
}