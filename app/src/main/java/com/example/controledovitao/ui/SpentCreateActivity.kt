package com.example.controledovitao.ui

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.controledovitao.R
import com.example.controledovitao.data.model.Options
import com.example.controledovitao.data.model.Payment
import com.example.controledovitao.databinding.SpentCreateBinding
import com.example.controledovitao.ui.adapter.PaymentSpinnerAdapter
import com.example.controledovitao.viewmodel.PaymentViewModel
import com.example.controledovitao.viewmodel.SpentViewModel
import java.math.BigDecimal
import java.util.Locale

class SpentCreateActivity : AppCompatActivity() {

    private lateinit var binding: SpentCreateBinding

    private lateinit var viewModel: SpentViewModel

    private val paymentViewModel: PaymentViewModel by viewModels()

    private lateinit var methodAdapter: PaymentSpinnerAdapter
    private var selectedPayment: Payment? = null

    private val calendar = Calendar.getInstance()

    // Variáveis de controle local
    private var amountValue = 0.0
    private var timesValue = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SpentCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[SpentViewModel::class.java]

        TopBarHelper.setupTopBar(this, binding.topBar)

        setupUI()
        setupDropdown()
        setupListeners()
        setupObservers()

        paymentViewModel.loadMethods()
    }

    private fun setupUI() {
        atualizarDataNoInput()
        binding.inputValue.setText("0,00")

        configurarInputVezes(false)
    }

    private fun setupDropdown() {
        methodAdapter = PaymentSpinnerAdapter(emptyList()) { payment ->
            selectedPayment = payment
            binding.inputMethod.text = payment.name

            binding.recyclerMethods.visibility = View.GONE
            binding.iconArrowMethod.setImageResource(R.drawable.icon_down)

            val isCredit = (payment.option == Options.CREDIT)
            configurarInputVezes(isCredit)
        }

        binding.recyclerMethods.layoutManager = LinearLayoutManager(this)
        binding.recyclerMethods.adapter = methodAdapter
        val toggleListener = View.OnClickListener {
            if (binding.recyclerMethods.visibility == View.VISIBLE) {
                binding.recyclerMethods.visibility = View.GONE
                binding.iconArrowMethod.setImageResource(R.drawable.icon_down)
            } else {
                binding.recyclerMethods.visibility = View.VISIBLE
                binding.iconArrowMethod.setImageResource(R.drawable.icon_up)
            }
        }

        binding.inputMethod.setOnClickListener(toggleListener)
        binding.iconArrowMethod.setOnClickListener(toggleListener)
    }

    private fun setupListeners() {
        binding.inputDate.setOnClickListener {
            mostrarCalendario()
        }

        // Botões de Valor
        binding.btnValuePlus.setOnClickListener { ajustarValor(10.0) }
        binding.btnValueMinus.setOnClickListener { ajustarValor(-10.0) }

        // Botões de Vezes
        binding.btnTimesPlus.setOnClickListener { ajustarParcelas(1) }
        binding.btnTimesMinus.setOnClickListener { ajustarParcelas(-1) }

        binding.btnAdd.setOnClickListener {
            salvarGasto()
        }
    }

    private fun setupObservers() {
        paymentViewModel.paymentMethods.observe(this) { methods ->
            methodAdapter.updateList(methods)

            if (selectedPayment == null && methods.isNotEmpty()) {
                val first = methods[0]
                selectedPayment = first
                binding.inputMethod.text = first.name
                configurarInputVezes(first.option == Options.CREDIT)
            }
        }

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

    private fun configurarInputVezes(ativar: Boolean) {
        binding.inputTimes.isEnabled = ativar
        binding.btnTimesPlus.isEnabled = ativar
        binding.btnTimesMinus.isEnabled = ativar

        val alpha = if (ativar) 1.0f else 0.3f
        binding.lblTimes.alpha = alpha
        binding.inputTimes.alpha = alpha
        binding.btnTimesPlus.alpha = alpha
        binding.btnTimesMinus.alpha = alpha

        if (!ativar) {
            timesValue = 1
            binding.inputTimes.setText("1")
        }
    }

    private fun ajustarValor(delta: Double) {
        val valorTexto = binding.inputValue.text.toString()
            .replace(".", "")
            .replace(",", ".")

        val atual = valorTexto.toDoubleOrNull() ?: amountValue
        val novo = (atual + delta).coerceAtLeast(0.0)

        amountValue = novo
        binding.inputValue.setText(String.format(Locale("pt", "BR"), "%.2f", novo))
    }

    private fun ajustarParcelas(delta: Int) {
        if (!binding.inputTimes.isEnabled) return

        val atual = binding.inputTimes.text.toString().toIntOrNull() ?: timesValue
        val novo = (atual + delta).coerceAtLeast(1)

        timesValue = novo
        binding.inputTimes.setText(novo.toString())
    }

    private fun salvarGasto() {
        val item = binding.inputItem.text.toString().trim()
        val valorStr = binding.inputValue.text.toString()
            .replace(".", "")
            .replace(",", ".")

        val valor = valorStr.toBigDecimalOrNull()

        if (item.isEmpty()) {
            binding.inputItem.error = "Dê um nome para o gasto"
            return
        }

        if (selectedPayment == null) {
            Toast.makeText(this, "Selecione um método de pagamento", Toast.LENGTH_SHORT).show()
            return
        }

        if (valor == null || valor <= BigDecimal("0")) {
            binding.inputValue.error = "Valor inválido"
            return
        }

        val parcelas = binding.inputTimes.text.toString().toIntOrNull() ?: 1

        viewModel.saveExpense(
            title = item,
            method = selectedPayment!!.name,
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
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        binding.inputDate.text = formato.format(calendar.time)
    }
}