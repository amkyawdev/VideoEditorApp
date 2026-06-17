package com.videoeditor.app.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.videoeditor.app.databinding.ItemAudioTrackBinding
import com.videoeditor.app.domain.model.AudioTrack

class AudioTrackAdapter(
    private val onVolumeChange: (AudioTrack, Float) -> Unit,
    private val onMuteToggle: (AudioTrack) -> Unit,
    private val onDelete: (AudioTrack) -> Unit
) : ListAdapter<AudioTrack, AudioTrackAdapter.AudioTrackViewHolder>(AudioTrackDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioTrackViewHolder {
        val binding = ItemAudioTrackBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AudioTrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AudioTrackViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AudioTrackViewHolder(
        private val binding: ItemAudioTrackBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(track: AudioTrack) {
            binding.tvAudioName.text = track.name
            binding.tvAudioDuration.text = formatDuration(track.startTime, track.duration)
            binding.sliderVolume.value = track.volume * 100
            binding.tvVolumeValue.text = "${(track.volume * 100).toInt()}%"

            // Update mute icon
            binding.btnMuteAudio.setImageResource(
                if (track.isMuted) com.videoeditor.app.R.drawable.ic_volume_off
                else com.videoeditor.app.R.drawable.ic_volume
            )

            binding.sliderVolume.addOnChangeListener { _, value, fromUser ->
                if (fromUser) {
                    binding.tvVolumeValue.text = "${value.toInt()}%"
                    onVolumeChange(track, value / 100f)
                }
            }

            binding.btnMuteAudio.setOnClickListener {
                onMuteToggle(track)
            }

            binding.btnDeleteAudio.setOnClickListener {
                onDelete(track)
            }
        }

        private fun formatDuration(startMs: Long, durationMs: Long): String {
            val startSec = startMs / 1000
            val endSec = (startMs + durationMs) / 1000
            val startMin = startSec / 60
            val startSecPart = startSec % 60
            val endMin = endSec / 60
            val endSecPart = endSec % 60
            return String.format("%02d:%02d - %02d:%02d", startMin, startSecPart, endMin, endSecPart)
        }
    }

    class AudioTrackDiffCallback : DiffUtil.ItemCallback<AudioTrack>() {
        override fun areItemsTheSame(oldItem: AudioTrack, newItem: AudioTrack): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AudioTrack, newItem: AudioTrack): Boolean {
            return oldItem == newItem
        }
    }
}