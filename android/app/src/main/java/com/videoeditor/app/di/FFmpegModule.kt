package com.videoeditor.app.di

import com.videoeditor.app.core.ffmpeg.AudioProcessor
import com.videoeditor.app.core.ffmpeg.EffectProcessor
import com.videoeditor.app.core.ffmpeg.FFmpegService
import com.videoeditor.app.core.ffmpeg.RenderEngine
import com.videoeditor.app.core.ffmpeg.SubtitleProcessor
import com.videoeditor.app.core.ffmpeg.VideoProcessor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FFmpegModule {

    @Provides
    @Singleton
    fun provideFFmpegService(): FFmpegService {
        return FFmpegService()
    }

    @Provides
    @Singleton
    fun provideVideoProcessor(ffmpegService: FFmpegService): VideoProcessor {
        return VideoProcessor(ffmpegService)
    }

    @Provides
    @Singleton
    fun provideAudioProcessor(ffmpegService: FFmpegService): AudioProcessor {
        return AudioProcessor(ffmpegService)
    }

    @Provides
    @Singleton
    fun provideSubtitleProcessor(ffmpegService: FFmpegService): SubtitleProcessor {
        return SubtitleProcessor(ffmpegService)
    }

    @Provides
    @Singleton
    fun provideEffectProcessor(ffmpegService: FFmpegService): EffectProcessor {
        return EffectProcessor(ffmpegService)
    }

    @Provides
    @Singleton
    fun provideRenderEngine(
        ffmpegService: FFmpegService,
        videoProcessor: VideoProcessor,
        audioProcessor: AudioProcessor,
        subtitleProcessor: SubtitleProcessor,
        effectProcessor: EffectProcessor
    ): RenderEngine {
        return RenderEngine(
            ffmpegService,
            videoProcessor,
            audioProcessor,
            subtitleProcessor,
            effectProcessor
        )
    }
}