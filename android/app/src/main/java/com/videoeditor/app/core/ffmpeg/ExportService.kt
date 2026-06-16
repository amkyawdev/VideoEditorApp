package com.videoeditor.app.core.ffmpeg

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.videoeditor.app.R
import com.videoeditor.app.domain.model.ExportFormat
import com.videoeditor.app.domain.model.ExportQuality
import com.videoeditor.app.domain.model.ExportSettings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class ExportService : Service() {

    @Inject
    lateinit var renderEngine: RenderEngine

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var exportJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification(0)
        startForeground(NOTIFICATION_ID, notification)

        intent?.let { i ->
            val videoPath = i.getStringExtra(EXTRA_VIDEO_PATH) ?: return@let
            val outputPath = i.getStringExtra(EXTRA_OUTPUT_PATH) ?: return@let
            val quality = i.getStringExtra(EXTRA_QUALITY) ?: "MEDIUM"
            val format = i.getStringExtra(EXTRA_FORMAT) ?: "MP4"

            startExport(videoPath, outputPath, quality, format)
        }

        return START_NOT_STICKY
    }

    private fun startExport(
        videoPath: String,
        outputPath: String,
        quality: String,
        format: String
    ) {
        exportJob?.cancel()
        exportJob = serviceScope.launch {
            try {
                val settings = ExportSettings(
                    quality = ExportQuality.valueOf(quality),
                    format = ExportFormat.valueOf(format)
                )

                // Export implementation would go here
                // For now, just simulate progress
                for (progress in 0..100 step 5) {
                    updateNotification(progress)
                    delay(200)
                }

                sendBroadcast(Intent(ACTION_EXPORT_COMPLETE).apply {
                    putExtra(EXTRA_OUTPUT_PATH, outputPath)
                })
            } catch (e: Exception) {
                sendBroadcast(Intent(ACTION_EXPORT_ERROR).apply {
                    putExtra(EXTRA_ERROR_MESSAGE, e.message)
                })
            } finally {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_export),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_export_desc)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(progress: Int): android.app.Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_export_title))
            .setContentText(getString(R.string.notification_export_progress, progress))
            .setSmallIcon(R.drawable.ic_export)
            .setProgress(100, progress, progress == 0)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(progress: Int) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, createNotification(progress))
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val CHANNEL_ID = "export_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_EXPORT_COMPLETE = "com.videoeditor.app.EXPORT_COMPLETE"
        const val ACTION_EXPORT_ERROR = "com.videoeditor.app.EXPORT_ERROR"

        const val EXTRA_VIDEO_PATH = "video_path"
        const val EXTRA_OUTPUT_PATH = "output_path"
        const val EXTRA_QUALITY = "quality"
        const val EXTRA_FORMAT = "format"
        const val EXTRA_ERROR_MESSAGE = "error_message"
    }
}