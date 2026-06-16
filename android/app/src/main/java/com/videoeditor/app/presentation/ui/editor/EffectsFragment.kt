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

    private var selectedEffect: Effect? = null

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
    }

    private fun setupUI() {
        binding.btnApplyEffects.setOnClickListener {
            applyEffects()
        }
    }

    private fun setupSliders() {
        binding.sliderBrightness.addOnChangeListener { _, value, _ ->
            binding.tvBrightnessValue.text = "${value.toInt()}%"
            updateEffect(EffectType.BRIGHTNESS, value / 100f)
        }

        binding.sliderContrast.addOnChangeListener { _, value, _ ->
            binding.tvContrastValue.text = "${value.toInt()}%"
            updateEffect(EffectType.CONTRAST, value / 100f)
        }

        binding.sliderSaturation.addOnChangeListener { _, value, _ ->
            binding.tvSaturationValue.text = "${value.toInt()}%"
            updateEffect(EffectType.SATURATION, value / 100f)
        }
    }

    private fun updateEffect(type: EffectType, intensity: Float) {
        val currentEffects = mutableListOf<Effect>()
        if (selectedEffect != null) {
            currentEffects.add(selectedEffect!!)
        }
        viewModel.applyEffects(currentEffects)
    }

    private fun applyEffects() {
        // Apply effects to video
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}