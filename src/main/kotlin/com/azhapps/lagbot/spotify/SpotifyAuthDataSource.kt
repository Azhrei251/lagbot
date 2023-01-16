package com.azhapps.lagbot.spotify

import com.azhapps.lagbot.spotify.model.AuthToken
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


private const val DEFAULT_GRANT_TYPE = "client_credentials"

interface SpotifyAuthDataSource {

    @FormUrlEncoded
    @POST("api/token")
    suspend fun getAuthToken(
        @Field("grant_type") grantType: String = DEFAULT_GRANT_TYPE
    ): Response<AuthToken>
}