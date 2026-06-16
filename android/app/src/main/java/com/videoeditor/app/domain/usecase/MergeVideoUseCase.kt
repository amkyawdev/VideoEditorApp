package com.videoeditor.app.domain.usecase

import com.videoeditor.app.core.ffmpeg.FFmpegService
import com.videoeditor.app.domain.model.VideoClip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MergeVideoUseCase @Inject constructor(
    private val ffmpegService: FFmpegService
) {
    suspend operator fun invoke(
        clips: List<VideoClip>,
        outputPath: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (clips.isEmpty()) {
                return@withContext Result.failure(IllegalArgumentException("No clips to merge"))
            }
            
            if (clips.size == 1) {
                return@withContext Result.success(clips.first().sourcePath)
            }

            val inputPaths = clips.map { it.sourcePath }
            val result = ffmpegService.mergeVideos(
                inputPaths = inputPaths,
                outputPath = outputPath
            )
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}