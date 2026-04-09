package com.example.ricescan.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ricescan.databinding.ItemHistoryDiseaseBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(
    private val onItemClick: (DetectionHistoryItem) -> Unit
) : ListAdapter<DetectionHistoryItem, HistoryAdapter.HistoryViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryDiseaseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HistoryViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HistoryViewHolder(
        private val binding: ItemHistoryDiseaseBinding,
        private val onItemClick: (DetectionHistoryItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DetectionHistoryItem) {
            binding.tvDiseaseName.text = item.displayName
            binding.tvTimestamp.text = formatDate(item.timestamp)
            val uri = item.imageUri
            if (uri.isNullOrBlank()) {
                binding.ivThumbnail.setImageDrawable(null)
            } else {
                Glide.with(binding.ivThumbnail)
                    .load(uri)
                    .centerCrop()
                    .into(binding.ivThumbnail)
            }

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }

        private fun formatDate(timestamp: Long): String {
            val formatter = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
            return formatter.format(Date(timestamp))
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<DetectionHistoryItem>() {
            override fun areItemsTheSame(oldItem: DetectionHistoryItem, newItem: DetectionHistoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DetectionHistoryItem, newItem: DetectionHistoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
