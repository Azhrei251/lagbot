package com.azhapps.lagbot.local

import com.azhapps.lagbot.utils.PropertiesUtil
import com.sedmelluq.discord.lavaplayer.container.MediaContainerDetection
import com.sedmelluq.discord.lavaplayer.container.MediaContainerHints
import com.sedmelluq.discord.lavaplayer.container.MediaContainerRegistry
import com.sedmelluq.discord.lavaplayer.source.local.LocalSeekableInputStream
import com.sedmelluq.discord.lavaplayer.track.AudioReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.apache.commons.text.similarity.FuzzyScore
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

private const val FUZZY_THRESHOLD_FACTOR = 1.8
private const val MIN_TOTAL_LENGTH_TO_CONSIDER = 4

class LocalTrackChecker(
    private val ioScope: CoroutineScope,
) {

    // TODO Some sort of save to disk + periodic update (or folder watching)
    private val localTracks: MutableList<LocalAudioTrackInfo> = mutableListOf()
    private val scorer: FuzzyScore = FuzzyScore(Locale.getDefault())
    private val logger = LoggerFactory.getLogger(this::class.java)
    private var initFinished: Boolean = false

    init {
        val initStart = System.currentTimeMillis()
        val jobList: MutableList<Job> = mutableListOf()
        val audioDir = File(PropertiesUtil.get(PropertiesUtil.LOCAL_AUDIO_DIR))
        if (audioDir.isDirectory && audioDir.canRead()) {
            ioScope.launch {
                audioDir.listFiles().forEach {
                    jobList.add(ioScope.launch {
                        if (it.isDirectory) {
                            it.walkTopDown().forEach {
                                parseFile(it)
                            }
                        } else {
                            parseFile(it)
                        }
                    })
                }
                jobList.joinAll()
                val initDuration = System.currentTimeMillis() - initStart
                initFinished = true
                logger.info("Init finished in ${initDuration}ms")
            }
        }
    }

    private fun parseFile(file: File) {
        if (!file.isDirectory && file.exists() && file.isFile && file.canRead()) {
            try {
                val lastDotIndex: Int = file.name.lastIndexOf('.')
                val fileExtension: String? = if (lastDotIndex >= 0) file.name.substring(lastDotIndex + 1) else null

                val detectionResult = MediaContainerDetection(
                    MediaContainerRegistry.DEFAULT_REGISTRY,
                    AudioReference(file.name, null),
                    LocalSeekableInputStream(file),
                    MediaContainerHints.from(null, fileExtension)
                ).detectContainer()

                if (detectionResult.isContainerDetected && detectionResult.isSupportedFile) {
                    localTracks.add(LocalAudioTrackInfo(detectionResult.trackInfo, file.absolutePath))
                }
            } catch (e: Exception) {
                logger.info("Unable to parse file ${file.absoluteFile}, reason: ${e.message}")
            }
        }
    }

    fun fuzzyLocalSearch(identifier: String): String? {
        // Don't attempt to search locally for particularly short identifiers.
        if (!initFinished || identifier.length < MIN_TOTAL_LENGTH_TO_CONSIDER) return null

        val searchStartTime = System.currentTimeMillis()

        val splitIdentifier = identifier.split("\\s+".toRegex())

        var max: Int = 0
        var maxIndex: Int = -1
        localTracks.forEachIndexed { i, localAudioTrackInfo ->
            val trackScore = splitIdentifier.sumOf { piece ->
                scorer.fuzzyScore(localAudioTrackInfo.trackInfo.title, piece) +
                        scorer.fuzzyScore(localAudioTrackInfo.trackInfo.author, piece)
            }
            if (trackScore > max) {
                max = trackScore
                maxIndex = i
            }
        }
        val result = if (maxIndex != -1 && max >= identifier.length * FUZZY_THRESHOLD_FACTOR)
            localTracks[maxIndex].filePath
        else null
        logger.info("Search finished, took ${System.currentTimeMillis() - searchStartTime}ms. Found result: $result")
        return result
    }
}