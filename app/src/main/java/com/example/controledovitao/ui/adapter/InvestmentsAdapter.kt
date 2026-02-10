package com.example.controledovitao.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.controledovitao.data.model.Invest
import com.example.controledovitao.databinding.ItemInvestmentBinding
import java.text.NumberFormat
import java.time.Period
import java.util.Locale

class InvestmentsAdapter : RecyclerView.Adapter<InvestmentsAdapter.InvestmentViewHolder>() {

    private var items: List<Invest> = emptyList()

    fun updateList(newItems: List<Invest>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    inner class InvestmentViewHolder(private val binding: ItemInvestmentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Invest) {
            val localeBR = Locale("pt", "BR")
            val formatter = NumberFormat.getCurrencyInstance(localeBR)

            binding.tvTitle.text = item.name

            val periodoObj = item.periodAsObject ?: Period.ZERO

            val anos = periodoObj.years
            val meses = periodoObj.months

            val textoPeriodo = StringBuilder()
            if (anos > 0) textoPeriodo.append("$anos ANO(S) ")
            if (anos > 0 && meses > 0) textoPeriodo.append("E ")
            if (meses > 0) textoPeriodo.append("$meses MES(ES)")

            val textoFinal = textoPeriodo.toString().trim()
            binding.tvBadge.text = if (textoFinal.isEmpty()) "RECENTE" else textoFinal

            binding.tvInvested.text = formatter.format(item.value)
            binding.tvTotal.text = formatter.format(item.estimate)

            val rendimento = item.estimateAsBigDecimal.subtract(item.valueAsBigDecimal)
            binding.tvYield.text = "+${formatter.format(rendimento)}"

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