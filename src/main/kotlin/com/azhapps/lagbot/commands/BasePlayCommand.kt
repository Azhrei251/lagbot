package com.azhapps.lagbot.commands

import com.azhapps.lagbot.audio.PlayTime
import dev.kord.core.event.message.MessageCreateEvent

abstract class BasePlayCommand(messageEvent: MessageCreateEvent) : BaseCommand(messageEvent) {

    abstract val playTime: PlayTime

    override suspend fun execute() {
       /* val botInVoice = Main.isConnectedToVoice(event)
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
                    event.message.channel.createMessage("Something went wrong:\n${t.message}")
                }
            }

        } else {
            event.message.channel.createMessage("Join a voice channel first!")
        }*/
    }

    private fun playOrLookupSong(songRequest: String) {
       /*if (songRequest.contains("open.spotify.com")) {
            Main.scope.launch {
                SpotifyRepository.getSearchTerms(songRequest).run {
                    forEach {
                        AudioUtil.playSong(event.server.get(), it, event.channel, playTime) {
                            //Do nothing
                        }
                    }
                    event.message.channel.createMessage("${this.size} tracks added to queue")
                }
            }
        } else {
            AudioUtil.playSong(event.server.get(), songRequest, event.channel, playTime) {
                event.message.channel.createMessage(it)
            }
        }*/
    }
}