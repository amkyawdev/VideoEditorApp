package com.videoeditor.app.presentation.ui.editor

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class EditorPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TimelineFragment()
            1 -> SubtitleEditorFragment()
            2 -> AudioEditorFragment()
            3 -> EffectsFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}