package com.azhapps.lagbot.utils

import dev.kord.core.entity.User
import dev.kord.core.entity.effectiveName

fun User?.displayUsername() = if (this == null) "Unknown" else "${username}|${effectiveName}"