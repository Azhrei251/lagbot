package com.azhapps.lagbot.github

import com.azhapps.lagbot.github.model.CreateIssueRequest
import com.azhapps.lagbot.github.model.CreateIssueResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GithubDataSource {
    @POST("repos/Azhrei251/lagbot/issues")
    suspend fun createIssue(@Body createIssueRequest: CreateIssueRequest): Response<CreateIssueResponse>
}