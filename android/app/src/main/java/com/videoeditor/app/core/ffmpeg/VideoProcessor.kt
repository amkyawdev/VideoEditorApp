package com.videoeditor.app.core.ffmpeg

import com.videoeditor.app.core.constants.FFmpegConstants
import com.videoeditor.app.domain.model.Effect
import com.videoeditor.app.domain.model.EffectType
import com.videoeditor.app.domain.model.ExportSettings
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoProcessor @Inject constructor(
    private val ffmpegService: FFmpegService
) {
    fun buildVideoFilter(
        effects: List<Effect>,
        settings: ExportSettings
    ): String {
        val filters = mutableListOf<String>()
        
        effects.forEach { effect ->
            when (effect.type) {
                EffectType.BRIGHTNESS -> {
                    val value = (effect.intensity - 0.5f) * 2f
                    filters.add("eq=brightness=$value")
                }
                EffectType.CONTRAST -> {
                    val value = 1f + effect.intensity
                    filters.add("eq=contrast=$value")
                }
                EffectType.SATURATION -> {
                    val value = effect.intensity * 2f
                    filters.add("eq=saturation=$value")
                }
                EffectType.SEPIA -> {
                    filters.add("colorchannelmixer=.393:.769:.189:0:.349:.686:.168:0:.272:.534:.131")
                }
                EffectType.GRAYSCALE -> {
                    filters.add("hue=s=0")
                }
                EffectType.BLUR -> {
                    val radius = (effect.intensity * 10).toInt()
                    filters.add("boxblur=$radius:$radius")
                }
                EffectType.VIGNETTE -> {
                    val angle = effect.intensity * 90
                    filters.add("vignette=angle=${angle}d")
                }
                EffectType.NONE -> { /* No filter */ }
                else -> { /* Not implemented */ }
            }
        }

        // Add scaling for quality settings
        val (width, height) = settings.quality.resolution
        filters.add("scale=$width:$height:force_original_aspect_ratio=decrease,pad=$width:$height:(ow-iw)/2:(oh-ih)/2")

        return filters.joinToString(",")
    }

    suspend fun processVideo(
        inputPath: String,
        outputPath: String,
        settings: ExportSettings,
        progressCallback: ((Float) -> Unit)? = null
    ): String {
        val command = buildProcessCommand(inputPath, outputPath, settings)
        return executeWithProgress(command, progressCallback)
    }

    private fun buildProcessCommand(
        inputPath: String,
        outputPath: String,
        settings: ExportSettings
    ): String {
        val (width, height) = settings.quality.resolution
        
        return "-y -i \"$inputPath\" " +
                "-vf \"scale=$width:$height:force_original_aspect_ratio=decrease\" " +
                "-c:v ${settings.format.codec} -preset ultrafast -b:v ${settings.videoBitrate} " +
                "-c:a aac -b:a ${settings.audioBitrate} -ar ${settings.audioSampleRate} " +
                "-r ${settings.fps} \"$outputPath\""
    }

    private suspend fun executeWithProgress(
        command: String,
        callback: ((Float) -> Unit)?
    ): String {
        // For now, execute without detailed progress
        return ffmpegService.applyFilter(
            inputPath = command.substringAfter("-i \"").substringBefore("\""),
            filter = command.substringAfter("-vf \"").substringBefore("\""),
            outputPath = command.substringAfterLast("\"")
        )
    }
}