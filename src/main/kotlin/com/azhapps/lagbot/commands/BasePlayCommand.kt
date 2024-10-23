package com.azhapps.lagbot.commands

import com.azhapps.lagbot.Main
import com.azhapps.lagbot.audio.AudioManager
import com.azhapps.lagbot.audio.PlayTime
import com.azhapps.lagbot.spotify.SpotifyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.javacord.api.event.message.MessageCreateEvent

abstract class BasePlayCommand(
    messageEvent: MessageCreateEvent,
    private val audioManager: AudioManager,
    private val scope: CoroutineScope,
    private val spotifyRepository: SpotifyRepository = SpotifyRepository(),
) : BaseCommand(messageEvent) {

    abstract val playTime: PlayTime

    override fun execute() {
        val botInVoice = Main.isConnectedToVoice(event)
        val requesterInVoice = event.messageAuthor.connectedVoiceChannel.isPresent
        if (requesterInVoice) {
            val songRequest = event.messageContent.substringAfter(' ').substringBefore("?playlist")
            if (botInVoice) {
                playOrLookupSong(songRequest)
            } else {
                event.messageAuthor.connectedVoiceChannel.get().connect().thenAccept {
                    audioManager.connect(event.server.get(), it)
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
            // TODO - Refactor scope/spotify repository
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
}