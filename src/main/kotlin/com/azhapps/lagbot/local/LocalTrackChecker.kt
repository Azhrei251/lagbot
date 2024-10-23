package com.azhapps.lagbot.local

import com.azhapps.lagbot.utils.PropertiesUtil
import com.sedmelluq.discord.lavaplayer.container.MediaContainerDetection
import com.sedmelluq.discord.lavaplayer.container.MediaContainerHints
import com.sedmelluq.discord.lavaplayer.container.MediaContainerRegistry
import com.sedmelluq.discord.lavaplayer.source.local.LocalSeekableInputStream
import com.sedmelluq.discord.lavaplayer.track.AudioReference
import kotlinx.coroutines.*
import org.apache.commons.text.similarity.FuzzyScore
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

private const val FUZZY_THRESHOLD_FACTOR = 1.8
private const val MIN_TOTAL_LENGTH_TO_CONSIDER = 4

class LocalTrackChecker(
    private val ioScope: CoroutineScope,
    private val audioDir: File = File(PropertiesUtil.get(PropertiesUtil.LOCAL_AUDIO_DIR)),
    private val scorer: FuzzyScore = FuzzyScore(Locale.getDefault()),
    private val logger: Logger = LoggerFactory.getLogger(LocalTrackChecker::class.java),
) {
    private var localTracks: MutableList<LocalAudioTrackInfo> = mutableListOf()
    private var lastLocalProcessingStartTime = -1L
    private val processingInterval = PropertiesUtil.get(PropertiesUtil.LOCAL_AUDIO_REFRESH_PERIOD).toLong()

    init {
        ioScope.launch {
            while (true) {
                if (System.currentTimeMillis() - lastLocalProcessingStartTime > processingInterval) {
                    processLocalAudio()
                }
                delay(60000L)
            }
        }
    }

    private fun processLocalAudio() {
        lastLocalProcessingStartTime = System.currentTimeMillis()

        if (audioDir.isDirectory && audioDir.canRead()) {
            ioScope.launch {
                val jobList: MutableList<Job> = mutableListOf()
                val newLocalTracks: MutableList<LocalAudioTrackInfo> = mutableListOf()

                // Split initialization between subdirectories of the root audio directory
                audioDir.listFiles().forEach {
                    jobList.add(ioScope.launch {
                        if (it.isDirectory) {
                            it.walkTopDown().forEach {
                                parseFile(it, newLocalTracks)
                            }
                        } else {
                            parseFile(it, newLocalTracks)
                        }
                    })
                }
                jobList.joinAll()

                localTracks = newLocalTracks
                logger.info("Processing finished in ${System.currentTimeMillis() - lastLocalProcessingStartTime}ms")
            }
        }
    }

    private fun parseFile(file: File, trackList: MutableList<LocalAudioTrackInfo>) {
        if (file.exists() && file.isFile && file.canRead()) {
            try {
                val lastDotIndex: Int = file.name.lastIndexOf('.')
                val fileExtension: String? = if (lastDotIndex >= 0) file.name.substring(lastDotIndex + 1) else null

                val detectionResult = MediaContainerDetection(
                    MediaContainerRegistry.DEFAULT_REGISTRY,
                    AudioReference(file.name, null),
                    LocalSeekableInputStream(file),
                    MediaContainerHints.from(null, fileExtension)
                ).detectContainer()

                if (detectionResult.isSupportedFile) {
                    trackList.add(LocalAudioTrackInfo(detectionResult.trackInfo, file.absolutePath))
                }
            } catch (e: Exception) {
                logger.info("Unable to parse file ${file.absoluteFile}, reason: ${e.message}")
            }
        }
    }

    fun fuzzyLocalSearch(identifier: String): String? {
        // Don't attempt to search locally for particularly short identifiers.
        if (identifier.length < MIN_TOTAL_LENGTH_TO_CONSIDER) return null

        val searchSpace = localTracks.toList()

        val searchStartTime = System.currentTimeMillis()
        val splitIdentifier = identifier.split("\\s+".toRegex())

        var max = 0
        var maxIndex: Int = -1
        searchSpace.forEachIndexed { i, localAudioTrackInfo ->
            val trackScore = splitIdentifier.sumOf { piece ->
                scorer.fuzzyScore(localAudioTrackInfo.trackInfo.title, piece) +
                        scorer.fuzzyScore(localAudioTrackInfo.trackInfo.author, piece)
            }
            if (trackScore > max) {
                max = trackScore
                maxIndex = i
            }
        }

        val result = if (maxIndex != -1 && max >= identifier.length * FUZZY_THRESHOLD_FACTOR) {
            searchSpace[maxIndex].filePath
        } else null

        logger.info("Search finished, took ${System.currentTimeMillis() - searchStartTime}ms. Found result: $result")
        return result
    }
}