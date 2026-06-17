package com.videoeditor.app.presentation.ui.editor

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.videoeditor.app.databinding.FragmentAudioBinding
import com.videoeditor.app.domain.model.AudioTrack
import com.videoeditor.app.presentation.adapter.AudioTrackAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AudioEditorFragment : Fragment() {

    private var _binding: FragmentAudioBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditorViewModel by activityViewModels()
    private lateinit var audioAdapter: AudioTrackAdapter

    private val pickAudioLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.addAudioTrack(it, requireContext())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupAudioAdapter()
        observeAudioTracks()
    }

    private fun setupUI() {
        binding.btnAddAudio.setOnClickListener {
            pickAudioLauncher.launch("audio/*")
        }
    }

    private fun setupAudioAdapter() {
        audioAdapter = AudioTrackAdapter(
            onVolumeChange = { track, volume ->
                viewModel.updateAudioTrackVolume(track, volume)
            },
            onMuteToggle = { track ->
                viewModel.toggleAudioTrackMute(track)
            },
            onDelete = { track ->
                viewModel.deleteAudioTrack(track)
            }
        )

        binding.rvAudioTracks.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = audioAdapter
        }
    }

    private fun observeAudioTracks() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.audioTracks.collectLatest { tracks ->
                audioAdapter.submitList(tracks)
                updateEmptyState(tracks.isEmpty())
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyAudioState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvAudioTracks.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}