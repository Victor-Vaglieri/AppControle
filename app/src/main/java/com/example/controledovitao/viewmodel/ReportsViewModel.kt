package com.example.controledovitao.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.controledovitao.data.repository.ReportsRepository
import com.example.controledovitao.ui.adapter.ChartData

data class ReportItem(
    val date: String,
    val category: String,
    val description: String,
    val value: Double
)

class ReportsViewModel : ViewModel() {

    private val repository = ReportsRepository()

    private val _charts = MutableLiveData<List<ChartData>>()
    val charts: LiveData<List<ChartData>> = _charts

    // Lista de dados para exportação
    private val _exportData = MutableLiveData<List<ReportItem>>()
    val exportData: LiveData<List<ReportItem>> = _exportData

    private val _limitAlert = MutableLiveData(80)
    val limitAlert: LiveData<Int> = _limitAlert

    private val _daysCount = MutableLiveData(30)
    val daysCount: LiveData<Int> = _daysCount

    init {
        startListening()
    }

    private fun startListening() {
        // Escuta os dados do gráfico
        repository.listenToChartsData { chartDataList ->
            _charts.value = chartDataList
        }

        // --- ALTERAÇÃO AQUI: Escuta os dados reais do Excel/PDF em tempo real ---
        repository.listenToExportData { exportList ->
            _exportData.value = exportList
        }
    }

    fun changeLimit(delta: Int) {
        val current = _limitAlert.value ?: 0
        _limitAlert.value = (current + delta).coerceIn(0, 100)
    }

    fun changeDays(delta: Int) {
        val current = _daysCount.value ?: 0
        val novoValor = (current + delta).coerceAtLeast(1)
        _daysCount.value = novoValor
    }
}