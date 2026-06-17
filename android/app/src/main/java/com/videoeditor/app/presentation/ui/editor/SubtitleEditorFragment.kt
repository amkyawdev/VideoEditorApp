package com.videoeditor.app.presentation.ui.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.videoeditor.app.R
import com.videoeditor.app.databinding.FragmentSubtitlesBinding
import com.videoeditor.app.domain.model.Subtitle
import com.videoeditor.app.domain.model.SubtitlePosition
import com.videoeditor.app.presentation.adapter.SubtitleAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SubtitleEditorFragment : Fragment() {

    private var _binding: FragmentSubtitlesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditorViewModel by activityViewModels()
    private lateinit var subtitleAdapter: SubtitleAdapter

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
        setupSubtitleAdapter()
        observeSubtitles()
    }

    private fun setupUI() {
        binding.btnAddSubtitle.setOnClickListener {
            showAddSubtitleDialog()
        }
    }

    private fun setupSubtitleAdapter() {
        subtitleAdapter = SubtitleAdapter(
            onEdit = { subtitle -> showEditSubtitleDialog(subtitle) },
            onDelete = { subtitle -> viewModel.deleteSubtitle(subtitle) },
            onVisibilityToggle = { subtitle -> viewModel.toggleSubtitleVisibility(subtitle) }
        )

        binding.rvSubtitles.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = subtitleAdapter
        }
    }

    private fun observeSubtitles() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.subtitles.collectLatest { subtitles ->
                subtitleAdapter.submitList(subtitles)
                updateEmptyState(subtitles.isEmpty())
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptySubtitleState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvSubtitles.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun showAddSubtitleDialog() {
        val dialogView = createSubtitleDialogView(null)
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Subtitle")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val textInput = dialogView.findViewById<EditText>(R.id.etSubtitleText)
                val startSlider = dialogView.findViewById<Slider>(R.id.sliderStartTime)
                val endSlider = dialogView.findViewById<Slider>(R.id.sliderEndTime)
                val spinner = dialogView.findViewById<Spinner>(R.id.spinnerPosition)
                
                val text = textInput?.text?.toString() ?: ""
                val startTime = (startSlider?.value ?: 0f).toLong() * 1000
                val endTime = (endSlider?.value ?: 5f).toLong() * 1000
                val position = if (spinner != null) SubtitlePosition.values()[spinner.selectedItemPosition] else SubtitlePosition.BOTTOM
                
                if (text.isNotBlank()) {
                    viewModel.addSubtitle(text, startTime, endTime, position)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditSubtitleDialog(subtitle: Subtitle) {
        val dialogView = createSubtitleDialogView(subtitle)
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Subtitle")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val textInput = dialogView.findViewById<EditText>(R.id.etSubtitleText)
                val startSlider = dialogView.findViewById<Slider>(R.id.sliderStartTime)
                val endSlider = dialogView.findViewById<Slider>(R.id.sliderEndTime)
                val spinner = dialogView.findViewById<Spinner>(R.id.spinnerPosition)
                
                val text = textInput?.text?.toString() ?: ""
                val startTime = (startSlider?.value ?: 0f).toLong() * 1000
                val endTime = (endSlider?.value ?: 5f).toLong() * 1000
                val position = if (spinner != null) SubtitlePosition.values()[spinner.selectedItemPosition] else SubtitlePosition.BOTTOM
                
                if (text.isNotBlank()) {
                    viewModel.updateSubtitleDetails(subtitle, text, startTime, endTime, position)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun createSubtitleDialogView(subtitle: Subtitle?): View {
        val context = requireContext()
        val padding = resources.getDimensionPixelSize(R.dimen.spacing_md)
        
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(padding, padding, padding, padding)
        }
        
        // Subtitle Text Input
        val textInput = EditText(context).apply {
            id = R.id.etSubtitleText
            hint = "Enter subtitle text"
            setText(subtitle?.text ?: "")
        }
        layout.addView(textInput)
        
        // Start Time Slider
        val startLabel = android.widget.TextView(context).apply {
            text = "Start Time: ${(subtitle?.startTimeMs ?: 0) / 1000}s"
            setTextColor(context.getColor(R.color.text_primary))
        }
        layout.addView(startLabel)
        
        val startSlider = com.google.android.material.slider.Slider(context).apply {
            id = R.id.sliderStartTime
            valueFrom = 0f
            valueTo = 60f
            value = (subtitle?.startTimeMs ?: 0) / 1000f
            addOnChangeListener { _, value, _ ->
                startLabel.text = "Start Time: ${value.toInt()}s"
            }
        }
        layout.addView(startSlider)
        
        // End Time Slider
        val endLabel = android.widget.TextView(context).apply {
            text = "End Time: ${(subtitle?.endTimeMs ?: 5000) / 1000}s"
            setTextColor(context.getColor(R.color.text_primary))
        }
        layout.addView(endLabel)
        
        val endSlider = com.google.android.material.slider.Slider(context).apply {
            id = R.id.sliderEndTime
            valueFrom = 0f
            valueTo = 60f
            value = (subtitle?.endTimeMs ?: 5000) / 1000f
            addOnChangeListener { _, value, _ ->
                endLabel.text = "End Time: ${value.toInt()}s"
            }
        }
        layout.addView(endSlider)
        
        // Position Spinner
        val positionLabel = android.widget.TextView(context).apply {
            text = "Position"
            setTextColor(context.getColor(R.color.text_primary))
        }
        layout.addView(positionLabel)
        
        val positions = SubtitlePosition.values().map { it.name }
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, positions)
        val spinner = Spinner(context).apply {
            id = R.id.spinnerPosition
            setAdapter(adapter)
            setSelection(subtitle?.position?.ordinal ?: 2)
        }
        layout.addView(spinner)
        
        return layout
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}