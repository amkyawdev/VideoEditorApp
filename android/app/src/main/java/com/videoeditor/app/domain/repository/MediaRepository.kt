package com.videoeditor.app.domain.repository

import android.net.Uri

interface MediaRepository {
    suspend fun getVideoDuration(uri: Uri): Long
    suspend fun generateThumbnail(uri: Uri, timeMs: Long): String?
    suspend fun copyMediaToProject(sourceUri: Uri, projectId: String): String?
    suspend fun getMediaMetadata(uri: Uri): MediaMetadata?
}

data class MediaMetadata(
    val duration: Long,
    val width: Int,
    val height: Int,
    val rotation: Int,
    val bitrate: Int,
    val frameRate: Float
)