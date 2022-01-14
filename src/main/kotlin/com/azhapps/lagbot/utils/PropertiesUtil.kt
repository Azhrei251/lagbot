package com.azhapps.lagbot.utils

import java.io.FileInputStream
import java.util.*

object PropertiesUtil {

    const val DISCORD_TOKEN = "discord.token"
    const val AFK_TIMEOUT = "afk.timeout"

    private val defaultProperties by lazy {
        Properties().apply {
            load(FileInputStream("src/main/resources/default.properties"))
        }
    }
    private val localProperties by lazy {
        Properties().apply {
            load(FileInputStream("local.properties"))
        }
    }

    fun get(key: String): String = localProperties.getProperty(key, defaultProperties.getProperty(key))

}