package com.videoeditor.app.core.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileUtils @Inject constructor() {

    fun getProjectDirectory(context: Context, projectId: String): File {
        val dir = File(context.filesDir, "projects/$projectId")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getExportDirectory(context: Context): File {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "Exports")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getTempDirectory(context: Context): File {
        val dir = File(context.cacheDir, "temp")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun copyMediaToProject(context: Context, sourceUri: Uri, projectId: String): String? {
        return try {
            val projectDir = getProjectDirectory(context, projectId)
            val extension = getFileExtension(context, sourceUri)
            val fileName = "${UUID.randomUUID()}.$extension"
            val destFile = File(projectDir, fileName)

            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }

            destFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    fun deleteProject(context: Context, projectId: String): Boolean {
        return try {
            val projectDir = getProjectDirectory(context, projectId)
            projectDir.deleteRecursively()
        } catch (e: Exception) {
            false
        }
    }

    fun getFileSize(path: String): Long {
        return try {
            File(path).length()
        } catch (e: Exception) {
            0L
        }
    }

    fun formatFileSize(size: Long): String {
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> String.format("%.1f KB", size / 1024.0)
            size < 1024 * 1024 * 1024 -> String.format("%.1f MB", size / (1024.0 * 1024))
            else -> String.format("%.2f GB", size / (1024.0 * 1024 * 1024))
        }
    }

    fun getFileExtension(context: Context, uri: Uri): String {
        return context.contentResolver.getType(uri)?.substringAfterLast("/") ?: "mp4"
    }

    fun cleanTempFiles(context: Context) {
        try {
            getTempDirectory(context).listFiles()?.forEach { file ->
                if (file.lastModified() < System.currentTimeMillis() - 24 * 60 * 60 * 1000) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            // Ignore cleanup errors
        }
    }
}