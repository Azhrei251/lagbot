package com.azhapps.lagbot.local

import com.azhapps.lagbot.Main
import com.azhapps.lagbot.utils.PropertiesUtil
import com.sedmelluq.discord.lavaplayer.container.MediaContainerDetection
import com.sedmelluq.discord.lavaplayer.container.MediaContainerHints
import com.sedmelluq.discord.lavaplayer.container.MediaContainerRegistry
import com.sedmelluq.discord.lavaplayer.source.local.LocalSeekableInputStream
import com.sedmelluq.discord.lavaplayer.track.AudioReference
import org.apache.commons.text.similarity.FuzzyScore
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

class LocalTrackChecker {

    // TODO Some sort of save to disk + periodic update (or folder watching)
    private val localTracks: MutableList<LocalAudioTrackInfo> = mutableListOf()
    private val scorer: FuzzyScore = FuzzyScore(Locale.getDefault())
    private val logger = LoggerFactory.getLogger(Main::class.java)

    init {
        val audioDir = File(PropertiesUtil.get(PropertiesUtil.LOCAL_AUDIO_DIR))
        audioDir.walkTopDown().forEach { currentFile ->
            if (!currentFile.isDirectory && currentFile.exists() && currentFile.isFile && currentFile.canRead()) {
                try {
                    val lastDotIndex: Int = currentFile.name.lastIndexOf('.')
                    val fileExtension: String? = if (lastDotIndex >= 0) currentFile.name.substring(lastDotIndex + 1) else null

                     val detectionResult =MediaContainerDetection(
                        MediaContainerRegistry.DEFAULT_REGISTRY,
                        AudioReference(currentFile.name, null),
                        LocalSeekableInputStream(currentFile),
                        MediaContainerHints.from(null, fileExtension)
                    ).detectContainer()

                    if (detectionResult.isContainerDetected && detectionResult.isSupportedFile) {
                        localTracks.add(LocalAudioTrackInfo(detectionResult.trackInfo, currentFile.absolutePath))
                    }
                } catch (e: Exception) {
                    logger.info("Unable to parse file ${currentFile.absoluteFile}, reason: ${e.message}")
                }
            }
        }
        localTracks.forEach {
            println(it.filePath)
        }
    }

    fun fuzzyLocalSearch(identifier: String): String? {
        // Don't attempt to search locally for particularly short identifiers.
        if (identifier.length <= 3) return null

        val splitIdentifier = identifier.split("\\s+".toRegex())

        val scores = mutableListOf<Scores>()
        localTracks.forEach { localAudioTrackInfo ->
            scores.add(
                Scores(
                    titleScore = splitIdentifier.sumOf { piece ->
                        scorer.fuzzyScore(localAudioTrackInfo.trackInfo.title, piece)
                    },
                    artistScore =  splitIdentifier.sumOf { piece ->
                        scorer.fuzzyScore(localAudioTrackInfo.trackInfo.author, piece)
                    }
                )
            )
        }
        val max = scores.maxBy {
            it.total()
        }
        return if (max.total() >= identifier.length - 3) localTracks[scores.indexOf(max)].filePath else null
    }
}

data class Scores(
    val titleScore: Int,
    val artistScore: Int,
) {
    fun total() = titleScore + artistScore
}