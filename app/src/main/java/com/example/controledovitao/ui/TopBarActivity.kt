package com.example.controledovitao.ui

import android.app.Activity
import android.widget.Toast
import com.example.controledovitao.databinding.HomeBinding

object TopBarHelper {

    fun setupTopBar(activity: Activity, binding: HomeBinding) {

        binding.topBar.btnConfig.setOnClickListener {
            Toast.makeText(activity, "Abrir Configurações", Toast.LENGTH_SHORT).show()
        }

        binding.topBar.btnHome.setOnClickListener {
            Toast.makeText(activity, "Já estamos na Home", Toast.LENGTH_SHORT).show()
        }
    }
}
