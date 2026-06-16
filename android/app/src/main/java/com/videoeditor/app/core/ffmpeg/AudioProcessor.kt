package com.videoeditor.app.core.ffmpeg

import com.videoeditor.app.domain.model.AudioTrack
import java.io.File
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
        
        // FFmpeg not available - copy video as fallback
        android.util.Log.w("AudioProcessor", "mixAudio - Using copy fallback")
        File(videoPath).copyTo(File(outputPath), overwrite = true)
        return outputPath
    }

    suspend fun adjustVolume(
        inputPath: String,
        volume: Float,
        outputPath: String
    ): String {
        // FFmpeg not available - copy file as fallback
        android.util.Log.w("AudioProcessor", "adjustVolume - Using copy fallback")
        File(inputPath).copyTo(File(outputPath), overwrite = true)
        return outputPath
    }
}
