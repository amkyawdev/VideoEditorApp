package com.videoeditor.app.core.constants

object AppConstants {
    const val APP_NAME = "Video Editor"
    const val APP_VERSION = "1.0.0"

    // File extensions
    const val VIDEO_EXTENSIONS = "mp4,mov,avi,mkv,webm,3gp"
    const val AUDIO_EXTENSIONS = "mp3,wav,aac,ogg,m4a,flac"
    const val IMAGE_EXTENSIONS = "jpg,jpeg,png,gif,webp"

    // Project settings
    const val DEFAULT_PROJECT_NAME = "Untitled Project"
    const val AUTO_SAVE_INTERVAL_MS = 30000L // 30 seconds
    const val MAX_PROJECTS = 100

    // Timeline settings
    const val MIN_ZOOM_LEVEL = 0.5f
    const val MAX_ZOOM_LEVEL = 5.0f
    const val DEFAULT_ZOOM_LEVEL = 1.0f
    const val SNAP_THRESHOLD_DP = 10f

    // Video settings
    const val DEFAULT_FPS = 30
    const val MIN_FPS = 15
    const val MAX_FPS = 60

    // Thumbnail settings
    const val THUMBNAIL_WIDTH = 160
    const val THUMBNAIL_HEIGHT = 90
    const val THUMBNAIL_CACHE_SIZE = 100

    // Export settings
    const val DEFAULT_VIDEO_BITRATE = 8000000
    const val DEFAULT_AUDIO_BITRATE = 192000
    const val DEFAULT_AUDIO_SAMPLE_RATE = 44100

    // Intent actions
    const val ACTION_IMPORT_VIDEO = "com.videoeditor.app.ACTION_IMPORT_VIDEO"
    const val ACTION_IMPORT_AUDIO = "com.videoeditor.app.ACTION_IMPORT_AUDIO"
    const val ACTION_IMPORT_IMAGE = "com.videoeditor.app.ACTION_IMPORT_IMAGE"
}