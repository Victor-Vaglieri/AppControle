package com.example.controledovitao.ui

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.example.controledovitao.databinding.ComponentTopBarBinding

object TopBarHelper {

    fun setupTopBar(activity: Activity, topBarBinding: ComponentTopBarBinding) {

        topBarBinding.btnConfig.setOnClickListener {
            if (activity is ConfigActivity) {
                Toast.makeText(activity, "Já estamos na Configurações", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(activity, ConfigActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                activity.startActivity(intent)
                activity.finish()
            }
        }

        topBarBinding.btnHome.setOnClickListener {
            if (activity is HomeActivity) {
                Toast.makeText(activity, "Já estamos na Home", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(activity, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                activity.startActivity(intent)
                activity.finish()
            }
        }
    }
}