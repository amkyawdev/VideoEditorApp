package com.videoeditor.app.domain.repository

import com.videoeditor.app.domain.model.Project
import com.videoeditor.app.domain.model.VideoClip
import com.videoeditor.app.domain.model.AudioTrack
import com.videoeditor.app.domain.model.Subtitle
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun getAllProjects(): Flow<List<Project>>
    fun getProjectById(id: String): Flow<Project?>
    suspend fun insertProject(project: Project)
    suspend fun updateProject(project: Project)
    suspend fun deleteProject(id: String)
    suspend fun getVideoClips(projectId: String): List<VideoClip>
    suspend fun insertVideoClip(clip: VideoClip)
    suspend fun updateVideoClip(clip: VideoClip)
    suspend fun deleteVideoClip(id: String)
    suspend fun getAudioTracks(projectId: String): List<AudioTrack>
    suspend fun insertAudioTrack(track: AudioTrack)
    suspend fun updateAudioTrack(track: AudioTrack)
    suspend fun deleteAudioTrack(id: String)
    suspend fun getSubtitles(projectId: String): List<Subtitle>
    suspend fun insertSubtitle(subtitle: Subtitle)
    suspend fun updateSubtitle(subtitle: Subtitle)
    suspend fun deleteSubtitle(id: String)
}