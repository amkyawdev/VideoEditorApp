package com.videoeditor.app.domain.usecase

import com.videoeditor.app.core.ffmpeg.EffectProcessor
import com.videoeditor.app.domain.model.Effect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ApplyEffectUseCase @Inject constructor(
    private val effectProcessor: EffectProcessor
) {
    suspend operator fun invoke(
        videoPath: String,
        effects: List<Effect>,
        outputPath: String,
        onProgress: ((Float) -> Unit)? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val result = effectProcessor.applyEffects(
                inputPath = videoPath,
                effects = effects,
                outputPath = outputPath,
                progressCallback = onProgress
            )
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}