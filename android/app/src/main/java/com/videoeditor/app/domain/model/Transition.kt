package com.videoeditor.app.domain.model

import java.util.UUID

data class Transition(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: TransitionType,
    val durationMs: Long = 500L
)

enum class TransitionType {
    NONE,
    FADE,
    CROSSFADE,
    SLIDE_LEFT,
    SLIDE_RIGHT,
    SLIDE_UP,
    SLIDE_DOWN,
    ZOOM_IN,
    ZOOM_OUT,
    DISSOLVE,
    WIPE
}

object TransitionPresets {
    val presets = listOf(
        Transition("none", "None", TransitionType.NONE, 0L),
        Transition("fade", "Fade", TransitionType.FADE, 500L),
        Transition("crossfade", "Crossfade", TransitionType.CROSSFADE, 500L),
        Transition("slide_left", "Slide Left", TransitionType.SLIDE_LEFT, 500L),
        Transition("slide_right", "Slide Right", TransitionType.SLIDE_RIGHT, 500L),
        Transition("slide_up", "Slide Up", TransitionType.SLIDE_UP, 500L),
        Transition("slide_down", "Slide Down", TransitionType.SLIDE_DOWN, 500L),
        Transition("zoom_in", "Zoom In", TransitionType.ZOOM_IN, 500L),
        Transition("zoom_out", "Zoom Out", TransitionType.ZOOM_OUT, 500L)
    )
}