package com.example.controledovitao.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.controledovitao.data.model.Invest // Seu Import
import com.example.controledovitao.databinding.ItemInvestmentBinding
import java.text.NumberFormat
import java.util.Locale

class InvestmentsAdapter(
    private val items: List<Invest>
) : RecyclerView.Adapter<InvestmentsAdapter.InvestmentViewHolder>() {

    inner class InvestmentViewHolder(val binding: ItemInvestmentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Invest) {
            val localeBR = Locale("pt", "BR")
            val formatter = NumberFormat.getCurrencyInstance(localeBR)

            binding.tvTitle.text = item.name

            val anos = item.period.years
            val meses = item.period.months

            val textoPeriodo = StringBuilder()
            if (anos > 0) textoPeriodo.append("$anos ANO(S) ")
            if (anos > 0 && meses > 0) textoPeriodo.append("E ")
            if (meses > 0) textoPeriodo.append("$meses MES(ES)")

            binding.tvBadge.text = textoPeriodo.toString().trim()

            binding.tvInvested.text = formatter.format(item.value)

            val rendimento = item.estimate.subtract(item.value)
            binding.tvYield.text = "+${formatter.format(rendimento)}"

            binding.tvTotal.text = formatter.format(item.estimate)

            binding.btnEdit.setOnClickListener {
                Toast.makeText(binding.root.context, "Editar ${item.name}", Toast.LENGTH_SHORT).show()
            }

            binding.btnWithdraw.setOnClickListener {
                Toast.makeText(binding.root.context, "Retirar ${item.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvestmentViewHolder {
        val binding = ItemInvestmentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return InvestmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InvestmentViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}