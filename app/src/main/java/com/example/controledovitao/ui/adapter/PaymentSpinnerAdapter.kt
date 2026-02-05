package com.example.controledovitao.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.controledovitao.databinding.ItemSpinnerOptionBinding
import com.example.controledovitao.data.model.Payment

class PaymentSpinnerAdapter(
    private var items: List<Payment>,
    private val onClick: (Payment) -> Unit
) : RecyclerView.Adapter<PaymentSpinnerAdapter.PaymentOptionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentOptionViewHolder {
        val binding = ItemSpinnerOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentOptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentOptionViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<Payment>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class PaymentOptionViewHolder(private val binding: ItemSpinnerOptionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Payment) {
            binding.tvOptionName.text = item.name
            binding.root.setOnClickListener { onClick(item) }
        }
    }
}