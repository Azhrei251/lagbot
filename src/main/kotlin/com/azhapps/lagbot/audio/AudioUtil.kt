package com.azhapps.lagbot.audio

object AudioUtil {
    /*  private val logger = LoggerFactory.getLogger(AudioUtil::class.java)
      private val playerMap = mutableMapOf<Long, AudioPlayer>()
      private val schedulerMap = mutableMapOf<Long, TrackScheduler>()
      private val connectionMap = mutableMapOf<Long, AudioConnection>()
      private val playerManager by lazy {
          DefaultAudioPlayerManager().apply {
              registerSourceManager(YoutubeAudioSourceManager())
              registerSourceManager(BandcampAudioSourceManager())
              registerSourceManager(VimeoAudioSourceManager())
              registerSourceManager(TwitchStreamAudioSourceManager())
              registerSourceManager(SoundCloudAudioSourceManager.Builder().build())
          }
      }

      fun connect(server: Server, audioConnection: AudioConnection) {
          val player = playerMap[server.id] ?: playerManager.createPlayer().apply {
              playerMap[server.id] = this
          }
          audioConnection.setAudioSource(LavaPlayerAudioSource(Main.api, player))
          connectionMap[server.id] = audioConnection
          setupTimeout(server)
      }

      fun disconnect(server: Server) {
          connectionMap[server.id]?.close()
      }

      fun setupTimeout(server: Server) {
          Timer().schedule(object : TimerTask() {
              override fun run() {
                  if (playerMap[server.id]?.playingTrack == null) {
                      disconnect(server)
                      logger.info("Disconnected from server ${server.name}")
                  }
              }
          }, PropertiesUtil.get(PropertiesUtil.AFK_TIMEOUT).toLong())
      }

      fun playSong(
          server: Server,
          identifier: String,
          textChannel: TextChannel,
          playTime: PlayTime,
          onTrackAdded: (String) -> Unit,
      ) {
          val player = playerMap[server.id]!!
          val scheduler = schedulerMap[server.id] ?: TrackScheduler(player, textChannel).apply {
              schedulerMap[server.id] = this
              player.addListener(this)
          }

          //If we've got a valid URL, try to load it. Otherwise, do a youtube search
          var isSearch = false
          val identifierToUse = if (Patterns.WEB_URL.matcher(identifier).matches()) {
              identifier
          } else {
              isSearch = true
              "ytsearch:${identifier}"
          }

          playerManager.loadItem(identifierToUse, object : AudioLoadResultHandler {
              override fun trackLoaded(track: AudioTrack) {
                  addIndividualTrack(track, playTime, scheduler)
              }

              override fun playlistLoaded(playlist: AudioPlaylist) {
                  //Search results are returned as a playlist oddly. Only add the first. Otherwise add the whole playlist
                  if (isSearch) {
                      onTrackAdded(addIndividualTrack(playlist.tracks.first(), playTime, scheduler))

                  } else {
                      onTrackAdded("${playlist.tracks.size} tracks added to queue")
                  }
              }

              override fun noMatches() {
                  textChannel.sendMessage("Could not find that song")
              }

              override fun loadFailed(exception: FriendlyException?) {
                  textChannel.sendMessage("Something went wrong" + exception.toString())
              }

          })
      }

      private fun addIndividualTrack(
          track: AudioTrack,
          playTime: PlayTime,
          scheduler: TrackScheduler,
      ): String {
          val message = when (playTime) {
              PlayTime.IMMEDIATE -> scheduler.playImmediate(track)
              PlayTime.NEXT -> scheduler.playNext(track)
              PlayTime.QUEUED -> scheduler.addToQueue(track)
          }
          return message ?: ""
      }

      fun getScheduler(server: Server) = schedulerMap[server.id]
*/
      enum class PlayTime {
          QUEUED, IMMEDIATE, NEXT
      }
}
