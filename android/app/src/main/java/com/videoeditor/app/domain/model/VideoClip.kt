package com.videoeditor.app.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "video_clips",
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId")]
)
data class VideoClip(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val sourcePath: String,
    val thumbnailPath: String? = null,
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val duration: Long = 0L,
    val orderIndex: Int = 0,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long = 0L,
    val volume: Float = 1.0f,
    val speed: Float = 1.0f,
    val rotation: Int = 0,
    val flipHorizontal: Boolean = false,
    val flipVertical: Boolean = false,
    val effectId: String? = null,
    val transitionId: String? = null
) {
    val effectiveDuration: Long
        get() = ((trimEndMs - trimStartMs) / speed).toLong()
}