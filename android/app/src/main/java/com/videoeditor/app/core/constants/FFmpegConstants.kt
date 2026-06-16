package com.videoeditor.app.core.constants

object FFmpegConstants {
    const val VIDEO_CODEC = "libx264"
    const val AUDIO_CODEC = "aac"
    const val DEFAULT_FORMAT = "mp4"
    
    const val OUTPUT_DIR = "/storage/emulated/0/Android/data/com.videoeditor.app/files/Exports"
    const val TEMP_DIR = "/storage/emulated/0/Android/data/com.videoeditor.app/cache"
    
    // FFmpeg presets
    const val PRESET_ULTRAFAST = "ultrafast"
    const val PRESET_FAST = "fast"
    const val PRESET_MEDIUM = "medium"
    const val PRESET_SLOW = "slow"
    
    // Quality presets
    val QUALITY_480P = QualityPreset(854, 480, 4000000)
    val QUALITY_720P = QualityPreset(1280, 720, 8000000)
    val QUALITY_1080P = QualityPreset(1920, 1080, 15000000)
    val QUALITY_4K = QualityPreset(3840, 2160, 45000000)
    
    data class QualityPreset(
        val width: Int,
        val height: Int,
        val bitrate: Int
    )
}