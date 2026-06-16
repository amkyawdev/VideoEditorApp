package com.videoeditor.app.core.ffmpeg

import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.ReturnCode
import com.videoeditor.app.core.constants.FFmpegConstants
import com.videoeditor.app.domain.model.Subtitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FFmpegService @Inject constructor() {

    init {
        FFmpegKitConfig.enableLogCallback { logMessage ->
            android.util.Log.d("FFmpeg", logMessage.text)
        }
    }

    suspend fun trimVideo(
        inputPath: String,
        startMs: Long,
        endMs: Long,
        outputPath: String? = null
    ): String = withContext(Dispatchers.IO) {
        val output = outputPath ?: generateOutputPath("trim")
        val startSec = startMs / 1000.0
        val durationSec = (endMs - startMs) / 1000.0

        val command = arrayOf(
            "-y",
            "-ss", startSec.toString(),
            "-i", inputPath,
            "-t", durationSec.toString(),
            "-c:v", FFmpegConstants.VIDEO_CODEC,
            "-preset", "ultrafast",
            "-c:a", "aac",
            "-b:a", "128k",
            output
        )

        executeCommand(command)
        output
    }

    suspend fun mergeVideos(
        inputPaths: List<String>,
        outputPath: String
    ): String = withContext(Dispatchers.IO) {
        if (inputPaths.size < 2) {
            return@withContext inputPaths.firstOrNull() ?: throw IllegalArgumentException("No input files")
        }

        // Create concat file
        val concatFile = File.createTempFile("concat", ".txt")
        concatFile.writeText(inputPaths.joinToString("\n") { "file '$it'" })

        val command = arrayOf(
            "-y",
            "-f", "concat",
            "-safe", "0",
            "-i", concatFile.absolutePath,
            "-c:v", FFmpegConstants.VIDEO_CODEC,
            "-preset", "ultrafast",
            "-c:a", "aac",
            "-b:a", "128k",
            outputPath
        )

        executeCommand(command)
        concatFile.delete()
        outputPath
    }

    suspend fun addAudio(
        videoPath: String,
        audioPath: String,
        audioStartMs: Long,
        audioVolume: Float,
        outputPath: String
    ): String = withContext(Dispatchers.IO) {
        val command = arrayOf(
            "-y",
            "-i", videoPath,
            "-i", audioPath,
            "-filter_complex", "[1:a]adelay=${audioStartMs}|${audioStartMs},volume=$audioVolume[a]",
            "-map", "0:v",
            "-map", "[a]",
            "-c:v", "copy",
            "-c:a", "aac",
            "-b:a", "192k",
            outputPath
        )

        executeCommand(command)
        outputPath
    }

    suspend fun burnSubtitles(
        videoPath: String,
        subtitlePath: String,
        outputPath: String
    ): String = withContext(Dispatchers.IO) {
        val command = arrayOf(
            "-y",
            "-i", videoPath,
            "-vf", "subtitles=$subtitlePath",
            "-c:v", FFmpegConstants.VIDEO_CODEC,
            "-preset", "ultrafast",
            "-c:a", "aac",
            "-b:a", "128k",
            outputPath
        )

        executeCommand(command)
        outputPath
    }

    suspend fun applyFilter(
        inputPath: String,
        filter: String,
        outputPath: String
    ): String = withContext(Dispatchers.IO) {
        val command = arrayOf(
            "-y",
            "-i", inputPath,
            "-vf", filter,
            "-c:v", FFmpegConstants.VIDEO_CODEC,
            "-preset", "ultrafast",
            "-c:a", "copy",
            outputPath
        )

        executeCommand(command)
        outputPath
    }

    suspend fun changeSpeed(
        inputPath: String,
        speed: Float,
        outputPath: String
    ): String = withContext(Dispatchers.IO) {
        val pts = 1.0 / speed
        val command = arrayOf(
            "-y",
            "-i", inputPath,
            "-filter:v", "setpts=${pts}*PTS",
            "-filter:a", "atempo=$speed",
            "-c:v", FFmpegConstants.VIDEO_CODEC,
            "-preset", "ultrafast",
            "-c:a", "aac",
            outputPath
        )

        executeCommand(command)
        outputPath
    }

    suspend fun rotateVideo(
        inputPath: String,
        degrees: Int,
        outputPath: String
    ): String = withContext(Dispatchers.IO) {
        val rotation = when (degrees) {
            90 -> "transpose=1"
            180 -> "transpose=2,transpose=2"
            270 -> "transpose=2"
            else -> ""
        }
        
        if (rotation.isEmpty()) {
            return@withContext inputPath
        }

        val command = arrayOf(
            "-y",
            "-i", inputPath,
            "-vf", rotation,
            "-c:v", FFmpegConstants.VIDEO_CODEC,
            "-preset", "ultrafast",
            "-c:a", "copy",
            outputPath
        )

        executeCommand(command)
        outputPath
    }

    fun createSubtitleFile(subtitles: List<Subtitle>): String {
        val file = File.createTempFile("subtitles", ".srt")
        val content = subtitles.mapIndexed { index, subtitle ->
            val startTime = formatSrtTime(subtitle.startTimeMs)
            val endTime = formatSrtTime(subtitle.endTimeMs)
            """
            |${index + 1}
            |$startTime --> $endTime
            |${subtitle.text}
            |""".trimMargin()
        }.joinToString("\n")
        file.writeText(content)
        return file.absolutePath
    }

    private fun formatSrtTime(ms: Long): String {
        val hours = ms / 3600000
        val minutes = (ms % 3600000) / 60000
        val seconds = (ms % 60000) / 1000
        val millis = ms % 1000
        return String.format("%02d:%02d:%02d,%03d", hours, minutes, seconds, millis)
    }

    private fun executeCommand(args: Array<String>): Int {
        val session = FFmpegKit.execute(args)
        val returnCode = session.returnCode
        if (!ReturnCode.isSuccess(returnCode)) {
            throw RuntimeException("FFmpeg execution failed with return code: $returnCode")
        }
        return returnCode
    }

    private fun generateOutputPath(prefix: String): String {
        val cacheDir = File(FFmpegConstants.OUTPUT_DIR)
        if (!cacheDir.exists()) cacheDir.mkdirs()
        return File(cacheDir, "${prefix}_${System.currentTimeMillis()}.mp4").absolutePath
    }

    data class VideoInfo(
        val duration: Long,
        val width: Int,
        val height: Int,
        val bitrate: Int
    )
}