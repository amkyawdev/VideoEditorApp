package com.videoeditor.app.core.ffmpeg

import com.videoeditor.app.domain.model.Subtitle
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubtitleProcessor @Inject constructor(
    private val ffmpegService: FFmpegService
) {
    fun createSrtFile(subtitles: List<Subtitle>): String {
        return ffmpegService.createSubtitleFile(subtitles)
    }

    fun createAssFile(subtitles: List<Subtitle>): String {
        val file = File.createTempFile("subtitles", ".ass")
        val content = buildAssContent(subtitles)
        file.writeText(content)
        return file.absolutePath
    }

    private fun buildAssContent(subtitles: List<Subtitle>): String {
        val sb = StringBuilder()
        sb.appendLine("[Script Info]")
        sb.appendLine("Title: Video Editor Subtitles")
        sb.appendLine("ScriptType: v4.00+")
        sb.appendLine("Collisions: Normal")
        sb.appendLine("PlayDepth: 0")
        sb.appendLine()
        sb.appendLine("[V4+ Styles]")
        sb.appendLine("Format: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, Bold, Italic, Underline, StrikeOut, ScaleX, ScaleY, Spacing, Angle, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, Encoding")
        sb.appendLine("Style: Default,Arial,48,&H00FFFFFF,&H000000FF,&H00000000,&H80000000,-1,0,0,0,100,100,0,0,1,3,3,2,10,10,10,1")
        sb.appendLine()
        sb.appendLine("[Events]")
        sb.appendLine("Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text")

        subtitles.forEach { subtitle ->
            val startTime = formatAssTime(subtitle.startTimeMs)
            val endTime = formatAssTime(subtitle.endTimeMs)
            val text = subtitle.text.replace("\n", "\\N")
            sb.appendLine("Dialogue: 0,$startTime,$endTime,Default,,0,0,0,,$text")
        }

        return sb.toString()
    }

    private fun formatAssTime(ms: Long): String {
        val hours = ms / 3600000
        val minutes = (ms % 3600000) / 60000
        val seconds = (ms % 60000) / 1000
        val centis = (ms % 1000) / 10
        return String.format("%d:%02d:%02d.%02d", hours, minutes, seconds, centis)
    }

    suspend fun burnSubtitles(
        videoPath: String,
        subtitles: List<Subtitle>,
        outputPath: String
    ): String {
        val srtPath = createSrtFile(subtitles)
        return ffmpegService.burnSubtitles(videoPath, srtPath, outputPath)
    }
}