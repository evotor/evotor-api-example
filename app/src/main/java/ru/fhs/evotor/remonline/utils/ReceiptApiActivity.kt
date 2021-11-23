package ru.fhs.evotor.remonline.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_receipt_api.*
import ru.evotor.framework.receipt.ExtraKey
import ru.evotor.framework.receipt.Receipt
import ru.evotor.framework.receipt.ReceiptApi.getPositionsByBarcode
import ru.evotor.framework.receipt.ReceiptApi.getReceipt
import ru.evotor.integrations.BarcodeBroadcastReceiver
import ru.fhs.evotor.remonline.R

/**
 * Пример работы с ReceiptAPI
 */
class ReceiptApiActivity : AppCompatActivity() {
    private var mBarcodeBroadcastReceiver: BarcodeBroadcastReceiver? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_api)

        //Получение заголовков открытого чека
        /*findViewById<View>(R.id.btnGetHeaders).setOnClickListener {
            tvLog.text = "Headers type:${if (rbSell.isChecked) "SELL" else "PAYBACK"}".trimIndent()
            val sb = StringBuffer()
            val cursor = ReceiptApi.getReceiptHeaders(
                this@ReceiptApiActivity,
                if (rbSell.isChecked) Receipt.Type.SELL else Receipt.Type.PAYBACK
            )
            try {
                if (cursor != null) {
                    cursor.moveToFirst()
                    while (cursor.moveToNext()) {
                        sb.append("NUMBER: ").append(cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER))).append("\n")
                        sb.append("UUID: ").append(cursor.getString(cursor.getColumnIndex(COLUMN_UUID))).append("\n")
                        sb.append("CLIENT_EMAIL: ").append(cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_EMAIL))).append("\n")
                        sb.append("CLIENT_PHONE: ").append(cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_PHONE))).append("\n")
                        sb.append("DATE: ").append(cursor.getString(cursor.getColumnIndex(COLUMN_DATE))).append("\n")
                        //sb.append("EXTRA: ").append(cursor.getString(cursor.getColumnIndex(ReceiptHeaderTable.COLUMN_EXTRA))).append("\n");
                        sb.append("TYPE: ").append(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE))).append("\n===\n")
                    }
                    tvLog.setText(sb.toString())
                }
            } finally {
                if (cursor != null) {
                    cursor.close()
                }
            }
        }*/

        //Поиск товаров в чеке по штрихкоду
        findViewById<View>(R.id.btnReceiptBarcode).setOnClickListener {
            tvLog.text = "Receipt by Barcode\n"
            val sb = StringBuffer()
            val list = getPositionsByBarcode(this@ReceiptApiActivity, editText.getText().toString())
            sb.append("===Get positions===\n")
            for (item in list) {
                sb.append(item.toString()).append("\n")
            }
            tvLog.text = sb.toString()
        }

        //Получение чека по его UUID
        findViewById<View>(R.id.btnReceiptById).setOnClickListener {
            tvLog.text = "Receipt by UUID\n"
            val sb = StringBuffer()
            val slip = getReceipt(this@ReceiptApiActivity, editText.getText().toString())
            if (slip != null) {
                sb.append("===Get positions===\n")
                for (item in slip.getPositions()) {
                    sb.append(item.toString()).append("\n")
                }
                sb.append("==Header===\n").append(slip.header.toString()).append("\n")
                sb.append("===Payments===\n")
                for (item in slip.getPayments()) {
                    sb.append(item.toString()).append("\n")
                }
                tvLog.text = sb.toString()
            } else {
                tvLog.text = "Receipt: NULL"
            }
        }

        //Получение открытого чека
        findViewById<View>(R.id.btnGetReceiptOpen).setOnClickListener {
            tvLog.text = "Receipt.Type.${if (rbSell.isChecked) "SELL" else "PAYBACK"}".trimIndent()
            val sb = StringBuffer()
            val slip = getReceipt(this@ReceiptApiActivity, if (rbSell.isChecked) Receipt.Type.SELL else Receipt.Type.PAYBACK)
            if (slip != null) {
                sb.append("===Get positions===\n")
                for (item in slip.getPositions()) {
                    sb.append(item.toString()).append("\n")
                    val list: List<ExtraKey> = ArrayList<ExtraKey>(item.extraKeys)
                    sb.append("Extra:\n").append(if (list.isNotEmpty()) list[0].description else "").append("\n")
                }
                sb.append("==Header===\n").append(slip.header.toString()).append("\n")
                sb.append("===Payments===\n")
                for (item in slip.getPayments()) {
                    sb.append(item.toString()).append("\n")
                }
                tvLog.text = sb.toString()
            } else {
                tvLog.text = "Receipt: NULL"
            }
        }
        mBarcodeBroadcastReceiver = object : BarcodeBroadcastReceiver() {
            override fun onBarcodeReceived(barcode: String, context: Context?) {
                editText.setText(barcode)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mBarcodeBroadcastReceiver)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(
            mBarcodeBroadcastReceiver,
            BarcodeBroadcastReceiver.BARCODE_INTENT_FILTER,
            BarcodeBroadcastReceiver.SENDER_PERMISSION,
            null
        )
    }
}