package com.videoeditor.app.presentation.ui.editor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.core.content.ContextCompat
import com.videoeditor.app.R
import com.videoeditor.app.core.constants.AppConstants
import com.videoeditor.app.core.utils.VideoUtils
import com.videoeditor.app.domain.model.VideoClip

class TimelineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val videoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.timeline_clip_video)
    }

    private val audioPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.timeline_clip_audio)
    }

    private val playheadPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.timeline_playhead)
        strokeWidth = 4f
    }

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.timeline_track)
    }

    private val selectionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.timeline_selection)
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.text_primary)
        textSize = 28f
    }

    private var clips = listOf<VideoClip>()
    private var selectedClip: VideoClip? = null
    private var currentTimeMs: Long = 0L
    private var durationMs: Long = 0L
    private var zoomLevel = 1.0f

    private val trackHeight = 120f
    private val trackMargin = 20f
    private val clipMargin = 4f
    private val playheadOffset = 100f

    private var onClipSelectedListener: ((VideoClip) -> Unit)? = null
    private var onSeekListener: ((Long) -> Unit)? = null

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            handleTap(e.x, e.y)
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            val newTime = msFromX(e2.x)
            onSeekListener?.invoke(newTime)
            return true
        }
    })

    private val scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            zoomLevel *= detector.scaleFactor
            zoomLevel = zoomLevel.coerceIn(
                AppConstants.MIN_ZOOM_LEVEL,
                AppConstants.MAX_ZOOM_LEVEL
            )
            invalidate()
            return true
        }
    })

    fun setClips(clips: List<VideoClip>) {
        this.clips = clips
        durationMs = clips.sumOf { it.effectiveDuration }
        invalidate()
    }

    fun setCurrentTime(timeMs: Long) {
        currentTimeMs = timeMs
        invalidate()
    }

    fun setSelectedClip(clip: VideoClip?) {
        selectedClip = clip
        invalidate()
    }

    fun setOnClipSelectedListener(listener: (VideoClip) -> Unit) {
        onClipSelectedListener = listener
    }

    fun setOnSeekListener(listener: (Long) -> Unit) {
        onSeekListener = listener
    }

    fun zoomIn() {
        zoomLevel = (zoomLevel * 1.2f).coerceAtMost(AppConstants.MAX_ZOOM_LEVEL)
        invalidate()
    }

    fun zoomOut() {
        zoomLevel = (zoomLevel / 1.2f).coerceAtLeast(AppConstants.MIN_ZOOM_LEVEL)
        invalidate()
    }

    val zoomLevelValue: Float get() = zoomLevel

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw background
        canvas.drawColor(ContextCompat.getColor(context, R.color.timeline_background))

        // Draw track background
        val trackRect = RectF(
            playheadOffset,
            trackMargin,
            width.toFloat() - trackMargin,
            trackMargin + trackHeight
        )
        canvas.drawRoundRect(trackRect, 8f, 8f, trackPaint)

        // Draw clips
        var xOffset = playheadOffset + clipMargin
        clips.forEach { clip ->
            val clipWidth = (clip.effectiveDuration * zoomLevel / 1000f)
            val clipRect = RectF(
                xOffset,
                trackMargin + clipMargin,
                xOffset + clipWidth,
                trackMargin + trackHeight - clipMargin
            )
            
            // Draw clip
            canvas.drawRoundRect(clipRect, 4f, 4f, videoPaint)

            // Draw clip name
            val clipName = clip.sourcePath.substringAfterLast("/")
            canvas.drawText(
                VideoUtils.formatDuration(clip.effectiveDuration),
                xOffset + 8f,
                trackMargin + trackHeight / 2,
                textPaint
            )

            // Draw selection
            if (clip == selectedClip) {
                canvas.drawRoundRect(clipRect, 4f, 4f, selectionPaint)
            }

            xOffset += clipWidth + clipMargin
        }

        // Draw playhead
        val playheadX = playheadOffset + (currentTimeMs * zoomLevel / 1000f)
        canvas.drawLine(playheadX, 0f, playheadX, height.toFloat(), playheadPaint)

        // Draw time markers
        drawTimeMarkers(canvas)
    }

    private fun drawTimeMarkers(canvas: Canvas) {
        val markerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ContextCompat.getColor(context, R.color.text_secondary)
            textSize = 20f
        }

        val intervalMs = when {
            zoomLevel < 0.5f -> 30000L
            zoomLevel < 1f -> 10000L
            zoomLevel < 2f -> 5000L
            else -> 1000L
        }

        var time = 0L
        while (time <= durationMs) {
            val x = playheadOffset + (time * zoomLevel / 1000f)
            if (x > playheadOffset && x < width - playheadOffset) {
                canvas.drawText(VideoUtils.formatDuration(time), x + 4f, height - 8f, markerPaint)
            }
            time += intervalMs
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        return true
    }

    private fun handleTap(x: Float, y: Float) {
        if (y < trackMargin || y > trackMargin + trackHeight) return

        var clipX = playheadOffset
        for (clip in clips) {
            val clipWidth = (clip.effectiveDuration * zoomLevel / 1000f)
            if (x >= clipX && x <= clipX + clipWidth) {
                onClipSelectedListener?.invoke(clip)
                selectedClip = clip
                invalidate()
                return
            }
            clipX += clipWidth + clipMargin
        }
    }

    private fun msFromX(x: Float): Long {
        return ((x - playheadOffset) * 1000 / zoomLevel).toLong().coerceIn(0, durationMs)
    }
}