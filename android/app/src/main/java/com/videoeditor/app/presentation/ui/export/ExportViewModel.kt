package com.videoeditor.app.presentation.ui.export

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.videoeditor.app.core.ffmpeg.RenderEngine
import com.videoeditor.app.core.utils.FileUtils
import com.videoeditor.app.domain.model.*
import com.videoeditor.app.domain.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val renderEngine: RenderEngine,
    private val fileUtils: FileUtils,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val projectId: String = savedStateHandle.get<String>("project_id") ?: ""

    private val _exportSettings = MutableStateFlow(ExportSettings())
    val exportSettings: StateFlow<ExportSettings> = _exportSettings.asStateFlow()

    private val _progress = MutableStateFlow(-1f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _status = MutableStateFlow("")
    val status: StateFlow<String> = _status.asStateFlow()

    private val _exportComplete = MutableStateFlow<String?>(null)
    val exportComplete: StateFlow<String?> = _exportComplete.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    private var exportJob: Job? = null

    fun loadProject(id: String) {
        viewModelScope.launch {
            _exportSettings.value = ExportSettings()
        }
    }

    fun setQuality(quality: ExportQuality) {
        _exportSettings.value = _exportSettings.value.copy(quality = quality)
    }

    fun setFormat(format: ExportFormat) {
        _exportSettings.value = _exportSettings.value.copy(format = format)
    }

    fun setFps(fps: Int) {
        _exportSettings.value = _exportSettings.value.copy(fps = fps)
    }

    fun startExport() {
        if (_isExporting.value) return

        exportJob = viewModelScope.launch {
            _isExporting.value = true
            _progress.value = 0
            _status.value = "Preparing export..."

            try {
                // Get project data
                val clips = projectRepository.getVideoClips(projectId)
                val audioTracks = projectRepository.getAudioTracks(projectId)
                val subtitles = projectRepository.getSubtitles(projectId)

                if (clips.isEmpty()) {
                    _error.value = "No video clips to export"
                    return@launch
                }

                // Generate output path
                val outputPath = generateOutputPath()
                
                _status.value = "Rendering video..."

                // Render
                val result = renderEngine.render(
                    clips = clips,
                    audioTracks = audioTracks,
                    subtitles = subtitles,
                    effects = emptyList(), // Effects would be stored in project
                    settings = _exportSettings.value,
                    outputPath = outputPath,
                    progressCallback = { p ->
                        _progress.value = (p * 100).toInt()
                    }
                )

                _progress.value = 100
                _status.value = "Export complete!"
                _exportComplete.value = result

            } catch (e: Exception) {
                _error.value = e.message ?: "Export failed"
                _progress.value = -1
            } finally {
                _isExporting.value = false
            }
        }
    }

    fun cancelExport() {
        exportJob?.cancel()
        _isExporting.value = false
        _progress.value = -1
        _status.value = ""
    }

    private fun generateOutputPath(): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val extension = _exportSettings.value.format.extension
        val exportDir = fileUtils.getExportDirectory(VideoEditApp.instance)
        return File(exportDir, "video_$timestamp.$extension").absolutePath
    }
}

private class VideoEditApp {
    companion object {
        lateinit var instance: android.app.Application
            private set
    }
}