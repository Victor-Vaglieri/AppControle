package com.example.controledovitao.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.controledovitao.R
import com.example.controledovitao.databinding.ItemChartBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

data class ChartData(
    val month: String,
    val values: List<Float>
)

class ChartAdapter(private val items: List<ChartData>) : RecyclerView.Adapter<ChartAdapter.ChartViewHolder>() {

    inner class ChartViewHolder(val binding: ItemChartBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChartData) {
            val context = binding.root.context

            val colorPrimary = ContextCompat.getColor(context, R.color.highlight_darkest)
            val colorText = ContextCompat.getColor(context, R.color.neutral_dark_darkest)
            val colorGrid = ContextCompat.getColor(context, R.color.neutral_dark_dark)

            binding.tvMonth.text = item.month.uppercase()
            binding.tvMonth.setTextColor(ContextCompat.getColor(context, R.color.neutral_light_lightest))
            binding.tvMonth.setBackgroundColor(ContextCompat.getColor(context, R.color.highlight_darkest))

            val entries = ArrayList<BarEntry>()
            item.values.forEachIndexed { index, value ->
                entries.add(BarEntry(index.toFloat(), value))
            }

            val dataSet = BarDataSet(entries, "Gastos")
            dataSet.color = colorPrimary
            dataSet.setDrawValues(false)

            val barData = BarData(dataSet)
            barData.barWidth = 0.5f

            binding.barChart.apply {
                data = barData

                description.isEnabled = false
                legend.isEnabled = false
                isScaleXEnabled = false
                isScaleYEnabled = false
                setPinchZoom(false)

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)

                    textColor = colorText
                    textSize = 12f

                    axisLineColor = Color.TRANSPARENT

                    valueFormatter = IndexAxisValueFormatter(listOf("Sem 1", "Sem 2", "Sem 3", "Sem 4", "Sem 5"))
                    granularity = 1f
                }

                axisLeft.apply {
                    isEnabled = true

                    textColor = colorText
                    textSize = 12f

                    setDrawGridLines(true)
                    gridColor = colorGrid

                    axisLineColor = Color.TRANSPARENT
                    setDrawAxisLine(false)
                    axisMinimum = 0f
                }

                axisRight.isEnabled = false


                extraBottomOffset = 16f

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