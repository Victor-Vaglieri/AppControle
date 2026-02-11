package com.example.controledovitao.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.controledovitao.data.model.Options
import com.example.controledovitao.databinding.PaymentMethodEditBinding
import com.example.controledovitao.viewmodel.PaymentViewModel
import java.util.Locale

class PaymentEditActivity : AppCompatActivity() {

    private lateinit var binding: PaymentMethodEditBinding
    private lateinit var viewModel: PaymentViewModel

    private var originalName: String? = null
    private var limitValue = 0.0
    private var closeDate = 1
    private var dueDate = 1

    private var isDataLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PaymentMethodEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[PaymentViewModel::class.java]

        TopBarHelper.setupTopBar(this, binding.topBar)
        originalName = intent.getStringExtra("METHOD_NAME")

        setupObservers()
        setupControls()
        if (originalName != null) {
            viewModel.loadMethodByName(originalName!!)
        }
    }

    private fun setupObservers() {
        viewModel.selectedPayment.observe(this) { payment ->
            if (payment != null && !isDataLoaded) {
                isDataLoaded = true

                binding.etMethodName.setText(payment.name)
                val isCredit = payment.option == Options.CREDIT
                binding.tvTypeReadOnly.text = if (isCredit) "Crédito" else "Débito"

                limitValue = payment.limit ?: 0.0
                closeDate = payment.bestDate ?: 1
                dueDate = payment.shutdown ?: 1

                updateUI()
            }
        }


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

            if (newName.isNotEmpty() && originalName != null) {
                viewModel.updatePayment(
                    originalName = originalName!!,
                    name = newName,
                    limit = limitValue,
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
        binding.tvCloseDateValue.text = closeDate.toString()
        binding.tvDueDateValue.text = dueDate.toString()
    }
}