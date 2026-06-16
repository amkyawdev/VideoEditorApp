package com.videoeditor.app.domain.usecase

import com.videoeditor.app.core.ffmpeg.FFmpegService
import com.videoeditor.app.domain.model.VideoClip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TrimVideoUseCase @Inject constructor(
    private val ffmpegService: FFmpegService
) {
    suspend operator fun invoke(
        clip: VideoClip,
        trimStartMs: Long,
        trimEndMs: Long
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val outputPath = ffmpegService.trimVideo(
                inputPath = clip.sourcePath,
                startMs = trimStartMs,
                endMs = trimEndMs
            )
            Result.success(outputPath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}