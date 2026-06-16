package com.videoeditor.app.presentation.ui.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.videoeditor.app.databinding.FragmentSubtitlesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SubtitleEditorFragment : Fragment() {

    private var _binding: FragmentSubtitlesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditorViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubtitlesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeSubtitles()
    }

    private fun setupUI() {
        binding.btnAddSubtitle.setOnClickListener {
            // Show add subtitle dialog
            showAddSubtitleDialog()
        }
    }

    private fun observeSubtitles() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.subtitles.collectLatest { subtitles ->
                // Update RecyclerView with subtitles
            }
        }
    }

    private fun showAddSubtitleDialog() {
        // Dialog implementation
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}