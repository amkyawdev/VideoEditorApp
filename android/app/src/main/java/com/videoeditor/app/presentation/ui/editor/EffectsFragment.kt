package com.videoeditor.app.presentation.ui.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.videoeditor.app.databinding.FragmentEffectsBinding
import com.videoeditor.app.domain.model.Effect
import com.videoeditor.app.domain.model.EffectPresets
import com.videoeditor.app.domain.model.EffectType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EffectsFragment : Fragment() {

    private var _binding: FragmentEffectsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditorViewModel by activityViewModels()

    private var selectedColorEffect: Effect? = null
    private var currentRotation = 0
    private var flipHorizontal = false
    private var flipVertical = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEffectsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupSliders()
        setupRotationControls()
        setupFlipControls()
        setupEffectPresets()
    }

    private fun setupUI() {
        binding.btnApplyEffects.setOnClickListener {
            applyEffects()
        }
    }

    private fun setupSliders() {
        binding.sliderBrightness.addOnChangeListener { _, value, _ ->
            binding.tvBrightnessValue.text = "${value.toInt()}%"
        }

        binding.sliderContrast.addOnChangeListener { _, value, _ ->
            binding.tvContrastValue.text = "${value.toInt()}%"
        }

        binding.sliderSaturation.addOnChangeListener { _, value, _ ->
            binding.tvSaturationValue.text = "${value.toInt()}%"
        }

        binding.sliderWarmth.addOnChangeListener { _, value, _ ->
            binding.tvWarmthValue.text = "${value.toInt()}%"
        }

        binding.sliderSpeed.addOnChangeListener { _, value, _ ->
            binding.tvSpeedValue.text = "${String.format("%.2f", value)}x"
        }
    }

    private fun setupRotationControls() {
        binding.toggleRotation.check(binding.btnRotate0.id)
        
        binding.toggleRotation.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                currentRotation = when (checkedId) {
                    binding.btnRotate0.id -> 0
                    binding.btnRotate90.id -> 90
                    binding.btnRotate180.id -> 180
                    binding.btnRotate270.id -> 270
                    else -> 0
                }
            }
        }
    }

    private fun setupFlipControls() {
        binding.btnFlipHorizontal.setOnClickListener {
            flipHorizontal = !flipHorizontal
            updateFlipButtonState(binding.btnFlipHorizontal, flipHorizontal)
        }

        binding.btnFlipVertical.setOnClickListener {
            flipVertical = !flipVertical
            updateFlipButtonState(binding.btnFlipVertical, flipVertical)
        }
    }

    private fun updateFlipButtonState(button: com.google.android.material.button.MaterialButton, isSelected: Boolean) {
        if (isSelected) {
            button.setBackgroundColor(requireContext().getColor(com.videoeditor.app.R.color.primary))
            button.setTextColor(requireContext().getColor(com.videoeditor.app.R.color.on_primary))
        } else {
            button.setBackgroundColor(requireContext().getColor(android.R.color.transparent))
            button.setTextColor(requireContext().getColor(com.videoeditor.app.R.color.text_primary))
        }
    }

    private fun setupEffectPresets() {
        // Setup effect presets RecyclerView
        // Color effects: Sepia, Grayscale, Vignette, Blur, Sharpen
    }

    fun getAdjustmentValues(): Map<EffectType, Float> {
        return mapOf(
            EffectType.BRIGHTNESS to (binding.sliderBrightness.value / 100f),
            EffectType.CONTRAST to (binding.sliderContrast.value / 100f),
            EffectType.SATURATION to (binding.sliderSaturation.value / 100f),
            EffectType.WARMTH to (binding.sliderWarmth.value / 100f)
        )
    }

    fun getTransformValues(): Triple<Float, Int, Pair<Boolean, Boolean>> {
        return Triple(
            binding.sliderSpeed.value,
            currentRotation,
            Pair(flipHorizontal, flipVertical)
        )
    }

    private fun applyEffects() {
        val adjustments = getAdjustmentValues()
        val (speed, rotation, flip) = getTransformValues()
        
        // Apply effects to selected clip
        viewModel.selectedClip.value?.let { clip ->
            viewModel.updateClipTransform(clip, speed, rotation, flip.first, flip.second)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}