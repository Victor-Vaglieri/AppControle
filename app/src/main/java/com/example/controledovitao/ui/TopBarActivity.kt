package com.example.controledovitao.ui

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.example.controledovitao.databinding.ComponentTopBarBinding

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

object TopBarHelper {

    fun setupConnectionStatus(binding: ComponentTopBarBinding, isConnected: Boolean) {
        if (isConnected) {
            // 1. Mostra o ponto (o texto vai deslizar sozinho pelo XML)
            binding.statusDot.visibility = View.VISIBLE

            // 2. Cria animação de Pulso (Escala + Transparência)
            val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.2f, 1f)
            val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.2f, 1f)
            val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0.5f, 1f)

            val animator = ObjectAnimator.ofPropertyValuesHolder(
                binding.statusDot, scaleX, scaleY, alpha
            )

            animator.duration = 1500 // 1.5 segundos
            animator.repeatCount = ObjectAnimator.INFINITE // Repete para sempre
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.start()

        } else {
            // Se desconectar, esconde o ponto
            binding.statusDot.visibility = View.GONE
            binding.statusDot.animate().cancel() // Para a animação
        }
    }

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