package com.azhapps.lagbot.spotify

import com.azhapps.lagbot.spotify.model.SpotifyAlbum
import com.azhapps.lagbot.spotify.model.SpotifyPlaylist
import com.azhapps.lagbot.spotify.model.SpotifyTrack
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SpotifyDataSource {

    @GET("tracks/{trackId}")
    suspend fun getTrack(@Path("trackId") trackId: String): Response<SpotifyTrack>

    @GET("albums/{albumId}")
    suspend fun getAlbum(@Path("albumId") albumId: String): Response<SpotifyAlbum>

    @GET("playlists/{playlistId}")
    suspend fun getPlaylist(@Path("playlistId") playlistId: String): Response<SpotifyPlaylist>
}