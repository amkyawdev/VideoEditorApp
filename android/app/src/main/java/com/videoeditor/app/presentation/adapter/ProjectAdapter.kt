package com.videoeditor.app.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.videoeditor.app.core.utils.VideoUtils
import com.videoeditor.app.databinding.ItemProjectGridBinding
import com.videoeditor.app.databinding.ItemProjectHorizontalBinding
import com.videoeditor.app.domain.model.Project
import java.text.SimpleDateFormat
import java.util.*

class ProjectAdapter(
    private val onProjectClick: (Project) -> Unit,
    private val onProjectLongClick: (Project) -> Unit,
    private val isHorizontal: Boolean = false
) : ListAdapter<Project, RecyclerView.ViewHolder>(ProjectDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return if (isHorizontal) VIEW_TYPE_HORIZONTAL else VIEW_TYPE_GRID
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HORIZONTAL) {
            val binding = ItemProjectHorizontalBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            HorizontalViewHolder(binding)
        } else {
            val binding = ItemProjectGridBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            GridViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val project = getItem(position)
        when (holder) {
            is HorizontalViewHolder -> holder.bind(project)
            is GridViewHolder -> holder.bind(project)
        }
    }

    inner class HorizontalViewHolder(
        private val binding: ItemProjectHorizontalBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onProjectClick(getItem(position))
                }
            }
            binding.root.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onProjectLongClick(getItem(position))
                }
                true
            }
        }

        fun bind(project: Project) {
            binding.tvProjectName.text = project.name
            binding.tvDuration.text = VideoUtils.formatDuration(project.duration)
            binding.tvProjectDate.text = formatDate(project.updatedAt)

            project.thumbnailPath?.let { path ->
                Glide.with(binding.ivThumbnail)
                    .load(path)
                    .centerCrop()
                    .into(binding.ivThumbnail)
            }
        }
    }

    inner class GridViewHolder(
        private val binding: ItemProjectGridBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onProjectClick(getItem(position))
                }
            }
            binding.root.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onProjectLongClick(getItem(position))
                }
                true
            }
        }

        fun bind(project: Project) {
            binding.tvProjectName.text = project.name
            binding.tvDuration.text = VideoUtils.formatDuration(project.duration)
            binding.tvProjectDate.text = formatDate(project.updatedAt)

            project.thumbnailPath?.let { path ->
                Glide.with(binding.ivThumbnail)
                    .load(path)
                    .centerCrop()
                    .into(binding.ivThumbnail)
            }
        }
    }

    private fun formatDate(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60 * 1000 -> "Just now"
            diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} minutes ago"
            diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)} hours ago"
            diff < 7 * 24 * 60 * 60 * 1000 -> "${diff / (24 * 60 * 60 * 1000)} days ago"
            else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
        }
    }

    class ProjectDiffCallback : DiffUtil.ItemCallback<Project>() {
        override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val VIEW_TYPE_HORIZONTAL = 0
        private const val VIEW_TYPE_GRID = 1
    }
}