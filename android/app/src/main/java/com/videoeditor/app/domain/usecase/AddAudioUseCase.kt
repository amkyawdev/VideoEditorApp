package com.videoeditor.app.domain.usecase

import com.videoeditor.app.core.ffmpeg.FFmpegService
import com.videoeditor.app.domain.model.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddAudioUseCase @Inject constructor(
    private val ffmpegService: FFmpegService
) {
    suspend operator fun invoke(
        videoPath: String,
        audioTrack: AudioTrack,
        outputPath: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val result = ffmpegService.addAudio(
                videoPath = videoPath,
                audioPath = audioTrack.sourcePath,
                audioStartMs = audioTrack.startTime,
                audioVolume = if (audioTrack.isMuted) 0f else audioTrack.volume,
                outputPath = outputPath
            )
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}