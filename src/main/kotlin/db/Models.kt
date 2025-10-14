package com.lrc.server.db

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID

object TracksTable : UUIDTable("TRACKS_TABLE") {
    val name = text("name")
}

object ArtistsTable : UUIDTable("ARTISTS") {
    val name = text("name")
}

object ArtistsOnTrackTable : UUIDTable("ARTISTS_ON_TRACK_TABLE") {
    val track = reference("track", TracksTable)
    val artist = reference("artist", ArtistsTable)
}

object LyricsTable : UUIDTable("LYRICS_TABLE") {
    val plainText = text("plain_text").nullable()
    val syncedText = text("synced_text").nullable()

    val track = reference("track", TracksTable).uniqueIndex()
}

class ArtistEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ArtistEntity>(ArtistsTable)

    val name by ArtistsTable.name
    val tracks by TrackEntity via ArtistsOnTrackTable
}

class TrackEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<TrackEntity>(TracksTable)

    val name by TracksTable.name
    val lyrics by LyricsEntity referrersOn LyricsTable.track

    var artists by ArtistEntity via ArtistsOnTrackTable
}

class LyricsEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<LyricsEntity>(LyricsTable)

    var plainText by LyricsTable.plainText
    var syncedText by LyricsTable.syncedText

    var track by TrackEntity referencedOn LyricsTable.track
}