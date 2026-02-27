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
    private var balanceValue = 0.0
    private var closeDate = 1
    private var dueDate = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PaymentMethodEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[PaymentViewModel::class.java]

        TopBarHelper.setupTopBar(this, binding.topBar)

        originalName = intent.getStringExtra("METHOD_NAME")
        val originalType = intent.getStringExtra("METHOD_TYPE") ?: "credit"
        limitValue = intent.getDoubleExtra("METHOD_LIMIT", 0.0)
        balanceValue = intent.getDoubleExtra("METHOD_BALANCE", 0.0)
        closeDate = intent.getIntExtra("METHOD_CLOSE", 1)
        dueDate = intent.getIntExtra("METHOD_DUE", 1)

        binding.etMethodName.setText(originalName)
        val isCredit = originalType.equals("credit", ignoreCase = true)
        val isDebit = originalType.equals("debit", ignoreCase = true)
        binding.tvTypeReadOnly.text = when {
            isCredit -> "Crédito"
            isDebit -> "Débito"
            else -> "Dinheiro"
        }

        updateFieldsVisibility(isCredit)

        setupObservers()
        setupControls()
        updateUI()
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
        binding.btnLimitMinus.setOnClickListener {
            limitValue = (limitValue - 50).coerceAtLeast(0.0)
            updateUI()
        }
        binding.btnLimitPlus.setOnClickListener {
            limitValue += 50
            updateUI()
        }
        binding.btnBalanceMinus.setOnClickListener {
            balanceValue -= 10
            updateUI()
        }
        binding.btnBalancePlus.setOnClickListener {
            balanceValue += 10
            updateUI()
        }

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

        binding.btnEdit.setOnClickListener {
            val newName = binding.etMethodName.text.toString()

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

        binding.tvBalanceValue.setText(String.format(localeBR, "%.2f", balanceValue))

        binding.tvCloseDateValue.text = closeDate.toString()
        binding.tvDueDateValue.text = dueDate.toString()
    }
    private fun updateFieldsVisibility(isCredit: Boolean) {
        binding.btnLimitMinus.isEnabled = isCredit
        binding.btnLimitPlus.isEnabled = isCredit
        binding.tvLimitValue.isEnabled = isCredit

        binding.btnCloseDateMinus.isEnabled = isCredit
        binding.btnCloseDatePlus.isEnabled = isCredit

        binding.btnDueDateMinus.isEnabled = isCredit
        binding.btnDueDatePlus.isEnabled = isCredit

        val alphaValue = if (isCredit) 1.0f else 0.3f

        binding.btnLimitMinus.alpha = alphaValue
        binding.btnLimitPlus.alpha = alphaValue
        binding.tvLimitValue.alpha = alphaValue

        binding.btnCloseDateMinus.alpha = alphaValue
        binding.btnCloseDatePlus.alpha = alphaValue
        binding.tvCloseDateValue.alpha = alphaValue

        binding.btnDueDateMinus.alpha = alphaValue
        binding.btnDueDatePlus.alpha = alphaValue
        binding.tvDueDateValue.alpha = alphaValue
    }
}