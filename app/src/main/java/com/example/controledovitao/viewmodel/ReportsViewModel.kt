package com.example.controledovitao.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.controledovitao.ui.adapter.ChartData

class ReportsViewModel : ViewModel() {

    // TODO mandar isso para repository
    val mockCharts = listOf(
        ChartData("Dezembro", listOf(200f, 450f, 300f, 800f, 150f)),
        ChartData("Novembro", listOf(600f, 200f, 500f, 300f, 400f))
    )
    private val _limitAlert = MutableLiveData(80) // Começa em 80%
    val limitAlert: LiveData<Int> = _limitAlert

    private val _daysCount = MutableLiveData(30) // Começa em 30 dias
    val daysCount: LiveData<Int> = _daysCount

    fun changeLimit(delta: Int) {
        val current = _limitAlert.value ?: 0
        _limitAlert.value = (current + delta).coerceIn(0, 100)
    }

    fun changeDays(delta: Int) {
        val current = _daysCount.value ?: 0
        _daysCount.value = (current + delta).coerceAtLeast(1)
    }
}