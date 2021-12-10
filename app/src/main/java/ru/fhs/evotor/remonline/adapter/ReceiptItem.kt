package ru.fhs.evotor.remonline.adapter

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_receipt.view.*
import ru.fhs.evotor.remonline.R
import ru.fhs.evotor.remonline.model.ReceiptModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

open class ReceiptItem(private val receipt: ReceiptModel, private val listener: OnReceiptEventListener) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        with(viewHolder.itemView) {
            tvTitle.text = receipt.title
            tvDate.text = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(receipt.date * 1000))

            val formatter: DecimalFormat = NumberFormat.getCurrencyInstance(Locale.US) as DecimalFormat
            formatter.maximumFractionDigits = 0
            val symbols: DecimalFormatSymbols = formatter.decimalFormatSymbols
            symbols.currencySymbol = ""
            formatter.decimalFormatSymbols = symbols

            val sumFormatted = formatter.format(receipt.total).replace(",", " ")
            tvTotal.text = context.getString(R.string.sum_s, sumFormatted)

            btReceipt.setOnClickListener { listener.onReceiptClick(receipt) }
        }
    }

    override fun getLayout(): Int = R.layout.item_receipt

    interface OnReceiptEventListener {
        fun onReceiptClick(receipt: ReceiptModel)
    }
}