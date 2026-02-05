package com.example.controledovitao.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.controledovitao.databinding.PaymentMethodCreateBinding
import com.example.controledovitao.viewmodel.PaymentViewModel
import com.example.controledovitao.R
import java.util.Locale

class PaymentCreateActivity : AppCompatActivity() {

    private lateinit var binding: PaymentMethodCreateBinding
    private val viewModel: PaymentViewModel by viewModels()

    // Variáveis locais
    private var limitValue = 1800.0
    private var balanceValue = 24.56
    private var closeDate = 25
    private var dueDate = 2

    private var isEditing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PaymentMethodCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        TopBarHelper.setupTopBar(this, binding.topBar)

        updateUI()

        setupControls()
    }

    private fun setupControls() {
        binding.btnLimitMinus.setOnClickListener {
            limitValue -= 50
            updateLimitText()
        }
        binding.btnLimitPlus.setOnClickListener {
            limitValue += 50
            updateLimitText()
        }

        // --- SALDO ---
        binding.btnBalanceMinus.setOnClickListener {
            balanceValue -= 10
            updateBalanceText()
        }
        binding.btnBalancePlus.setOnClickListener {
            balanceValue += 10
            updateBalanceText()
        }

        // --- DATA FECHAMENTO ---
        binding.btnCloseDateMinus.setOnClickListener { if (closeDate > 1) closeDate--; updateDates() }
        binding.btnCloseDatePlus.setOnClickListener { if (closeDate < 31) closeDate++; updateDates() }

        // --- DATA PAGAMENTO ---
        binding.btnDueDateMinus.setOnClickListener { if (dueDate > 1) dueDate--; updateDates() }
        binding.btnDueDatePlus.setOnClickListener { if (dueDate < 31) dueDate++; updateDates() }

        binding.btnAdd.setOnClickListener {
            val name = binding.etMethodName.text.toString()
            val finalLimit = try {
                binding.tvLimitValue.text.toString().replace(".", "").replace(",", ".").toDouble()
            } catch (e: Exception) { limitValue }

            if (name.isNotEmpty()) {
                viewModel.createPayment(
                    name = name,
                    type = binding.spinnerType.text.toString(),
                    limit = finalLimit,
                    balance = balanceValue,
                    closeDay = closeDate,
                    dueDay = dueDate
                )
                Toast.makeText(this, "Criado com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Digite um nome", Toast.LENGTH_SHORT).show()
            }
        }

        binding.spinnerType.setOnClickListener {
            if (binding.containerTypeOptions.visibility == View.VISIBLE) {
                binding.containerTypeOptions.visibility = View.GONE
                binding.spinnerType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_down, 0)
            } else {
                binding.containerTypeOptions.visibility = View.VISIBLE
                binding.spinnerType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_up, 0)
            }
        }

        binding.containerTypeOptions.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbCredit -> binding.spinnerType.text = "Crédito"
                R.id.rbDebit -> binding.spinnerType.text = "Débito"
                R.id.rbMoney -> binding.spinnerType.text = "Dinheiro"
            }
            binding.containerTypeOptions.visibility = View.GONE
            binding.spinnerType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_down, 0)
        }
    }


    private fun updateUI() {
        updateLimitText()
        updateBalanceText()
        updateDates()
    }

    private fun updateLimitText() {
        // Formata para 1.800,00
        binding.tvLimitValue.setText(String.format(Locale("pt", "BR"), "%.2f", limitValue))
    }

    private fun updateBalanceText() {
        binding.tvBalanceValue.setText(String.format(Locale("pt", "BR"), "%.2f", balanceValue))
    }

    private fun updateDates() {
        binding.tvCloseDateValue.text = closeDate.toString()
        binding.tvDueDateValue.text = dueDate.toString()
    }
}