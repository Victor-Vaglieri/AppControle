package com.example.controledovitao.ui

import android.content.ContentValues
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.controledovitao.databinding.ReportsBinding
import com.example.controledovitao.ui.adapter.ChartAdapter
import com.example.controledovitao.viewmodel.ReportItem
import com.example.controledovitao.viewmodel.ReportsViewModel
import java.io.OutputStreamWriter

class ReportsActivity : AppCompatActivity() {

    private lateinit var binding: ReportsBinding
    private lateinit var viewModel: ReportsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ReportsViewModel::class.java]

        TopBarHelper.setupTopBar(this, binding.topBar)

        setupCharts()
        setupOptions()
        setupExportButtons()
    }

    private fun setupCharts() {
        viewModel.charts.observe(this) { chartList ->
            val adapter = ChartAdapter(chartList)
            binding.recyclerCharts.layoutManager = LinearLayoutManager(this)
            binding.recyclerCharts.adapter = adapter
        }
    }

    private fun setupOptions() {
        viewModel.limitAlert.observe(this) { value ->
            binding.txtLimitValue.text = "$value%"
        }
        binding.btnLimitPlus.setOnClickListener { viewModel.changeLimit(5) }
        binding.btnLimitMinus.setOnClickListener { viewModel.changeLimit(-5) }

        viewModel.daysCount.observe(this) { value ->
            binding.txtDaysValue.text = value.toString()
        }
        binding.btnDaysPlus.setOnClickListener { viewModel.changeDays(1) }
        binding.btnDaysMinus.setOnClickListener { viewModel.changeDays(-1) }
    }

    private fun setupExportButtons() {
        binding.btnExportExcel.setOnClickListener {
            val reportList = viewModel.exportData.value ?: emptyList()
            if (reportList.isNotEmpty()) {
                Toast.makeText(this, "Gerando arquivo Excel...", Toast.LENGTH_SHORT).show()
                exportToCsv(reportList)
            } else {
                Toast.makeText(this, "Não há dados para exportar.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnExportPDF.setOnClickListener {
            val reportList = viewModel.exportData.value ?: emptyList()
            if (reportList.isNotEmpty()) {
                Toast.makeText(this, "Gerando arquivo PDF...", Toast.LENGTH_SHORT).show()
                exportToPdf(reportList)
            } else {
                Toast.makeText(this, "Não há dados para exportar.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun exportToCsv(reportList: List<ReportItem>) {
        val fileName = "Relatorio_Controle_Vitao_${System.currentTimeMillis()}.csv"

        try {
            val outputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/ControleDoVitao")
                }
                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let { contentResolver.openOutputStream(it) }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val appDir = java.io.File(downloadsDir, "ControleDoVitao")
                if (!appDir.exists()) appDir.mkdirs()

                val file = java.io.File(appDir, fileName)
                java.io.FileOutputStream(file)
            }

            outputStream?.use { stream ->
                val writer = OutputStreamWriter(stream)
                writer.append("Data,Categoria,Descrição,Valor\n")

                for (item in reportList) {
                    writer.append("${item.date},${item.category},${item.description},${item.value}\n")
                }

                writer.flush()
                Toast.makeText(this, "Salvo na pasta Downloads/ControleDoVitao", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Erro ao salvar planilha. Verifique as permissões.", Toast.LENGTH_SHORT).show()
        }
    }

    // --- LÓGICA DE EXPORTAÇÃO PARA PDF NA PASTA DOWNLOADS ---
    private fun exportToPdf(reportList: List<ReportItem>) {
        val fileName = "Relatorio_Controle_Vitao_${System.currentTimeMillis()}.pdf"

        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)

            val canvas: Canvas = page.canvas
            val paint = Paint()

            paint.color = Color.BLACK
            paint.textSize = 24f
            paint.isFakeBoldText = true
            canvas.drawText("Relatório - Controle do Vitão", 50f, 80f, paint)

            paint.textSize = 16f
            paint.isFakeBoldText = false
            canvas.drawText("Resumo das métricas:", 50f, 120f, paint)

            var yPosition = 160f

            for (item in reportList) {
                val textoLinha = "${item.date} - ${item.category} (${item.description}): R$ ${item.value}"
                canvas.drawText(textoLinha, 50f, yPosition, paint)
                yPosition += 30f
            }

            pdfDocument.finishPage(page)

            val outputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/ControleDoVitao")
                }
                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let { contentResolver.openOutputStream(it) }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val appDir = java.io.File(downloadsDir, "ControleDoVitao")
                if (!appDir.exists()) appDir.mkdirs()

                val file = java.io.File(appDir, fileName)
                java.io.FileOutputStream(file)
            }

            outputStream?.use { stream ->
                pdfDocument.writeTo(stream)
            }
            pdfDocument.close()

            Toast.makeText(this, "PDF salvo na pasta Downloads/ControleDoVitao", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Erro ao salvar PDF. Verifique as permissões.", Toast.LENGTH_SHORT).show()
        }
    }
}