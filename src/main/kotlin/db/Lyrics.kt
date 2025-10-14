package com.lrc.server.db

import org.jetbrains.exposed.v1.core.dao.id.UUIDTable

object TracksTable : UUIDTable("TRACKS") {
    val name = text("name")
    val artworkUrl = text("artworkUrl")
    val albumName = text("album_name")
    val artistNames = text("artists")
    val staticAudioUrl = text("audio_url")
}

object LyricsTable : UUIDTable("LYRICS") {
    val plainText = text("plain_text").nullable()
    val syncedText = text("synced_text").nullable()

    val track = reference("track", TracksTable)
}