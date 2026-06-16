package com.videoeditor.app.presentation.ui.export

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.videoeditor.app.R
import com.videoeditor.app.databinding.ActivityExportBinding
import com.videoeditor.app.domain.model.ExportFormat
import com.videoeditor.app.domain.model.ExportQuality
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class ExportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExportBinding
    private val viewModel: ExportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val projectId = intent.getStringExtra(EXTRA_PROJECT_ID) ?: run {
            finish()
            return
        }

        viewModel.loadProject(projectId)
        
        setupToolbar()
        setupUI()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            if (viewModel.isExporting.value == true) {
                showCancelDialog()
            } else {
                finish()
            }
        }
    }

    private fun setupUI() {
        // Quality selection
        binding.chipGroupQuality.setOnCheckedStateChangeListener { _, checkedIds ->
            val quality = when (checkedIds.firstOrNull()) {
                R.id.chipQualityLow -> ExportQuality.LOW
                R.id.chipQualityMedium -> ExportQuality.MEDIUM
                R.id.chipQualityHigh -> ExportQuality.HIGH
                R.id.chipQualityUltra -> ExportQuality.ULTRA
                else -> ExportQuality.MEDIUM
            }
            viewModel.setQuality(quality)
        }

        // Format selection
        binding.chipGroupFormat.setOnCheckedStateChangeListener { _, checkedIds ->
            val format = when (checkedIds.firstOrNull()) {
                R.id.chipFormatMp4 -> ExportFormat.MP4
                R.id.chipFormatWebm -> ExportFormat.WEBM
                R.id.chipFormatGif -> ExportFormat.GIF
                else -> ExportFormat.MP4
            }
            viewModel.setFormat(format)
        }

        // FPS selection
        binding.chipGroupFps.setOnCheckedStateChangeListener { _, checkedIds ->
            val fps = when (checkedIds.firstOrNull()) {
                R.id.chipFps24 -> 24
                R.id.chipFps30 -> 30
                R.id.chipFps60 -> 60
                else -> 30
            }
            viewModel.setFps(fps)
        }

        // Export button
        binding.btnExport.setOnClickListener {
            viewModel.startExport()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.progress.collectLatest { progress ->
                if (progress >= 0) {
                    binding.progressOverlay.visibility = View.VISIBLE
                    binding.progressBar.progress = progress
                    binding.tvProgressPercent.text = "$progress%"
                } else {
                    binding.progressOverlay.visibility = View.GONE
                }
            }
        }

        lifecycleScope.launch {
            viewModel.status.collectLatest { status ->
                binding.tvProgressStatus.text = status
            }
        }

        lifecycleScope.launch {
            viewModel.exportComplete.collectLatest { path ->
                if (path != null) {
                    showExportCompleteDialog(path)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                if (error != null) {
                    Toast.makeText(this@ExportActivity, error, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showCancelDialog() {
        com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle("Cancel Export?")
            .setMessage("The export is still in progress. Are you sure you want to cancel?")
            .setPositiveButton("Cancel Export") { _, _ ->
                viewModel.cancelExport()
                finish()
            }
            .setNegativeButton("Continue", null)
            .show()
    }

    private fun showExportCompleteDialog(path: String) {
        com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle(R.string.export_complete)
            .setMessage("Your video has been exported successfully.")
            .setPositiveButton(R.string.export_share) { _, _ ->
                shareVideo(path)
            }
            .setNeutralButton(R.string.export_open) { _, _ ->
                openVideo(path)
            }
            .setNegativeButton(R.string.dialog_cancel) { _, _ ->
                finish()
            }
            .show()
    }

    private fun shareVideo(path: String) {
        val file = File(path)
        val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "video/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Share Video"))
    }

    private fun openVideo(path: String) {
        val file = File(path)
        val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
        
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "video/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }

    companion object {
        const val EXTRA_PROJECT_ID = "project_id"
    }
}