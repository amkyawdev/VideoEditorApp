package com.videoeditor.app.presentation.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.videoeditor.app.core.utils.ThumbnailGenerator
import com.videoeditor.app.core.utils.VideoUtils
import com.videoeditor.app.domain.model.Project
import com.videoeditor.app.domain.model.VideoClip
import com.videoeditor.app.domain.repository.MediaRepository
import com.videoeditor.app.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadProjects()
    }

    private fun loadProjects() {
        viewModelScope.launch {
            projectRepository.getAllProjects().collectLatest { projects ->
                _projects.value = projects
            }
        }
    }

    fun refreshProjects() {
        loadProjects()
    }

    fun createProjectFromVideo(uri: Uri, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val videoInfo = VideoUtils.getVideoInfo(context, uri)
                val duration = videoInfo?.duration ?: 0L
                
                val project = Project(
                    name = "New Project",
                    duration = duration,
                    videoClipCount = 1
                )
                
                projectRepository.insertProject(project)
                
                // Copy video to project folder
                val videoPath = mediaRepository.copyMediaToProject(uri, project.id)
                if (videoPath != null) {
                    // Create initial video clip
                    val thumbnail = mediaRepository.generateThumbnail(uri, 0)
                    val clip = VideoClip(
                        projectId = project.id,
                        sourcePath = videoPath,
                        thumbnailPath = thumbnail,
                        duration = duration,
                        endTime = duration,
                        orderIndex = 0
                    )
                    projectRepository.insertVideoClip(clip)
                    
                    // Update project with thumbnail
                    projectRepository.updateProject(
                        project.copy(thumbnailPath = thumbnail)
                    )
                }
                
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            projectRepository.deleteProject(projectId)
        }
    }

    fun clearError() {
        _error.value = null
    }
}