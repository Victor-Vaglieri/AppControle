package com.example.controledovitao.ui

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.controledovitao.databinding.SpentCreateBinding
import com.example.controledovitao.viewmodel.SpentViewModel
import java.math.BigDecimal
import java.util.Locale

class SpentCreateActivity : AppCompatActivity() {
    private lateinit var binding: SpentCreateBinding
    private lateinit var viewModel: SpentViewModel

    private val calendar = Calendar.getInstance()

    private var paymentMethods = listOf(
        Pair("Cartão de Crédito", true),
        Pair("Cartão de Débito", false),
        Pair("Dinheiro", false),
        Pair("Pix", false)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SpentCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[SpentViewModel::class.java]

        TopBarHelper.setupTopBar(this, binding.topBar)

        setupUI()
        setupListeners()
        setupObservers()
    }

    private fun setupUI() {
        atualizarDataNoInput()
        binding.inputValue.setText("0.00")

        paymentMethods = viewModel.methods


        if (paymentMethods.isNotEmpty()) {
            val (nome, isCredit) = paymentMethods[0]
            binding.inputMethod.text = nome
            configurarInputVezes(isCredit)
        }
    }

    private fun setupListeners() {
        binding.inputDate.setOnClickListener {
            mostrarCalendario()

        }

        binding.inputMethod.setOnClickListener { view ->
            val popup = PopupMenu(this, view)

            paymentMethods.forEach { (nome, _) ->
                popup.menu.add(nome)
            }

            popup.setOnMenuItemClickListener { item ->
                val selecionado = item.title.toString()
                binding.inputMethod.text = selecionado

                // Busca na lista se é crédito ou não
                val metodo = paymentMethods.find { it.first == selecionado }
                val isCredit = metodo?.second ?: false

                configurarInputVezes(isCredit)
                true
            }
            popup.show()
        }

        binding.btnValuePlus.setOnClickListener { ajustarValor(10.0) }
        binding.btnValueMinus.setOnClickListener { ajustarValor(-10.0) }

        binding.btnTimesPlus.setOnClickListener { ajustarParcelas(1) }
        binding.btnTimesMinus.setOnClickListener { ajustarParcelas(-1) }

        binding.btnAdd.setOnClickListener {
            salvarGasto()
        }
    }

    // Função que ativa ou desativa o bloco de "Vezes"
    private fun configurarInputVezes(ativar: Boolean) {
        // Habilita/Desabilita interações
        binding.inputTimes.isEnabled = ativar
        binding.btnTimesPlus.isEnabled = ativar
        binding.btnTimesMinus.isEnabled = ativar

        // Feedback Visual: Se desativado, fica meio transparente (0.5)
        val alpha = if (ativar) 1.0f else 0.3f
        binding.lblTimes.alpha = alpha
        binding.inputTimes.alpha = alpha
        binding.btnTimesPlus.alpha = alpha
        binding.btnTimesMinus.alpha = alpha

        // Se desativar, reseta para 1 parcela (opcional, mas recomendado)
        if (!ativar) {
            binding.inputTimes.setText("1")
        }
    }

    private fun setupObservers() {
        viewModel.saveSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Gasto salvo com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.errorMessage.observe(this) { erro ->
            Toast.makeText(this, erro, Toast.LENGTH_SHORT).show()
        }
    }

    private fun ajustarValor(delta: Double) {
        val atualString = binding.inputValue.text.toString().replace(",", ".")
        val atual = atualString.toDoubleOrNull() ?: 0.0
        val novo = (atual + delta).coerceAtLeast(0.0)
        binding.inputValue.setText(String.format(Locale.of("pt", "BR"), "%.2f", novo))
    }

    private fun ajustarParcelas(delta: Int) {
        if (!binding.inputTimes.isEnabled) return

        val atual = binding.inputTimes.text.toString().toIntOrNull() ?: 1
        val novo = (atual + delta).coerceAtLeast(1)
        binding.inputTimes.setText(novo.toString())
    }

    private fun salvarGasto() {
        val item = binding.inputItem.text.toString().trim()
        val metodo = binding.inputMethod.text.toString()
        val valorStr = binding.inputValue.text.toString().replace(",", ".")
        val parcelasStr = binding.inputTimes.text.toString()

        if (item.isEmpty()) {
            binding.inputItem.error = "Dê um nome para o gasto"
            return
        }

        val valor = valorStr.toBigDecimalOrNull()
        if (valor == null || valor <= BigDecimal("0")) {
            binding.inputValue.error = "Valor inválido"
            return
        }

        val parcelas = parcelasStr.toIntOrNull() ?: 1

        viewModel.saveExpense(
            title = item,
            method = metodo,
            value = valor,
            installments = parcelas,
            date = calendar.timeInMillis
        )
    }

    private fun mostrarCalendario() {
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                atualizarDataNoInput()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.maxDate = System.currentTimeMillis()
        datePicker.show()
    }

    private fun atualizarDataNoInput() {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.of("pt", "BR"))
        binding.inputDate.text = formato.format(calendar.time)
    }


}