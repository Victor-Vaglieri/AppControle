package com.example.controledovitao.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.controledovitao.databinding.ItemChartBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

data class ChartData(
    val month: String,
    val values: List<Float>
)

class ChartAdapter(private val items: List<ChartData>) : RecyclerView.Adapter<ChartAdapter.ChartViewHolder>() {

    inner class ChartViewHolder(val binding: ItemChartBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChartData) {
            binding.tvMonth.text = item.month.uppercase()

            val entries = ArrayList<BarEntry>()
            item.values.forEachIndexed { index, value ->
                entries.add(BarEntry(index.toFloat(), value))
            }

            val dataSet = BarDataSet(entries, "Gastos")
            // TODO ver cor
            dataSet.color = Color.parseColor("#4285F4")
            dataSet.valueTextColor = Color.WHITE
            dataSet.valueTextSize = 10f
            dataSet.setDrawValues(false)

            val barData = BarData(dataSet)
            barData.barWidth = 0.5f

            binding.barChart.apply {
                data = barData
                description.isEnabled = false
                legend.isEnabled = false

                // Eixo X (Embaixo)
                xAxis.setDrawGridLines(false)
                xAxis.setDrawLabels(false)
                xAxis.setDrawAxisLine(false)

                // Eixo Y (Direita - Desativa)
                axisRight.isEnabled = false

                // Eixo Y (Esquerda)
                axisLeft.isEnabled = true
                axisLeft.textColor = Color.WHITE
                axisLeft.setDrawGridLines(true)
                axisLeft.gridColor = Color.parseColor("#33FFFFFF")

                animateY(1000)
                invalidate()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartViewHolder {
        val binding = ItemChartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChartViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}