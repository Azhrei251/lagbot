package com.azhapps.lagbot.spotify.model

data class SpotifyTrack(
    val name: String,
    val artists: List<SpotifyArtist>,
) {
    fun getSearchTerm() = "${artists.firstOrNull()?.name} ${name} lyric"
}
