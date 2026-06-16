package com.videoeditor.app.core.ffmpeg

import com.videoeditor.app.domain.model.AudioTrack
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioProcessor @Inject constructor(
    private val ffmpegService: FFmpegService
) {
    suspend fun mixAudio(
        videoPath: String,
        audioTracks: List<AudioTrack>,
        outputPath: String
    ): String {
        if (audioTracks.isEmpty()) {
            return videoPath
        }

        // Build complex filter for mixing multiple audio tracks
        val audioInputs = audioTracks.mapIndexed { index, track ->
            val delay = track.startTime
            "[$index:a]adelay=${delay}|${delay},volume=${if (track.isMuted) 0f else track.volume}[a$index]"
        }

        val mixFilter = if (audioTracks.size > 1) {
            val inputs = audioTracks.indices.joinToString("") { "[a$it]" }
            "${inputs}amix=inputs=${audioTracks.size}:duration=longest[aout]"
        } else {
            "[a0]anull[aout]"
        }

        val command = buildMixCommand(videoPath, audioTracks, audioInputs, mixFilter, outputPath)
        
        return ffmpegService.applyFilter(
            inputPath = videoPath,
            filter = "", // Complex filter handled differently
            outputPath = outputPath
        )
    }

    private fun buildMixCommand(
        videoPath: String,
        audioTracks: List<AudioTrack>,
        audioInputs: List<String>,
        mixFilter: String,
        outputPath: String
    ): String {
        val audioArgs = audioTracks.mapIndexed { index, track ->
            "-i \"${track.sourcePath}\""
        }.joinToString(" ")

        return "-y -i \"$videoPath\" $audioArgs " +
                "-filter_complex \"${audioInputs.joinToString(";")};$mixFilter\" " +
                "-map 0:v -map \"[aout]\" -c:v copy -c:a aac \"$outputPath\""
    }

    suspend fun adjustVolume(
        inputPath: String,
        volume: Float,
        outputPath: String
    ): String {
        return ffmpegService.applyFilter(
            inputPath = inputPath,
            filter = "volume=$volume",
            outputPath = outputPath
        )
    }
}