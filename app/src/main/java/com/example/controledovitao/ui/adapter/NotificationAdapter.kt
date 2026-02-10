package com.example.controledovitao.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.controledovitao.data.model.Notification // Importe o seu Model
import com.example.controledovitao.data.model.Status       // Importe o seu Enum Status
import com.example.controledovitao.databinding.ItemNotificationBinding

class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    private var items: List<Notification> = emptyList()
    private val expandedPositions = mutableSetOf<Int>()
    fun updateList(newItems: List<Notification>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount() = items.size

    inner class NotificationViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Notification, position: Int) {
            binding.tvTitle.text = item.title
            binding.tvDescription.text = item.description


            val color = when (item.statusOp) {
                Status.URGENT.op -> Color.parseColor("#EA4335")
                Status.STANDARD.op -> Color.parseColor("#FBBC05")
                Status.CONCLUDE.op -> Color.parseColor("#34A853")
                else -> Color.parseColor("#4285F4")
            }
            binding.imgIcon.setColorFilter(color)

            val isExpanded = expandedPositions.contains(position)

            if (isExpanded) {
                binding.tvDescription.visibility = View.VISIBLE
                binding.imgArrow.rotation = 180f
            } else {
                binding.tvDescription.visibility = View.GONE
                binding.imgArrow.rotation = 0f
            }

            binding.root.setOnClickListener {
                if (isExpanded) {
                    expandedPositions.remove(position)
                } else {
                    expandedPositions.add(position)
                }
                notifyItemChanged(position)
            }
        }
    }
}