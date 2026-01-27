package com.example.controledovitao.ui

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.controledovitao.data.model.Spent
import com.example.controledovitao.databinding.SpentViewBinding
import java.text.NumberFormat
import java.util.Locale

class SpentViewActivity : AppCompatActivity() {

    private lateinit var binding: SpentViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SpentViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        TopBarHelper.setupTopBar(this, binding.topBar)

        val spent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("EXTRA_SPENT", Spent::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("EXTRA_SPENT")
        }

        if (spent != null) {
            setupData(spent)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupData(item: Spent) {
        binding.tvDetailItem.text = item.name
        val localeBR = Locale("pt", "BR")
        val numberFormat = NumberFormat.getCurrencyInstance(localeBR)
        binding.tvDetailValue.text = numberFormat.format(item.value)
        binding.tvDetailTimes.text = item.times.toString()
        binding.tvDetailMethod.text = "Visa Crédito"
        binding.calendarDetail.date = item.spentDate
        binding.calendarDetail.isEnabled = false
    }
}