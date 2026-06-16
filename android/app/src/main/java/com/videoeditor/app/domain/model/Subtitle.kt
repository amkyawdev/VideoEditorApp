package com.videoeditor.app.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "subtitles",
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
data class Subtitle(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val text: String,
    val startTimeMs: Long,
    val endTimeMs: Long,
    val fontSize: Int = 24,
    val fontColor: String = "#FFFFFF",
    val backgroundColor: String = "#00000080",
    val position: SubtitlePosition = SubtitlePosition.BOTTOM,
    val isVisible: Boolean = true
)

enum class SubtitlePosition {
    TOP, CENTER, BOTTOM
}