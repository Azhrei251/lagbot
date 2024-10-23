package com.azhapps.lagbot.github

import com.azhapps.lagbot.Main.mainScope
import com.azhapps.lagbot.github.model.CreateIssueRequest
import com.azhapps.lagbot.utils.NetworkUtils
import com.azhapps.lagbot.utils.PropertiesUtil
import com.azhapps.lagbot.utils.PropertiesUtil.GITHUB_TOKEN
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object GithubRepository {
    private const val GITHUB_API_BASE_URL = "https://api.github.com/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(GITHUB_API_BASE_URL)
        .addConverterFactory(NetworkUtils.converterFactory)
        .client(OkHttpClient.Builder()
            .addInterceptor {
                val request = it.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${PropertiesUtil.get(GITHUB_TOKEN)}")
                    .addHeader("Accept", "application/vnd.github+json")
                    .build()

                it.proceed(request)
            }
            .addInterceptor(NetworkUtils.loggingInterceptor)
            .build())
        .build()
    private val githubDataSource = retrofit.create(GithubDataSource::class.java)

    fun createIssue(title: String, body: String, sendResponse: (String) -> Unit) {
        mainScope.launch {
            val response = githubDataSource.createIssue(CreateIssueRequest(title, body))
            if (response.isSuccessful) {
                response.body()?.let {
                    sendResponse("Created issue, see: ${it.htmlUrl}")
                }
            } else {
                sendResponse("Failed to create issue: ${response.message()}")
            }
        }
    }
}