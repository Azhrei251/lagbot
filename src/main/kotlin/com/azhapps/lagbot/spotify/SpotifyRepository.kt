package com.azhapps.lagbot.spotify

import com.azhapps.lagbot.spotify.model.AuthToken
import com.azhapps.lagbot.utils.NetworkUtils
import com.azhapps.lagbot.utils.PropertiesUtil
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.*

object SpotifyRepository {

    private const val SPOTIFY_API_BASE_URL = "https://api.spotify.com/v1/"
    private const val SPOTIFY_AUTH_BASE_URL = "https://accounts.spotify.com/"
    private const val EXPIRY_OFFSET = 60000L

    private var authToken: AuthToken? = null
    private var tokenExpiryTime: Long = 0

    private val authRetrofit = Retrofit.Builder()
        .baseUrl(SPOTIFY_AUTH_BASE_URL)
        .client(
            OkHttpClient.Builder()
                .addInterceptor {
                    it.proceed(
                        it.request()
                            .newBuilder()
                            .addHeader("authorization", getBasicAuthValue())
                            .build()
                    )
                }
                .addInterceptor(NetworkUtils.loggingInterceptor)
                .build()
        )
        .addConverterFactory(NetworkUtils.converterFactory)
        .build()
    private val authService = authRetrofit.create(SpotifyAuthDataSource::class.java)

    private val spotifyRetrofit = Retrofit.Builder()
        .baseUrl(SPOTIFY_API_BASE_URL)
        .client(
            OkHttpClient.Builder()
                .addInterceptor(NetworkUtils.loggingInterceptor)
                .addInterceptor {
                    it.proceed(
                        it.request()
                            .newBuilder()
                            .addHeader("Authorization", "Bearer " + authToken?.accessToken)
                            .build()
                    )
                }
                .build()
        )
        .addConverterFactory(NetworkUtils.converterFactory)
        .build()
    private val spotifyDataSource = spotifyRetrofit.create(SpotifyDataSource::class.java)

    suspend fun getSearchTerms(identifier: String): List<String> {
        updateToken()

        return if (authToken != null) {
            identifier.split('/')
                .lastOrNull()
                ?.split('?')
                ?.firstOrNull()
                ?.let { lookupId ->
                    when {
                        identifier.contains("track") -> getTrackSearchTerm(lookupId)
                        identifier.contains("album") -> getAlbumSearchTerms(lookupId)
                        identifier.contains("playlist") -> getPlaylistSearchTerms(lookupId)
                        else -> emptyList()
                    }
                } ?: emptyList()
        } else emptyList()
    }

    private suspend fun getTrackSearchTerm(trackId: String) =
        spotifyDataSource.getTrack(trackId).takeIf { it.isSuccessful }?.body()?.getSearchTerm()?.let {
            listOf(it)
        } ?: emptyList()

    private suspend fun getAlbumSearchTerms(albumId: String): List<String> {
        val searchTerms = mutableListOf<String>()
        spotifyDataSource.getAlbum(albumId).takeIf { it.isSuccessful }?.body()?.let {
            it.tracks.items.forEach { track ->
                searchTerms.add(track.getSearchTerm())
            }
        }
        return searchTerms
    }

    private suspend fun getPlaylistSearchTerms(playlistId: String): List<String> {
        val searchTerms = mutableListOf<String>()
        spotifyDataSource.getPlaylist(playlistId).takeIf { it.isSuccessful }?.body()?.let {
            it.tracks.items.forEach { item ->
                searchTerms.add(item.track.getSearchTerm())
            }
        }
        return searchTerms
    }

    private suspend fun updateToken() {
        if (authToken == null || System.currentTimeMillis() > tokenExpiryTime) {
            val tokenResponse = authService.getAuthToken()
            if (tokenResponse.isSuccessful) {
                tokenResponse.body()?.let {
                    authToken = it
                    tokenExpiryTime = System.currentTimeMillis() + (authToken?.expiresIn?.times(1000L) ?: 0L) - EXPIRY_OFFSET
                }
            }
        }
    }

    private fun getBasicAuthValue(): String =
        "Basic " + Base64.getEncoder()
            .encodeToString("${PropertiesUtil.get(PropertiesUtil.SPOTIFY_CLIENT_ID)}:${PropertiesUtil.get(PropertiesUtil.SPOTIFY_CLIENT_SECRET)}".toByteArray())
}