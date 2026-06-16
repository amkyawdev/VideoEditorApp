package com.videoeditor.app.presentation.ui.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.videoeditor.app.databinding.FragmentTimelineBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TimelineFragment : Fragment() {

    private var _binding: FragmentTimelineBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditorViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimelineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupZoomControls()
        setupTimeline()
    }

    private fun setupZoomControls() {
        binding.btnZoomIn.setOnClickListener {
            binding.timelineView.zoomIn()
            updateZoomLevel()
        }

        binding.btnZoomOut.setOnClickListener {
            binding.timelineView.zoomOut()
            updateZoomLevel()
        }
    }

    private fun setupTimeline() {
        binding.timelineView.setOnClipSelectedListener { clip ->
            viewModel.selectClip(clip)
        }
    }

    private fun updateZoomLevel() {
        binding.tvZoomLevel.text = "${(binding.timelineView.zoomLevelValue * 100).toInt()}%"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}