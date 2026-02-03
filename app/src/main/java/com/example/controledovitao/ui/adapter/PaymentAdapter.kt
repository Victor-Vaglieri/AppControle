package com.example.controledovitao.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.controledovitao.databinding.ItemPaymentMethodBinding
import com.example.controledovitao.data.model.Payment

class PaymentAdapter(
    private val items: List<Payment>,
    private val onClick: (Payment) -> Unit
) : RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val binding = ItemPaymentMethodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class PaymentViewHolder(private val binding: ItemPaymentMethodBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Payment) {
            binding.tvMethodName.text = item.name
            binding.tvExpiry.text = "Vencimento dia ${item.shutdown ?: "--"}"

            binding.root.setOnClickListener { onClick(item) }
        }
    }
}