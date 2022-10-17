package com.azhapps.lagbot.github

import com.azhapps.lagbot.github.model.CreateIssueRequest
import com.azhapps.lagbot.utils.PropertiesUtil
import com.azhapps.lagbot.utils.PropertiesUtil.GITHUB_TOKEN
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GithubRepository {
    private const val GITHUB_API_BASE_URL = "https://api.github.com/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(GITHUB_API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder()
            .addInterceptor {
                val request = it.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${PropertiesUtil.get(GITHUB_TOKEN)}")
                    .addHeader("Accept", "application/vnd.github+json")
                    .build()

                it.proceed(request)
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            })
            .build())
        .build()
    private val githubDataSource = retrofit.create(GithubDataSource::class.java)

    private val scope by lazy {
        CoroutineScope(newSingleThreadContext("API"))
    }

    fun createIssue(title: String, body: String, sendResponse: (String) -> Unit) {
        scope.launch {
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