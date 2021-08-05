package ru.fhs.evotor.remonline.adapter

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import ru.fhs.evotor.remonline.R
import ru.fhs.evotor.remonline.model.ReceiptModel

open class ReceiptItem(private val receipt: ReceiptModel, private val listener: OnReceiptEventListener) : Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        with(viewHolder.itemView) {
            setOnClickListener { listener.onReceiptClick(receipt) }
        }
    }

    override fun getLayout(): Int = R.layout.item_receipt

    interface OnReceiptEventListener {
        fun onReceiptClick(receipt: ReceiptModel)
    }
}