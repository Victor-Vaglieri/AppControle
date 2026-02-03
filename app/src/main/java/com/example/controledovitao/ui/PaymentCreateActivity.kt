package com.example.controledovitao.ui

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.controledovitao.databinding.PaymentMethodCreateBinding
import com.example.controledovitao.viewmodel.PaymentViewModel
import java.util.Locale

// TODO arrumar os campos de valores onde ao clicar se pode escrever
// tambem precisa arrumar o campo "tipo" onde não esta sendo listado os tipos disponiveis
class PaymentCreateActivity : AppCompatActivity() {

    private lateinit var binding: PaymentMethodCreateBinding
    private val viewModel: PaymentViewModel by viewModels()

    // Variáveis locais para controlar os valores da tela
    private var limitValue = 1800.0
    private var balanceValue = 24.56
    private var closeDate = 25
    private var dueDate = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PaymentMethodCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        TopBarHelper.setupTopBar(this, binding.topBar)
        updateUI()
        setupControls()
    }

    private fun setupControls() {
        // Limite
        binding.btnLimitMinus.setOnClickListener { limitValue -= 50; updateUI() }
        binding.btnLimitPlus.setOnClickListener { limitValue += 50; updateUI() }

        // Saldo
        binding.btnBalanceMinus.setOnClickListener { balanceValue -= 10; updateUI() }
        binding.btnBalancePlus.setOnClickListener { balanceValue += 10; updateUI() }

        // Data Fechamento (1 a 31)
        binding.btnCloseDateMinus.setOnClickListener { if (closeDate > 1) closeDate--; updateUI() }
        binding.btnCloseDatePlus.setOnClickListener { if (closeDate < 31) closeDate++; updateUI() }

        // Data Pagamento (1 a 31)
        binding.btnDueDateMinus.setOnClickListener { if (dueDate > 1) dueDate--; updateUI() }
        binding.btnDueDatePlus.setOnClickListener { if (dueDate < 31) dueDate++; updateUI() }

        // Botão Salvar
        binding.btnAdd.setOnClickListener {
            val name = binding.etMethodName.text.toString()
            if (name.isNotEmpty()) {
                viewModel.createPayment(
                    name = name,
                    type = "Crédito", // Fixo por enquanto ou pegue do spinner
                    limit = limitValue,
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
    }

    private fun updateUI() {
        binding.tvLimitValue.text = String.format(Locale("pt", "BR"), "%.2f", limitValue)
        binding.tvBalanceValue.text = String.format(Locale("pt", "BR"), "%.2f", balanceValue)
        binding.tvCloseDateValue.text = closeDate.toString()
        binding.tvDueDateValue.text = dueDate.toString()
    }
}