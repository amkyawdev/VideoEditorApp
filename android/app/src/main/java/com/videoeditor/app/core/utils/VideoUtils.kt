package com.videoeditor.app.core.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri

object VideoUtils {

    fun getVideoInfo(context: Context, uri: Uri): VideoInfo? {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)

            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
            val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
            val rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toIntOrNull() ?: 0
            val bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toIntOrNull() ?: 0

            retriever.release()

            VideoInfo(duration, width, height, rotation, bitrate)
        } catch (e: Exception) {
            null
        }
    }

    fun formatDuration(durationMs: Long): String {
        val hours = durationMs / 3600000
        val minutes = (durationMs % 3600000) / 60000
        val seconds = (durationMs % 60000) / 1000

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    fun formatDurationWithMs(durationMs: Long): String {
        val minutes = durationMs / 60000
        val seconds = (durationMs % 60000) / 1000
        val ms = durationMs % 1000
        return String.format("%02d:%02d.%03d", minutes, seconds, ms)
    }

    data class VideoInfo(
        val duration: Long,
        val width: Int,
        val height: Int,
        val rotation: Int,
        val bitrate: Int
    ) {
        val aspectRatio: Float
            get() = if (height > 0) width.toFloat() / height else 16f / 9f

        val resolution: String
            get() = "${width}x${height}"

        val quality: String
            get() = when {
                height >= 2160 -> "4K"
                height >= 1440 -> "2K"
                height >= 1080 -> "FHD"
                height >= 720 -> "HD"
                height >= 480 -> "SD"
                else -> "Low"
            }
    }
}