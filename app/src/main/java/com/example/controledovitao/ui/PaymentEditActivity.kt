package com.example.controledovitao.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.controledovitao.databinding.PaymentMethodEditBinding
import com.example.controledovitao.viewmodel.PaymentViewModel
import java.util.Locale

class PaymentEditActivity : AppCompatActivity() {

    private lateinit var binding: PaymentMethodEditBinding
    private val viewModel: PaymentViewModel by viewModels()
    private var originalName: String? = null

    // Variáveis locais
    private var limitValue = 0.0
    private var closeDate = 1
    private var dueDate = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PaymentMethodEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        TopBarHelper.setupTopBar(this, binding.topBar)

        originalName = intent.getStringExtra("METHOD_NAME")
        if (originalName != null) {
            viewModel.loadMethodByName(originalName!!)
        }

        setupObservers()
        setupControls()
    }

    private fun setupObservers() {
        viewModel.selectedPayment.observe(this) { payment ->
            if (payment != null) {
                // Preenche a tela com os dados vindos do Repository
                binding.etMethodName.setText(payment.name)
                binding.tvTypeReadOnly.text = if (payment.option == com.example.controledovitao.data.model.Options.CREDIT) "Crédito" else "Débito"

                limitValue = payment.limit?.toDouble() ?: 0.0
                closeDate = payment.bestDate ?: 1
                dueDate = payment.shutdown ?: 1

                updateUI()
            }
        }
    }

    private fun setupControls() {
        // Mesma lógica de controles do Create, mas sem "Balance"
        binding.btnLimitMinus.setOnClickListener { limitValue -= 50; updateUI() }
        binding.btnLimitPlus.setOnClickListener { limitValue += 50; updateUI() }

        binding.btnCloseDateMinus.setOnClickListener { if (closeDate > 1) closeDate--; updateUI() }
        binding.btnCloseDatePlus.setOnClickListener { if (closeDate < 31) closeDate++; updateUI() }

        binding.btnDueDateMinus.setOnClickListener { if (dueDate > 1) dueDate--; updateUI() }
        binding.btnDueDatePlus.setOnClickListener { if (dueDate < 31) dueDate++; updateUI() }

        binding.btnEdit.setOnClickListener {
            val newName = binding.etMethodName.text.toString()
            if (newName.isNotEmpty() && originalName != null) {
                viewModel.updatePayment(
                    originalName = originalName!!,
                    name = newName,
                    limit = limitValue,
                    closeDay = closeDate,
                    dueDay = dueDate
                )
                Toast.makeText(this, "Atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun updateUI() {
        binding.tvLimitValue.text = String.format(Locale("pt", "BR"), "%.2f", limitValue)
        binding.tvCloseDateValue.text = closeDate.toString()
        binding.tvDueDateValue.text = dueDate.toString()
    }
}