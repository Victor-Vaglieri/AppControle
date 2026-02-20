package com.example.controledovitao.ui

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.controledovitao.databinding.PaymentMethodEditBinding
import com.example.controledovitao.viewmodel.PaymentViewModel
import java.util.Locale

class PaymentEditActivity : AppCompatActivity() {

    private lateinit var binding: PaymentMethodEditBinding
    private lateinit var viewModel: PaymentViewModel

    private var originalName: String? = null
    private var limitValue = 0.0
    private var balanceValue = 0.0 // --- NOVO: Variável para o saldo
    private var closeDate = 1
    private var dueDate = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PaymentMethodEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[PaymentViewModel::class.java]

        TopBarHelper.setupTopBar(this, binding.topBar)

        // Resgatando todos os dados que vieram da tela de Lista
        originalName = intent.getStringExtra("METHOD_NAME")
        val originalType = intent.getStringExtra("METHOD_TYPE") ?: "credit"
        limitValue = intent.getDoubleExtra("METHOD_LIMIT", 0.0)
        balanceValue = intent.getDoubleExtra("METHOD_BALANCE", 0.0) // --- NOVO: Lendo saldo da intent
        closeDate = intent.getIntExtra("METHOD_CLOSE", 1)
        dueDate = intent.getIntExtra("METHOD_DUE", 1)

        // Preenchendo a tela instantaneamente
        binding.etMethodName.setText(originalName)
        val isCredit = originalType.equals("credit", ignoreCase = true)
        binding.tvTypeReadOnly.text = if (isCredit) "Crédito" else "Débito"

        setupObservers()
        setupControls()
        updateUI() // Atualiza os números na tela, incluindo o novo saldo
    }

    private fun setupObservers() {
        viewModel.operationStatus.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Cartão atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Erro ao atualizar. Tente novamente.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupControls() {
        // --- CONTROLES DE LIMITE ---
        binding.btnLimitMinus.setOnClickListener {
            limitValue = (limitValue - 50).coerceAtLeast(0.0)
            updateUI()
        }
        binding.btnLimitPlus.setOnClickListener {
            limitValue += 50
            updateUI()
        }

        // --- CONTROLES DE SALDO (NOVO) ---
        binding.btnBalanceMinus.setOnClickListener {
            balanceValue -= 10
            updateUI()
        }
        binding.btnBalancePlus.setOnClickListener {
            balanceValue += 10
            updateUI()
        }

        // Capturar valor digitado manualmente no Saldo
        binding.tvBalanceValue.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                val cleanStr = v.text.toString().replace(".", "").replace(",", ".")
                balanceValue = cleanStr.toDoubleOrNull() ?: balanceValue
                updateUI()

                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                v.clearFocus()
                true
            } else {
                false
            }
        }

        // --- CONTROLES DE DATAS ---
        binding.btnCloseDateMinus.setOnClickListener {
            if (closeDate > 1) closeDate--
            updateUI()
        }
        binding.btnCloseDatePlus.setOnClickListener {
            if (closeDate < 31) closeDate++
            updateUI()
        }

        binding.btnDueDateMinus.setOnClickListener {
            if (dueDate > 1) dueDate--
            updateUI()
        }
        binding.btnDueDatePlus.setOnClickListener {
            if (dueDate < 31) dueDate++
            updateUI()
        }

        // --- BOTÃO EDITAR ---
        binding.btnEdit.setOnClickListener {
            val newName = binding.etMethodName.text.toString()

            // Garantir que pega o valor digitado caso o usuário não tenha dado Enter
            val currentBalanceStr = binding.tvBalanceValue.text.toString().replace(".", "").replace(",", ".")
            balanceValue = currentBalanceStr.toDoubleOrNull() ?: balanceValue

            if (newName.isNotEmpty() && originalName != null) {
                viewModel.updatePayment(
                    originalName = originalName!!,
                    name = newName,
                    limit = limitValue,
                    balance = balanceValue,
                    closeDay = closeDate,
                    dueDay = dueDate
                )
            } else {
                Toast.makeText(this, "O nome não pode ser vazio", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI() {
        val localeBR = Locale("pt", "BR")
        binding.tvLimitValue.text = String.format(localeBR, "%.2f", limitValue)

        // --- NOVO: Atualizando o texto do Saldo
        binding.tvBalanceValue.setText(String.format(localeBR, "%.2f", balanceValue))

        binding.tvCloseDateValue.text = closeDate.toString()
        binding.tvDueDateValue.text = dueDate.toString()
    }
}