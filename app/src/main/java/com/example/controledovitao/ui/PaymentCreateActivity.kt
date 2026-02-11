package com.example.controledovitao.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.controledovitao.R
import com.example.controledovitao.databinding.PaymentMethodCreateBinding
import com.example.controledovitao.viewmodel.PaymentViewModel
import java.util.Locale

class PaymentCreateActivity : AppCompatActivity() {

    private lateinit var binding: PaymentMethodCreateBinding
    private lateinit var viewModel: PaymentViewModel

    private var limitValue = 1800.0
    private var balanceValue = 0.0
    private var closeDate = 25
    private var dueDate = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PaymentMethodCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[PaymentViewModel::class.java]

        TopBarHelper.setupTopBar(this, binding.topBar)

        updateUI()
        setupControls()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.operationStatus.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Método criado com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Erro ao criar. Verifique a conexão.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupControls() {
        binding.btnLimitMinus.setOnClickListener {
            limitValue = (limitValue - 50).coerceAtLeast(0.0)
            updateLimitText()
        }
        binding.btnLimitPlus.setOnClickListener {
            limitValue += 50
            updateLimitText()
        }

        binding.btnBalanceMinus.setOnClickListener {
            balanceValue -= 10
            updateBalanceText()
        }
        binding.btnBalancePlus.setOnClickListener {
            balanceValue += 10
            updateBalanceText()
        }

        binding.btnCloseDateMinus.setOnClickListener { if (closeDate > 1) closeDate--; updateDates() }
        binding.btnCloseDatePlus.setOnClickListener { if (closeDate < 31) closeDate++; updateDates() }

        binding.btnDueDateMinus.setOnClickListener { if (dueDate > 1) dueDate--; updateDates() }
        binding.btnDueDatePlus.setOnClickListener { if (dueDate < 31) dueDate++; updateDates() }

        binding.spinnerType.setOnClickListener {
            toggleTypeMenu()
        }

        binding.root.setOnClickListener {
            if (binding.containerTypeOptions.visibility == View.VISIBLE) toggleTypeMenu()
        }

        binding.containerTypeOptions.setOnCheckedChangeListener { _, checkedId ->
            val selectedText = when (checkedId) {
                R.id.rbCredit -> "Crédito"
                R.id.rbDebit -> "Débito"
                R.id.rbMoney -> "Dinheiro"
                else -> "Crédito"
            }
            binding.spinnerType.text = selectedText
            toggleTypeMenu()
        }

        binding.btnAdd.setOnClickListener {
            val name = binding.etMethodName.text.toString()
            val type = binding.spinnerType.text.toString()

            if (name.isNotEmpty()) {
                viewModel.createPayment(
                    name = name,
                    type = type,
                    limit = limitValue,
                    balance = balanceValue,
                    closeDay = closeDate,
                    dueDay = dueDate
                )
            } else {
                Toast.makeText(this, "Digite um nome para o método", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun toggleTypeMenu() {
        if (binding.containerTypeOptions.visibility == View.VISIBLE) {
            binding.containerTypeOptions.visibility = View.GONE
            binding.spinnerType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_down, 0)
        } else {
            binding.containerTypeOptions.visibility = View.VISIBLE
            binding.spinnerType.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_up, 0)
        }
    }

    private fun updateUI() {
        updateLimitText()
        updateBalanceText()
        updateDates()
    }

    private fun updateLimitText() {
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