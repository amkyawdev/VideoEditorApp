package com.videoeditor.app.domain.usecase

import com.videoeditor.app.core.ffmpeg.FFmpegService
import com.videoeditor.app.domain.model.Subtitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BurnSubtitleUseCase @Inject constructor(
    private val ffmpegService: FFmpegService
) {
    suspend operator fun invoke(
        videoPath: String,
        subtitles: List<Subtitle>,
        outputPath: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val srtPath = ffmpegService.createSubtitleFile(subtitles)
            val result = ffmpegService.burnSubtitles(
                videoPath = videoPath,
                subtitlePath = srtPath,
                outputPath = outputPath
            )
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}