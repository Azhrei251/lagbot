package com.azhapps.lagbot.commands.model

import com.azhapps.lagbot.audio.TrackScheduler

data class CommandContext(
    val onResponse: (String) -> Unit,
    val scheduler: TrackScheduler?,
    val arguments: String,
)
