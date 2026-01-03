package com.example.controledovitao.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.controledovitao.databinding.HomeBinding

class HomeActivity : AppCompatActivity() {

    // Cria o vínculo com o layout activity_home.xml
    private lateinit var binding: HomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Infla o layout
        binding = HomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Configurar os cliques e interações
        setupTopBar()
        setupOptions()
        setupChips()
    }

    private fun setupTopBar() {
        // COMO ACESSAR O <INCLUDE>:
        // Como você deu o id "@+id/topBar" no XML da Home, o ViewBinding cria uma variável 'topBar'.
        // Dentro dela, você acessa os itens do componente (btnConfig, btnHome, etc).

        binding.topBar.btnConfig.setOnClickListener {
            Toast.makeText(this, "Abrir Configurações", Toast.LENGTH_SHORT).show()
        }

        binding.topBar.btnHome.setOnClickListener {
            // Opcional: Recarregar a página ou rolar para o topo
            Toast.makeText(this, "Já estamos na Home", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupOptions() {
        // Configurando cliques nos seus componentes personalizados (OptionItemView)

        binding.optAddExpense.setOnClickListener {
            Toast.makeText(this, "Clicou em Adicionar Gasto", Toast.LENGTH_SHORT).show()
            // Futuramente: startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        binding.optSimulator.setOnClickListener {
            Toast.makeText(this, "Clicou em Simulador", Toast.LENGTH_SHORT).show()
        }

        binding.optInvestments.setOnClickListener {
            Toast.makeText(this, "Clicou em Investimentos", Toast.LENGTH_SHORT).show()
        }

        // Exemplo de clique num item da lista de gastos recentes
        binding.expense1.setOnClickListener {
            Toast.makeText(this, "Detalhes do Gasto X", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupChips() {
        // Apenas um exemplo visual para os botões de filtro (Todos, Visa, etc)
        // Aqui você acessa os TextViews que estão dentro do HorizontalScrollView

        // Dica: Como eles não tem ID no seu XML, se quiser dar clique neles,
        // volte no XML e adicione android:id="@+id/btnChipTodos", etc.
    }
}