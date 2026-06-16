package com.videoeditor.app.data.local.dao

import androidx.room.*
import com.videoeditor.app.domain.model.Project
import com.videoeditor.app.domain.model.VideoClip
import com.videoeditor.app.domain.model.AudioTrack
import com.videoeditor.app.domain.model.Subtitle
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<Project>>

    @Query("SELECT * FROM projects WHERE id = :id")
    fun getProjectById(id: String): Flow<Project?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project)

    @Update
    suspend fun updateProject(project: Project)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProject(id: String)

    @Query("SELECT * FROM video_clips WHERE projectId = :projectId ORDER BY orderIndex")
    suspend fun getVideoClips(projectId: String): List<VideoClip>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideoClip(clip: VideoClip)

    @Update
    suspend fun updateVideoClip(clip: VideoClip)

    @Query("DELETE FROM video_clips WHERE id = :id")
    suspend fun deleteVideoClip(id: String)

    @Query("SELECT * FROM audio_tracks WHERE projectId = :projectId ORDER BY orderIndex")
    suspend fun getAudioTracks(projectId: String): List<AudioTrack>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudioTrack(track: AudioTrack)

    @Update
    suspend fun updateAudioTrack(track: AudioTrack)

    @Query("DELETE FROM audio_tracks WHERE id = :id")
    suspend fun deleteAudioTrack(id: String)

    @Query("SELECT * FROM subtitles WHERE projectId = :projectId ORDER BY startTimeMs")
    suspend fun getSubtitles(projectId: String): List<Subtitle>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtitle(subtitle: Subtitle)

    @Update
    suspend fun updateSubtitle(subtitle: Subtitle)

    @Query("DELETE FROM subtitles WHERE id = :id")
    suspend fun deleteSubtitle(id: String)
}