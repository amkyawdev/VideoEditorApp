package com.videoeditor.app.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.videoeditor.app.databinding.ItemSubtitleBinding
import com.videoeditor.app.domain.model.Subtitle

class SubtitleAdapter(
    private val onEdit: (Subtitle) -> Unit,
    private val onDelete: (Subtitle) -> Unit,
    private val onVisibilityToggle: (Subtitle) -> Unit
) : ListAdapter<Subtitle, SubtitleAdapter.SubtitleViewHolder>(SubtitleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtitleViewHolder {
        val binding = ItemSubtitleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SubtitleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubtitleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SubtitleViewHolder(
        private val binding: ItemSubtitleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(subtitle: Subtitle) {
            binding.tvSubtitleText.text = subtitle.text
            binding.tvSubtitleTime.text = formatTimeRange(subtitle.startTimeMs, subtitle.endTimeMs)
            binding.tvPosition.text = subtitle.position.name

            // Update visibility icon
            binding.btnVisibility.setImageResource(
                if (subtitle.isVisible) com.videoeditor.app.R.drawable.ic_visibility
                else com.videoeditor.app.R.drawable.ic_visibility_off
            )

            binding.root.setOnClickListener {
                onEdit(subtitle)
            }

            binding.btnVisibility.setOnClickListener {
                onVisibilityToggle(subtitle)
            }

            binding.btnDeleteSubtitle.setOnClickListener {
                onDelete(subtitle)
            }
        }

        private fun formatTimeRange(startMs: Long, endMs: Long): String {
            val startSec = startMs / 1000
            val endSec = endMs / 1000
            return "${formatTime(startSec)} - ${formatTime(endSec)}"
        }

        private fun formatTime(seconds: Long): String {
            val min = seconds / 60
            val sec = seconds % 60
            return String.format("%02d:%02d", min, sec)
        }
    }

    class SubtitleDiffCallback : DiffUtil.ItemCallback<Subtitle>() {
        override fun areItemsTheSame(oldItem: Subtitle, newItem: Subtitle): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Subtitle, newItem: Subtitle): Boolean {
            return oldItem == newItem
        }
    }
}