package com.example.controledovitao.ui.components

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
        LayoutInflater.from(context).inflate(R.layout.item_option_row, this, true)
        setBackgroundResource(R.drawable.bg_card_surface)

        minimumHeight = (56 * resources.displayMetrics.density).toInt()

        val padding = (16 * resources.displayMetrics.density).toInt()
        setPadding(padding, padding, padding, padding)

        tvTitle = findViewById(R.id.textOption)
        ivIcon = findViewById(R.id.iconOption)

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