package com.videoeditor.app.domain.model

import java.util.UUID

data class Effect(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: EffectType,
    val intensity: Float = 0.5f,
    val parameters: Map<String, Float> = emptyMap()
)

enum class EffectType {
    NONE,
    BRIGHTNESS,
    CONTRAST,
    SATURATION,
    WARMTH,
    VIGNETTE,
    BLUR,
    SEPIA,
    GRAYSCALE,
    SHARPEN,
    DENOISE
}

object EffectPresets {
    val presets = listOf(
        Effect("none", "None", EffectType.NONE),
        Effect("brightness", "Brightness", EffectType.BRIGHTNESS, 0.5f),
        Effect("contrast", "Contrast", EffectType.CONTRAST, 0.5f),
        Effect("saturation", "Saturation", EffectType.SATURATION, 0.5f),
        Effect("warmth", "Warmth", EffectType.WARMTH, 0.5f),
        Effect("vignette", "Vignette", EffectType.VIGNETTE, 0.5f),
        Effect("blur", "Blur", EffectType.BLUR, 0.3f),
        Effect("sepia", "Sepia", EffectType.SEPIA, 0.7f),
        Effect("grayscale", "Grayscale", EffectType.GRAYSCALE, 1.0f)
    )
}