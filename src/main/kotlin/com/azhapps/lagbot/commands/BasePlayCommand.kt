package com.azhapps.lagbot.commands

import com.azhapps.lagbot.Main
import com.azhapps.lagbot.audio.AudioUtil
import com.azhapps.lagbot.spotify.SpotifyRepository
import kotlinx.coroutines.launch
import org.javacord.api.event.message.MessageCreateEvent

abstract class BasePlayCommand(messageEvent: MessageCreateEvent) : BaseCommand(messageEvent) {

    abstract val playTime: AudioUtil.PlayTime

    override fun execute() {
        val botInVoice = Main.isConnectedToVoice(event)
        val requesterInVoice = event.messageAuthor.connectedVoiceChannel.isPresent
        if (requesterInVoice) {
            val songRequest = event.messageContent.substringAfter(' ').substringBefore("?playlist")
            if (botInVoice) {
                playOrLookupSong(songRequest)
            } else {
                event.messageAuthor.connectedVoiceChannel.get().connect().thenAccept {
                    AudioUtil.connect(event.server.get(), it)
                    playOrLookupSong(songRequest)
                }.whenComplete { _, t ->
                    t.printStackTrace()
                    event.channel.sendMessage("Something went wrong:\n${t.message}")
                }
            }

        } else {
            event.channel.sendMessage("Join a voice channel first!")
        }
    }

    private fun playOrLookupSong(songRequest: String) {
        if (songRequest.contains("open.spotify.com")) {
            Main.scope.launch {
                SpotifyRepository.getSearchTerms(songRequest).run {
                    forEach {
                        AudioUtil.playSong(event.server.get(), it, event.channel, playTime) {
                            //Do nothing
                        }
                    }
                    event.channel.sendMessage("${this.size} tracks added to queue")
                }
            }
        } else {
            AudioUtil.playSong(event.server.get(), songRequest, event.channel, playTime) {
                event.channel.sendMessage(it)
            }
        }
    }
}