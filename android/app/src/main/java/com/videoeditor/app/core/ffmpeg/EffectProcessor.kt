package com.videoeditor.app.core.ffmpeg

import com.videoeditor.app.domain.model.Effect
import com.videoeditor.app.domain.model.EffectType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EffectProcessor @Inject constructor(
    private val ffmpegService: FFmpegService
) {
    suspend fun applyEffects(
        inputPath: String,
        effects: List<Effect>,
        outputPath: String,
        progressCallback: ((Float) -> Unit)? = null
    ): String {
        val filter = buildEffectFilter(effects)
        
        return if (filter.isNotEmpty()) {
            ffmpegService.applyFilter(inputPath, filter, outputPath)
        } else {
            inputPath
        }
    }

    fun buildEffectFilter(effects: List<Effect>): String {
        val filters = mutableListOf<String>()
        
        effects.filter { it.type != EffectType.NONE }.forEach { effect ->
            when (effect.type) {
                EffectType.BRIGHTNESS -> {
                    val value = (effect.intensity - 0.5f) * 2f
                    filters.add("eq=brightness=$value:brightness=$value")
                }
                EffectType.CONTRAST -> {
                    val value = 0.5f + effect.intensity
                    filters.add("eq=contrast=$value")
                }
                EffectType.SATURATION -> {
                    val value = effect.intensity * 2f
                    filters.add("eq=saturation=$value")
                }
                EffectType.WARMTH -> {
                    val temp = 6500 + (effect.intensity * 5500).toInt()
                    filters.add("eq=saturation=1.2:gamma=1.2")
                }
                EffectType.VIGNETTE -> {
                    val angle = effect.intensity * 90
                    filters.add("vignette=angle=${angle}d")
                }
                EffectType.BLUR -> {
                    val radius = (effect.intensity * 10 + 1).toInt()
                    filters.add("boxblur=${radius}:${radius}")
                }
                EffectType.SEPIA -> {
                    filters.add("colorchannelmixer=.393:.769:.189:0:.349:.686:.168:0:.272:.534:.131")
                }
                EffectType.GRAYSCALE -> {
                    filters.add("hue=s=0")
                }
                EffectType.SHARPEN -> {
                    val amount = effect.intensity * 3
                    filters.add("unsharp=5:5:$amount:5:5:$amount")
                }
                EffectType.DENOISE -> {
                    val strength = (effect.intensity * 20).toInt()
                    filters.add("hqdn3d=$strength")
                }
                else -> { /* Skip */ }
            }
        }
        
        return filters.joinToString(",")
    }

    fun getPresetFilters(): List<EffectPreset> {
        return listOf(
            EffectPreset("none", "None", ""),
            EffectPreset("brightness", "Brightness", "eq=brightness=0.1"),
            EffectPreset("contrast", "Contrast", "eq=contrast=1.3"),
            EffectPreset("saturation", "Vibrant", "eq=saturation=1.8"),
            EffectPreset("warm", "Warm", "eq=saturation=1.2:gamma=1.2"),
            EffectPreset("cool", "Cool", "eq=saturation=0.9"),
            EffectPreset("vignette", "Vignette", "vignette=angle=30d"),
            EffectPreset("vintage", "Vintage", "colorchannelmixer=.393:.769:.189:0:.349:.686:.168:0:.272:.534:.131,curves=vintage"),
            EffectPreset("dramatic", "Dramatic", "eq=contrast=1.5:saturation=1.3,vignette=angle=45d"),
            EffectPreset("cinematic", "Cinematic", "colorchannelmixer=.8:.8:.7:0:.1:.1:0:0:.1:.1:0,curves=medium_contrast")
        )
    }

    data class EffectPreset(
        val id: String,
        val name: String,
        val filter: String
    )
}