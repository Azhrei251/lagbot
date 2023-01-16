package com.azhapps.lagbot.spotify.model

data class SpotifyPlaylist(
    val name: String,
    val tracks: PlaylistTracks,
)

data class PlaylistTracks(val items: List<PlaylistTrackItem>)

data class PlaylistTrackItem(val track: SpotifyTrack)