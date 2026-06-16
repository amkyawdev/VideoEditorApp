package com.videoeditor.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.videoeditor.app.data.local.dao.ProjectDao
import com.videoeditor.app.domain.model.Project
import com.videoeditor.app.domain.model.VideoClip
import com.videoeditor.app.domain.model.AudioTrack
import com.videoeditor.app.domain.model.Subtitle

@Database(
    entities = [
        Project::class,
        VideoClip::class,
        AudioTrack::class,
        Subtitle::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao

    companion object {
        const val DATABASE_NAME = "video_editor_db"
    }
}