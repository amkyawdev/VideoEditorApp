package com.videoeditor.app.presentation.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.videoeditor.app.core.utils.PermissionUtils
import com.videoeditor.app.databinding.ActivityMainBinding
import com.videoeditor.app.domain.model.Project
import com.videoeditor.app.presentation.adapter.ProjectAdapter
import com.videoeditor.app.presentation.ui.editor.EditorActivity
import com.videoeditor.app.presentation.ui.settings.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    
    private lateinit var recentAdapter: ProjectAdapter
    private lateinit var allProjectsAdapter: ProjectAdapter

    private val pickVideoLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { handleVideoPicked(it) }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            showProjects()
        } else {
            Toast.makeText(this, "Storage permission required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerViews()
        setupFab()
        observeProjects()

        checkPermissions()
    }

    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                com.videoeditor.app.R.id.action_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerViews() {
        recentAdapter = ProjectAdapter(
            onProjectClick = { openProject(it) },
            onProjectLongClick = { showProjectOptions(it) },
            isHorizontal = true
        )
        
        allProjectsAdapter = ProjectAdapter(
            onProjectClick = { openProject(it) },
            onProjectLongClick = { showProjectOptions(it) },
            isHorizontal = false
        )

        binding.rvRecentProjects.apply {
            adapter = recentAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvAllProjects.apply {
            adapter = allProjectsAdapter
            layoutManager = GridLayoutManager(context, 2)
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshProjects()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupFab() {
        binding.fabNewProject.setOnClickListener {
            pickVideoLauncher.launch("video/*")
        }
    }

    private fun observeProjects() {
        lifecycleScope.launch {
            viewModel.projects.collectLatest { projects ->
                updateUI(projects)
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.loadingContainer.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun updateUI(projects: List<Project>) {
        if (projects.isEmpty()) {
            showEmptyState()
        } else {
            showProjects()
            
            val recentProjects = projects.take(5)
            if (recentProjects.isNotEmpty()) {
                binding.tvRecentLabel.visibility = View.VISIBLE
                binding.rvRecentProjects.visibility = View.VISIBLE
                recentAdapter.submitList(recentProjects)
            }
            
            binding.tvAllProjectsLabel.visibility = View.VISIBLE
            binding.rvAllProjects.visibility = View.VISIBLE
            allProjectsAdapter.submitList(projects)
        }
    }

    private fun showEmptyState() {
        binding.emptyState.visibility = View.VISIBLE
        binding.tvRecentLabel.visibility = View.GONE
        binding.rvRecentProjects.visibility = View.GONE
        binding.tvAllProjectsLabel.visibility = View.GONE
        binding.rvAllProjects.visibility = View.GONE
    }

    private fun showProjects() {
        binding.emptyState.visibility = View.GONE
    }

    private fun checkPermissions() {
        if (!PermissionUtils.hasStoragePermission(this)) {
            permissionLauncher.launch(PermissionUtils.getAllRequiredPermissions())
        }
    }

    private fun handleVideoPicked(uri: Uri) {
        lifecycleScope.launch {
            viewModel.createProjectFromVideo(uri, this@MainActivity)
        }
    }

    private fun openProject(project: Project) {
        val intent = Intent(this, EditorActivity::class.java).apply {
            putExtra(EditorActivity.EXTRA_PROJECT_ID, project.id)
        }
        startActivity(intent)
    }

    private fun showProjectOptions(project: Project) {
        // Show delete dialog or options menu
        com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle("Delete Project?")
            .setMessage("This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteProject(project.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}