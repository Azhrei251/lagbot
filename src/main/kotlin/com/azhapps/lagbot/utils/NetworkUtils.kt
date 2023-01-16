package com.azhapps.lagbot.utils

import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory

object NetworkUtils {
    val loggingInterceptor = HttpLoggingInterceptor().setLevel(
        if (PropertiesUtil.get(PropertiesUtil.DEBUG).toBoolean())
            HttpLoggingInterceptor.Level.BODY
        else
            HttpLoggingInterceptor.Level.BASIC
    )
    val converterFactory: GsonConverterFactory = GsonConverterFactory.create()
}