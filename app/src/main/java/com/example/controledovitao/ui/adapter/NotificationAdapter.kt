package com.example.controledovitao.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.controledovitao.data.model.Status
import com.example.controledovitao.databinding.ItemNotificationBinding

class NotificationAdapter(
    private val items: List<Triple<Int, String, String>>
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    private val expandedPositions = mutableSetOf<Int>()

    inner class NotificationViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Triple<Int, String, String>, position: Int) {
            val (statusOp, title, description) = item

            binding.tvTitle.text = title
            binding.tvDescription.text = description

            val color = when (statusOp) {
                Status.URGENT.op -> Color.parseColor("#EA4335")   // Vermelho (2)
                Status.STANDARD.op -> Color.parseColor("#FBBC05") // Laranja (1)
                Status.CONCLUDE.op -> Color.parseColor("#34A853") // Verde (-1)
                else -> Color.parseColor("#4285F4")               // Azul (0 ou outros)
            }
            binding.imgIcon.setColorFilter(color)

            val isExpanded = expandedPositions.contains(position)

            if (isExpanded) {
                binding.tvDescription.visibility = View.VISIBLE
                binding.imgArrow.rotation = 180f
            } else {
                binding.tvDescription.visibility = View.GONE
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        // Passamos a posição atual para controlar o estado de expansão
        holder.bind(items[position], position)
    }

    override fun getItemCount() = items.size
}