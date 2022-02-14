package com.azhapps.lagbot.utils

object Utils {

    fun formatTimeStamp(durationInMillis: Long) : String {
        val seconds = durationInMillis / 1000
        return String.format("%d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, (seconds % 60))
    }
}