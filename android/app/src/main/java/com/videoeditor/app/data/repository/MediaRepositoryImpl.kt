package com.videoeditor.app.data.repository

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.videoeditor.app.core.utils.FileUtils
import com.videoeditor.app.core.utils.ThumbnailGenerator
import com.videoeditor.app.domain.repository.MediaMetadata
import com.videoeditor.app.domain.repository.MediaRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val thumbnailGenerator: ThumbnailGenerator,
    private val fileUtils: FileUtils
) : MediaRepository {

    override suspend fun getVideoDuration(uri: Uri): Long = withContext(Dispatchers.IO) {
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            retriever.release()
            duration?.toLongOrNull() ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    override suspend fun generateThumbnail(uri: Uri, timeMs: Long): String? = withContext(Dispatchers.IO) {
        try {
            thumbnailGenerator.generateThumbnail(context, uri, timeMs)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun copyMediaToProject(sourceUri: Uri, projectId: String): String? = withContext(Dispatchers.IO) {
        try {
            fileUtils.copyMediaToProject(context, sourceUri, projectId)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getMediaMetadata(uri: Uri): MediaMetadata? = withContext(Dispatchers.IO) {
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
            val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
            val rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toIntOrNull() ?: 0
            val bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toIntOrNull() ?: 0
            
            retriever.release()
            
            MediaMetadata(
                duration = duration,
                width = width,
                height = height,
                rotation = rotation,
                bitrate = bitrate,
                frameRate = 30f
            )
        } catch (e: Exception) {
            null
        }
    }
}