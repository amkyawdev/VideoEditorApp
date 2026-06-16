package com.videoeditor.app.core.ffmpeg

import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import com.videoeditor.app.core.constants.FFmpegConstants
import com.videoeditor.app.domain.model.AudioTrack
import com.videoeditor.app.domain.model.Effect
import com.videoeditor.app.domain.model.ExportSettings
import com.videoeditor.app.domain.model.Subtitle
import com.videoeditor.app.domain.model.VideoClip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RenderEngine @Inject constructor(
    private val ffmpegService: FFmpegService,
    private val videoProcessor: VideoProcessor,
    private val audioProcessor: AudioProcessor,
    private val subtitleProcessor: SubtitleProcessor,
    private val effectProcessor: EffectProcessor
) {
    suspend fun render(
        clips: List<VideoClip>,
        audioTracks: List<AudioTrack>,
        subtitles: List<Subtitle>,
        effects: List<Effect>,
        settings: ExportSettings,
        outputPath: String,
        progressCallback: ((Float) -> Unit)?
    ): String = withContext(Dispatchers.IO) {
        if (clips.isEmpty()) {
            throw IllegalArgumentException("No video clips to render")
        }

        var currentPath = clips.first().sourcePath
        var currentClips = clips

        // Step 1: Trim and merge clips if needed
        if (clips.size > 1 || clips.any { it.trimStartMs > 0 || it.trimEndMs < it.duration }) {
            currentPath = processClips(currentClips, progressCallback)
        }

        // Step 2: Apply effects
        if (effects.isNotEmpty()) {
            val filteredEffects = effects.filter { it.type != com.videoeditor.app.domain.model.EffectType.NONE }
            if (filteredEffects.isNotEmpty()) {
                val effectPath = getTempPath("effect")
                currentPath = effectProcessor.applyEffects(currentPath, filteredEffects, effectPath, progressCallback)
            }
        }

        // Step 3: Mix audio tracks
        if (audioTracks.isNotEmpty()) {
            val audioPath = getTempPath("audio")
            currentPath = audioProcessor.mixAudio(currentPath, audioTracks, audioPath)
        }

        // Step 4: Burn subtitles
        if (subtitles.isNotEmpty()) {
            val subtitlePath = getTempPath("subtitle")
            currentPath = subtitleProcessor.burnSubtitles(currentPath, subtitles, subtitlePath)
        }

        // Step 5: Final export with quality settings
        val finalPath = if (currentPath != outputPath) {
            val exportPath = getTempPath("export")
            videoProcessor.processVideo(currentPath, exportPath, settings, progressCallback)
        } else {
            currentPath
        }

        // Move to final destination if different
        if (exportPath != outputPath) {
            File(exportPath).copyTo(File(outputPath), overwrite = true)
            File(exportPath).delete()
        }

        outputPath
    }

    private suspend fun processClips(
        clips: List<VideoClip>,
        callback: ((Float) -> Unit)?
    ): String {
        val processedPaths = mutableListOf<String>()

        clips.forEachIndexed { index, clip ->
            callback?.invoke((index.toFloat() / clips.size) * 0.5f)

            val trimmedPath = if (clip.trimStartMs > 0 || clip.trimEndMs < clip.duration) {
                ffmpegService.trimVideo(
                    inputPath = clip.sourcePath,
                    startMs = clip.trimStartMs,
                    endMs = clip.trimEndMs
                )
            } else {
                clip.sourcePath
            }

            processedPaths.add(trimmedPath)
        }

        return if (processedPaths.size > 1) {
            val mergedPath = getTempPath("merge")
            ffmpegService.mergeVideos(processedPaths, mergedPath)
        } else {
            processedPaths.first()
        }
    }

    private fun getTempPath(prefix: String): String {
        val cacheDir = File(FFmpegConstants.OUTPUT_DIR)
        if (!cacheDir.exists()) cacheDir.mkdirs()
        return File(cacheDir, "${prefix}_${UUID.randomUUID()}.${FFmpegConstants.DEFAULT_FORMAT}").absolutePath
    }
}