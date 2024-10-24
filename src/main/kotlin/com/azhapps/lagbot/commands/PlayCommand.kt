package com.azhapps.lagbot.commands

import com.azhapps.lagbot.Main
import com.azhapps.lagbot.audio.PlayTime
import com.azhapps.lagbot.commands.model.CommandContext
import org.javacord.api.entity.channel.ServerVoiceChannel
import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import java.util.*

fun Commands.play(
    context: CommandContext,
    server: Server,
    voiceChannel: Optional<ServerVoiceChannel>,
    textChannel: TextChannel,
    requesterInVoice: Boolean,
    playTime: PlayTime
) {
    val botInVoice = Main.isConnectedToVoice(server)
    if (requesterInVoice) {
        val songRequest = context.arguments.substringBefore("?playlist")
        if (botInVoice) {
            playOrLookupSong(context, server, textChannel, playTime, songRequest)
        } else {
            voiceChannel.get().connect().thenAccept {
                audioManager.connect(server, it)
                playOrLookupSong(context, server, textChannel, playTime, songRequest)
            }.whenComplete { _, t ->
                t.printStackTrace()
                context.onResponse("Something went wrong:\n${t.message}")
            }
        }
    } else {
        context.onResponse("Join a voice channel first!")
    }
}

private fun Commands.playOrLookupSong(
    context: CommandContext,
    server: Server,
    textChannel: TextChannel,
    playTime: PlayTime,
    songRequest: String
) {
    if (songRequest.contains("open.spotify.com")) {
        spotifyRepository.getSearchTerms(songRequest) { spotifyResults ->
            spotifyResults.forEach {
                audioManager.playSong(server, it, textChannel, playTime) {
                    //Do nothing
                }
            }
            context.onResponse("${spotifyResults.size} tracks added to queue")
        }
    } else {
        audioManager.playSong(server, songRequest, textChannel, playTime) {
            context.onResponse(it)
        }
    }
}