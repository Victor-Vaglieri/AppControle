package com.example.controledovitao.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.controledovitao.databinding.ItemSpinnerOptionBinding
import com.example.controledovitao.data.model.SimulationOption

class SimulatorOptionAdapter(
    private var items: List<SimulationOption>,
    private val onClick: (SimulationOption) -> Unit
) : RecyclerView.Adapter<SimulatorOptionAdapter.OptionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val binding = ItemSpinnerOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<SimulationOption>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class OptionViewHolder(private val binding: ItemSpinnerOptionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SimulationOption) {
            binding.tvOptionName.text = item.name
            binding.root.setOnClickListener { onClick(item) }
        }
    }
}