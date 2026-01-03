package com.example.controledovitao.views


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.controledovitao.R

class ExpenseItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val tvTitle: TextView
    private val tvSubtitle: TextView

    init {
        // 1. Infla o layout NOVO
        LayoutInflater.from(context).inflate(R.layout.item_expense_row, this, true)

        // 2. Define visual (Fundo e Padding)
        setBackgroundResource(R.drawable.bg_card_surface)

        val padding = (16 * resources.displayMetrics.density).toInt()
        setPadding(padding, padding, padding, padding)

        // 3. Pega referências
        tvTitle = findViewById(R.id.tvExpenseTitle)
        tvSubtitle = findViewById(R.id.tvExpenseSubtitle)

        // 4. Lê os atributos
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.ExpenseItemView)

            val title = typedArray.getString(R.styleable.ExpenseItemView_expenseTitle)
            tvTitle.text = title ?: "Gasto"

            val subtitle = typedArray.getString(R.styleable.ExpenseItemView_expenseSubtitle)
            tvSubtitle.text = subtitle ?: "R$ 0,00"

            typedArray.recycle()
        }
    }
}