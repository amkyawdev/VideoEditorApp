package com.videoeditor.app.domain.usecase

import com.videoeditor.app.core.ffmpeg.RenderEngine
import com.videoeditor.app.domain.model.AudioTrack
import com.videoeditor.app.domain.model.Effect
import com.videoeditor.app.domain.model.ExportSettings
import com.videoeditor.app.domain.model.Subtitle
import com.videoeditor.app.domain.model.VideoClip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExportVideoUseCase @Inject constructor(
    private val renderEngine: RenderEngine
) {
    suspend operator fun invoke(
        clips: List<VideoClip>,
        audioTracks: List<AudioTrack>,
        subtitles: List<Subtitle>,
        effects: List<Effect>,
        settings: ExportSettings,
        outputPath: String,
        onProgress: ((Float) -> Unit)? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val result = renderEngine.render(
                clips = clips,
                audioTracks = audioTracks,
                subtitles = subtitles,
                effects = effects,
                settings = settings,
                outputPath = outputPath,
                progressCallback = onProgress
            )
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}