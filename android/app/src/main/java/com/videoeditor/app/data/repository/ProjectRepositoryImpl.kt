package com.videoeditor.app.data.repository

import com.videoeditor.app.data.local.dao.ProjectDao
import com.videoeditor.app.domain.model.Project
import com.videoeditor.app.domain.model.VideoClip
import com.videoeditor.app.domain.model.AudioTrack
import com.videoeditor.app.domain.model.Subtitle
import com.videoeditor.app.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepositoryImpl @Inject constructor(
    private val projectDao: ProjectDao
) : ProjectRepository {

    override fun getAllProjects(): Flow<List<Project>> = projectDao.getAllProjects()

    override fun getProjectById(id: String): Flow<Project?> = projectDao.getProjectById(id)

    override suspend fun insertProject(project: Project) {
        projectDao.insertProject(project)
    }

    override suspend fun updateProject(project: Project) {
        projectDao.updateProject(project)
    }

    override suspend fun deleteProject(id: String) {
        projectDao.deleteProject(id)
    }

    override suspend fun getVideoClips(projectId: String): List<VideoClip> {
        return projectDao.getVideoClips(projectId)
    }

    override suspend fun insertVideoClip(clip: VideoClip) {
        projectDao.insertVideoClip(clip)
    }

    override suspend fun updateVideoClip(clip: VideoClip) {
        projectDao.updateVideoClip(clip)
    }

    override suspend fun deleteVideoClip(id: String) {
        projectDao.deleteVideoClip(id)
    }

    override suspend fun getAudioTracks(projectId: String): List<AudioTrack> {
        return projectDao.getAudioTracks(projectId)
    }

    override suspend fun insertAudioTrack(track: AudioTrack) {
        projectDao.insertAudioTrack(track)
    }

    override suspend fun updateAudioTrack(track: AudioTrack) {
        projectDao.updateAudioTrack(track)
    }

    override suspend fun deleteAudioTrack(id: String) {
        projectDao.deleteAudioTrack(id)
    }

    override suspend fun getSubtitles(projectId: String): List<Subtitle> {
        return projectDao.getSubtitles(projectId)
    }

    override suspend fun insertSubtitle(subtitle: Subtitle) {
        projectDao.insertSubtitle(subtitle)
    }

    override suspend fun updateSubtitle(subtitle: Subtitle) {
        projectDao.updateSubtitle(subtitle)
    }

    override suspend fun deleteSubtitle(id: String) {
        projectDao.deleteSubtitle(id)
    }
}