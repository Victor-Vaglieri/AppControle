package com.example.controledovitao.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.controledovitao.R

class OptionItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val tvTitle: TextView
    private val ivIcon: ImageView

    init {
        // 1. Infla o layout
        LayoutInflater.from(context).inflate(R.layout.item_option_row, this, true)

        // 2. Define o visual do container
        setBackgroundResource(R.drawable.bg_card_surface)

        // Define uma altura mínima para garantir que o botão apareça mesmo se der erro no texto
        minimumHeight = (56 * resources.displayMetrics.density).toInt()

        // Padding (16dp)
        val padding = (16 * resources.displayMetrics.density).toInt()
        setPadding(padding, padding, padding, padding)

        // 3. Pega referências
        tvTitle = findViewById(R.id.textOption)
        ivIcon = findViewById(R.id.iconOption)

        // 4. Lê os atributos (Agora usando os NOMES NOVOS cvTitle e cvIcon)
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.OptionItemView)

            val text = typedArray.getString(R.styleable.OptionItemView_cvTitle)
            tvTitle.text = text ?: "Opção"

            val iconResId = typedArray.getResourceId(R.styleable.OptionItemView_cvIcon, 0)
            if (iconResId != 0) {
                ivIcon.setImageResource(iconResId)
            }

            typedArray.recycle()
        }
    }
}