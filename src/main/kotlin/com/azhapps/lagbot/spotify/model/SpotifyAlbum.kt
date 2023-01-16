package com.azhapps.lagbot.spotify.model

data class SpotifyAlbum(
    val name: String,
    val artists: List<SpotifyArtist>,
    val totalTracks: Int,
    val tracks: AlbumTracks
)

data class AlbumTracks(val items: List<SpotifyTrack>)