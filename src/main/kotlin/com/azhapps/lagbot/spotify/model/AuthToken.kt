package com.azhapps.lagbot.spotify.model

import com.google.gson.annotations.SerializedName

data class AuthToken(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("expires_in") val expiresIn: Long
)