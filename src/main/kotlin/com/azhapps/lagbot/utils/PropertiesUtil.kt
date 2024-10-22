package com.azhapps.lagbot.utils

import java.io.FileInputStream
import java.util.*

object PropertiesUtil {

    const val DISCORD_TOKEN = "discord.token"
    const val AFK_TIMEOUT = "afk.timeout"
    const val GITHUB_TOKEN = "github.token"
    const val SPOTIFY_CLIENT_ID = "spotify.client.id"
    const val SPOTIFY_CLIENT_SECRET = "spotify.client.secret"
    const val DEBUG = "debug"
    const val PO_TOKEN = "po.token"
    const val VISITOR_DATA = "visitor.data"

    private val defaultProperties by lazy {
        Properties().apply {
            load(FileInputStream("src/main/resources/default.properties"))
        }
    }
    private val localProperties by lazy {
        try {
            Properties().apply {
                load(FileInputStream("local.properties"))
            }
        } catch (t: Throwable) {
            defaultProperties
        }
    }

    fun get(key: String): String =
        System.getenv(toEnvVarKey(key)) ?: localProperties.getProperty(key, defaultProperties.getProperty(key))

    private fun toEnvVarKey(key: String) = key.uppercase().replace(".", "_")
}