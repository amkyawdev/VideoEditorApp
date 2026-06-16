package com.videoeditor.app.domain.model

data class ExportSettings(
    val quality: ExportQuality = ExportQuality.MEDIUM,
    val format: ExportFormat = ExportFormat.MP4,
    val fps: Int = 30,
    val videoBitrate: Int = 8000000,
    val audioBitrate: Int = 192000,
    val audioSampleRate: Int = 44100
)

enum class ExportQuality(
    val displayName: String,
    val resolution: Pair<Int, Int>,
    val maxBitrate: Int
) {
    LOW("Low (480p)", Pair(854, 480), 4000000),
    MEDIUM("Medium (720p)", Pair(1280, 720), 8000000),
    HIGH("High (1080p)", Pair(1920, 1080), 15000000),
    ULTRA("Ultra (4K)", Pair(3840, 2160), 45000000)
}

enum class ExportFormat(val displayName: String, val extension: String, val codec: String) {
    MP4("MP4 (H.264)", "mp4", "libx264"),
    WEBM("WebM (VP9)", "webm", "libvpx-vp9"),
    GIF("GIF", "gif", "gif")
}