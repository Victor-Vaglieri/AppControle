package com.example.controledovitao.ui.components


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
        LayoutInflater.from(context).inflate(R.layout.item_expense_row, this, true)
        setBackgroundResource(R.drawable.bg_card_surface)

        val padding = (16 * resources.displayMetrics.density).toInt()
        setPadding(padding, padding, padding, padding)

        tvTitle = findViewById(R.id.tvExpenseTitle)
        tvSubtitle = findViewById(R.id.tvExpenseSubtitle)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.ExpenseItemView)

            val title = typedArray.getString(R.styleable.ExpenseItemView_expenseTitle)
            tvTitle.text = title ?: "Gasto"

            val subtitle = typedArray.getString(R.styleable.ExpenseItemView_expenseSubtitle)
            tvSubtitle.text = subtitle ?: "R$ 0,00"

            typedArray.recycle()
        }
    }
    fun setExpenseTitle(title: String) {
        tvTitle.text = title
    }

    fun setExpenseSubtitle(subtitle: String) {
        tvSubtitle.text = subtitle
    }

}