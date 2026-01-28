package com.example.controledovitao.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.controledovitao.data.repository.ReportsRepository
import com.example.controledovitao.ui.adapter.ChartData

class ReportsViewModel : ViewModel() {

    private val repository = ReportsRepository()
    private val _charts = MutableLiveData<List<ChartData>>()
    val charts: LiveData<List<ChartData>> = _charts

    private val _limitAlert = MutableLiveData(80)
    val limitAlert: LiveData<Int> = _limitAlert

    private val _daysCount = MutableLiveData(30)
    val daysCount: LiveData<Int> = _daysCount

    init {
        loadCharts()
    }

    private fun loadCharts() {
        _charts.value = repository.getCharts()
    }

    fun changeLimit(delta: Int) {
        val current = _limitAlert.value ?: 0
        _limitAlert.value = (current + delta).coerceIn(0, 100)
    }

    fun changeDays(delta: Int) {
        val current = _daysCount.value ?: 0
        _daysCount.value = (current + delta).coerceAtLeast(1)
    }
}