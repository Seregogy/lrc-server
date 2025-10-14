package com.lrc.server.db

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID

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
    val lyricsProviderLink = text("provider").nullable()

    val track = reference("track", TracksTable).uniqueIndex()
}

class TrackEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<TrackEntity>(TracksTable)

    val name by TracksTable.name
    val artworkUrl by TracksTable.artworkUrl
    val albumName by TracksTable.albumName
    val artistNames by TracksTable.artistNames
    val staticAudioUrl by TracksTable.staticAudioUrl

    val lyrics by LyricsEntity referrersOn LyricsTable.track
}

class LyricsEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<LyricsEntity>(LyricsTable)

    val plainText by LyricsTable.plainText
    val syncedText by LyricsTable.syncedText
    val lyricsProvider by LyricsTable.lyricsProviderLink

    var track by TrackEntity referencedOn LyricsTable.track
}