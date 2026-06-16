package com.videoeditor.app.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThumbnailGenerator @Inject constructor() {

    suspend fun generateThumbnail(
        context: Context,
        uri: Uri,
        timeMs: Long = 0
    ): String? = withContext(Dispatchers.IO) {
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            
            val bitmap = retriever.getFrameAtTime(
                timeMs * 1000,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC
            )
            
            retriever.release()
            
            bitmap?.let { saveThumbnail(context, it) }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun generateThumbnails(
        context: Context,
        uri: Uri,
        count: Int = 10
    ): List<String> = withContext(Dispatchers.IO) {
        val thumbnails = mutableListOf<String>()
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            
            val duration = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )?.toLongOrNull() ?: 0L
            
            if (duration > 0) {
                val interval = duration / count
                for (i in 0 until count) {
                    val timeMs = i * interval
                    val bitmap = retriever.getFrameAtTime(
                        timeMs * 1000,
                        MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                    )
                    bitmap?.let {
                        saveThumbnail(context, it)?.let { path ->
                            thumbnails.add(path)
                        }
                    }
                }
            }
            
            retriever.release()
        } catch (e: Exception) {
            // Return whatever was generated
        }
        thumbnails
    }

    private fun saveThumbnail(context: Context, bitmap: Bitmap): String? {
        return try {
            val thumbnailDir = File(context.cacheDir, "thumbnails")
            if (!thumbnailDir.exists()) thumbnailDir.mkdirs()
            
            val file = File(thumbnailDir, "${UUID.randomUUID()}.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    fun getVideoDuration(context: Context, uri: Uri): Long {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, uri)
            val duration = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )?.toLongOrNull() ?: 0L
            retriever.release()
            duration
        } catch (e: Exception) {
            0L
        }
    }
}