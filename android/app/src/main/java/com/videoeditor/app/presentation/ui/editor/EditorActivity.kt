package com.videoeditor.app.presentation.ui.editor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.videoeditor.app.R
import com.videoeditor.app.databinding.ActivityEditorBinding
import com.videoeditor.app.presentation.ui.export.ExportActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditorBinding
    private val viewModel: EditorViewModel by viewModels()
    
    private var player: ExoPlayer? = null
    private lateinit var viewPagerAdapter: EditorPagerAdapter

    private val pickVideoLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.addVideoClip(it, this) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val projectId = intent.getStringExtra(EXTRA_PROJECT_ID) ?: run {
            finish()
            return
        }

        viewModel.loadProject(projectId)
        
        setupToolbar()
        setupViewPager()
        setupPlayer()
        setupBottomToolbar()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnUndo.setOnClickListener { viewModel.undo() }
        binding.btnRedo.setOnClickListener { viewModel.redo() }
    }

    private fun setupViewPager() {
        viewPagerAdapter = EditorPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.icon = getDrawable(R.drawable.ic_video)
                    tab.text = getString(R.string.tab_timeline)
                }
                1 -> {
                    tab.icon = getDrawable(R.drawable.ic_subtitles)
                    tab.text = getString(R.string.tab_subtitles)
                }
                2 -> {
                    tab.icon = getDrawable(R.drawable.ic_audio)
                    tab.text = getString(R.string.tab_audio)
                }
                3 -> {
                    tab.icon = getDrawable(R.drawable.ic_effects)
                    tab.text = getString(R.string.tab_effects)
                }
            }
        }.attach()
    }

    private fun setupPlayer() {
        player = ExoPlayer.Builder(this).build().also {
            binding.playerView.player = it
            it.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    updatePlayPauseButton()
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    updatePlayPauseButton()
                }
            })
        }

        binding.btnPlayPause.setOnClickListener {
            player?.let {
                if (it.isPlaying) it.pause() else it.play()
            }
        }
    }

    private fun setupBottomToolbar() {
        binding.btnAddClip.setOnClickListener {
            pickVideoLauncher.launch("video/*")
        }

        binding.btnTrim.setOnClickListener {
            // Show trim dialog
            viewModel.selectedClip.value?.let { clip ->
                showTrimDialog(clip)
            }
        }

        binding.btnSplit.setOnClickListener {
            viewModel.splitAtPlayhead()
        }

        binding.btnDelete.setOnClickListener {
            viewModel.deleteSelectedClip()
        }

        binding.btnExport.setOnClickListener {
            val intent = Intent(this, ExportActivity::class.java).apply {
                putExtra(ExportActivity.EXTRA_PROJECT_ID, viewModel.projectId)
            }
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.project.collectLatest { project ->
                project?.let {
                    binding.toolbar.title = it.name
                }
            }
        }

        lifecycleScope.launch {
            viewModel.currentTime.collectLatest { time ->
                binding.tvCurrentTime.text = formatTime(time)
            }
        }

        lifecycleScope.launch {
            viewModel.duration.collectLatest { duration ->
                binding.tvTotalTime.text = formatTime(duration)
            }
        }

        lifecycleScope.launch {
            viewModel.videoUrl.collectLatest { url ->
                url?.let { loadVideo(it) }
            }
        }
    }

    private fun loadVideo(path: String) {
        player?.let {
            it.setMediaItem(MediaItem.fromUri(Uri.parse(path)))
            it.prepare()
        }
    }

    private fun updatePlayPauseButton() {
        val isPlaying = player?.isPlaying == true
        binding.btnPlayPause.setImageResource(
            if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        )
    }

    private fun showTrimDialog(clip: com.videoeditor.app.domain.model.VideoClip) {
        // Trim dialog would be shown here
    }

    private fun formatTime(ms: Long): String {
        val minutes = ms / 60000
        val seconds = (ms % 60000) / 1000
        return String.format("%02d:%02d:%02d", ms / 3600000, minutes, seconds)
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }

    companion object {
        const val EXTRA_PROJECT_ID = "project_id"
    }
}