package com.azhapps.lagbot.commands

import com.azhapps.lagbot.Main
import com.azhapps.lagbot.audio.PlayTime
import kotlinx.coroutines.launch
import org.javacord.api.event.message.MessageCreateEvent

fun Commands.play(event: MessageCreateEvent, playTime: PlayTime) {
    val botInVoice = Main.isConnectedToVoice(event)
    val requesterInVoice = event.messageAuthor.connectedVoiceChannel.isPresent
    if (requesterInVoice) {
        val songRequest = event.messageContent.substringAfter(' ').substringBefore("?playlist")
        if (botInVoice) {
            playOrLookupSong(event, playTime, songRequest)
        } else {
            event.messageAuthor.connectedVoiceChannel.get().connect().thenAccept {
                audioManager.connect(event.server.get(), it)
                playOrLookupSong(event, playTime, songRequest)
            }.whenComplete { _, t ->
                t.printStackTrace()
                event.channel.sendMessage("Something went wrong:\n${t.message}")
            }
        }

    } else {
        event.channel.sendMessage("Join a voice channel first!")
    }
}

private fun Commands.playOrLookupSong(event: MessageCreateEvent, playTime: PlayTime, songRequest: String) {
    if (songRequest.contains("open.spotify.com")) {
        scope.launch {
            spotifyRepository.getSearchTerms(songRequest).run {
                forEach {
                    audioManager.playSong(event.server.get(), it, event.channel, playTime) {
                        //Do nothing
                    }
                }
                event.channel.sendMessage("${this.size} tracks added to queue")
            }
        }
    } else {
        audioManager.playSong(event.server.get(), songRequest, event.channel, playTime) {
            event.channel.sendMessage(it)
        }
    }
}