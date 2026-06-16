package com.videoeditor.app.presentation.ui.editor

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.videoeditor.app.domain.model.*
import com.videoeditor.app.domain.repository.MediaRepository
import com.videoeditor.app.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val mediaRepository: MediaRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val projectId: String = savedStateHandle.get<String>("project_id") ?: ""

    private val _project = MutableStateFlow<Project?>(null)
    val project: StateFlow<Project?> = _project.asStateFlow()

    private val _videoClips = MutableStateFlow<List<VideoClip>>(emptyList())
    val videoClips: StateFlow<List<VideoClip>> = _videoClips.asStateFlow()

    private val _audioTracks = MutableStateFlow<List<AudioTrack>>(emptyList())
    val audioTracks: StateFlow<List<AudioTrack>> = _audioTracks.asStateFlow()

    private val _subtitles = MutableStateFlow<List<Subtitle>>(emptyList())
    val subtitles: StateFlow<List<Subtitle>> = _subtitles.asStateFlow()

    private val _effects = MutableStateFlow<List<Effect>>(emptyList())
    val effects: StateFlow<List<Effect>> = _effects.asStateFlow()

    private val _currentTime = MutableStateFlow(0L)
    val currentTime: StateFlow<Long> = _currentTime.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _videoUrl = MutableStateFlow<String?>(null)
    val videoUrl: StateFlow<String?> = _videoUrl.asStateFlow()

    private val _selectedClip = MutableStateFlow<VideoClip?>(null)
    val selectedClip: StateFlow<VideoClip?> = _selectedClip.asStateFlow()

    private val undoStack = mutableListOf<UndoAction>()
    private val redoStack = mutableListOf<UndoAction>()

    fun loadProject(id: String) {
        viewModelScope.launch {
            projectRepository.getProjectById(id).collectLatest { project ->
                _project.value = project
            }
        }

        viewModelScope.launch {
            _videoClips.value = projectRepository.getVideoClips(id)
            _audioTracks.value = projectRepository.getAudioTracks(id)
            _subtitles.value = projectRepository.getSubtitles(id)
            updateDuration()
            updateVideoUrl()
        }
    }

    fun addVideoClip(uri: Uri, context: Context) {
        viewModelScope.launch {
            val duration = mediaRepository.getVideoDuration(uri)
            val thumbnail = mediaRepository.generateThumbnail(uri, 0)
            val path = mediaRepository.copyMediaToProject(uri, projectId)

            if (path != null) {
                val clip = VideoClip(
                    projectId = projectId,
                    sourcePath = path,
                    thumbnailPath = thumbnail,
                    duration = duration,
                    endTime = duration,
                    orderIndex = _videoClips.value.size
                )
                projectRepository.insertVideoClip(clip)
                _videoClips.value = projectRepository.getVideoClips(projectId)
                updateDuration()
                updateVideoUrl()
                pushUndo(UndoAction.AddClip(clip))
            }
        }
    }

    fun selectClip(clip: VideoClip) {
        _selectedClip.value = clip
    }

    fun deleteSelectedClip() {
        val clip = _selectedClip.value ?: return
        viewModelScope.launch {
            projectRepository.deleteVideoClip(clip.id)
            _videoClips.value = projectRepository.getVideoClips(projectId)
            _selectedClip.value = null
            updateDuration()
            updateVideoUrl()
            pushUndo(UndoAction.DeleteClip(clip))
        }
    }

    fun splitAtPlayhead() {
        val clip = _selectedClip.value ?: return
        val splitTime = _currentTime.value

        if (splitTime <= clip.trimStartMs || splitTime >= clip.trimEndMs) return

        viewModelScope.launch {
            // Create second clip
            val clip2 = clip.copy(
                id = UUID.randomUUID().toString(),
                trimStartMs = splitTime,
                orderIndex = clip.orderIndex + 1
            )

            // Update first clip
            val updatedClip = clip.copy(trimEndMs = splitTime)
            projectRepository.updateVideoClip(updatedClip)
            projectRepository.insertVideoClip(clip2)

            _videoClips.value = projectRepository.getVideoClips(projectId)
            pushUndo(UndoAction.SplitClip(clip, clip2))
        }
    }

    fun trimClip(clip: VideoClip, startMs: Long, endMs: Long) {
        viewModelScope.launch {
            val updatedClip = clip.copy(
                trimStartMs = startMs,
                trimEndMs = endMs
            )
            projectRepository.updateVideoClip(updatedClip)
            _videoClips.value = projectRepository.getVideoClips(projectId)
            _selectedClip.value = updatedClip
            updateDuration()
            pushUndo(UndoAction.TrimClip(clip, updatedClip))
        }
    }

    fun addAudioTrack(uri: Uri, context: Context) {
        viewModelScope.launch {
            val duration = mediaRepository.getVideoDuration(uri)
            val path = mediaRepository.copyMediaToProject(uri, projectId)

            if (path != null) {
                val track = AudioTrack(
                    projectId = projectId,
                    name = "Audio Track ${_audioTracks.value.size + 1}",
                    sourcePath = path,
                    duration = duration,
                    orderIndex = _audioTracks.value.size
                )
                projectRepository.insertAudioTrack(track)
                _audioTracks.value = projectRepository.getAudioTracks(projectId)
            }
        }
    }

    fun addSubtitle(text: String, startMs: Long, endMs: Long) {
        viewModelScope.launch {
            val subtitle = Subtitle(
                projectId = projectId,
                text = text,
                startTimeMs = startMs,
                endTimeMs = endMs
            )
            projectRepository.insertSubtitle(subtitle)
            _subtitles.value = projectRepository.getSubtitles(projectId)
        }
    }

    fun updateSubtitle(subtitle: Subtitle) {
        viewModelScope.launch {
            projectRepository.updateSubtitle(subtitle)
            _subtitles.value = projectRepository.getSubtitles(projectId)
        }
    }

    fun deleteSubtitle(subtitle: Subtitle) {
        viewModelScope.launch {
            projectRepository.deleteSubtitle(subtitle.id)
            _subtitles.value = projectRepository.getSubtitles(projectId)
        }
    }

    fun applyEffects(effectList: List<Effect>) {
        _effects.value = effectList
    }

    fun updateCurrentTime(timeMs: Long) {
        _currentTime.value = timeMs
    }

    fun undo() {
        if (undoStack.isEmpty()) return
        val action = undoStack.removeLast()
        redoStack.add(action)
        // Implementation would reverse the action
    }

    fun redo() {
        if (redoStack.isEmpty()) return
        val action = redoStack.removeLast()
        undoStack.add(action)
        // Implementation would reapply the action
    }

    private fun updateDuration() {
        _duration.value = _videoClips.value.sumOf { it.effectiveDuration }
    }

    private fun updateVideoUrl() {
        _videoUrl.value = _videoClips.value.firstOrNull()?.sourcePath
    }

    private fun pushUndo(action: UndoAction) {
        undoStack.add(action)
        redoStack.clear()
    }

    sealed class UndoAction {
        data class AddClip(val clip: VideoClip) : UndoAction()
        data class DeleteClip(val clip: VideoClip) : UndoAction()
        data class SplitClip(val clip1: VideoClip, val clip2: VideoClip) : UndoAction()
        data class TrimClip(val oldClip: VideoClip, val newClip: VideoClip) : UndoAction()
    }
}