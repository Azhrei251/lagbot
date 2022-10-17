package com.azhapps.lagbot.github.model

import com.google.gson.annotations.SerializedName

data class CreateIssueResponse(
    val url: String,
    @SerializedName("html_url") val htmlUrl: String,
)