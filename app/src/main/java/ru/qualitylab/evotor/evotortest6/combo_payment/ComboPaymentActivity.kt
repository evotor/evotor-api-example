package ru.qualitylab.evotor.evotortest6.combo_payment

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import ru.evotor.framework.component.PaymentPerformer
import ru.evotor.framework.core.IntegrationAppCompatActivity
import ru.evotor.framework.core.action.event.receipt.changes.receipt.SetExtra
import ru.evotor.framework.core.action.event.receipt.payment.combined.event.PaymentDelegatorPaybackData
import ru.evotor.framework.core.action.event.receipt.payment.combined.result.PaymentDelegatorSelectedEventResult
import ru.evotor.framework.payment.PaymentPurpose
import ru.evotor.framework.payment.PaymentSystem
import ru.evotor.framework.payment.PaymentType
import ru.evotor.framework.receipt.Receipt
import ru.evotor.framework.receipt.ReceiptApi
import ru.qualitylab.evotor.evotortest6.R
import java.math.BigDecimal
import java.util.UUID

class ComboPaymentActivity : IntegrationAppCompatActivity() {
    private var availablePaybackSums: ArrayList<PaymentDelegatorPaybackData>? = null
    private lateinit var receiptUuid: String

    private var isPayback: Boolean = false
    private var isBuyback: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_combo_payment)

        availablePaybackSums = intent.getSerializableExtra(KEY_AVAILABLE_PAYBACK_SUM) as ArrayList<PaymentDelegatorPaybackData>?

        receiptUuid = intent.getStringExtra(KEY_RECEIPT_UUID)!!

        isPayback = ReceiptApi.getReceipt(this, receiptUuid)?.header?.type == Receipt.Type.PAYBACK
        isBuyback = ReceiptApi.getReceipt(this, receiptUuid)?.header?.type == Receipt.Type.BUYBACK

        findViewById<Button>(R.id.payment_cash).setOnClickListener {
            val paymentId = UUID.randomUUID().toString()
            val paymentTotal = findViewById<EditText>(R.id.sum_input).text.toString()

            val paymentDescription = "Наличные"
            val paymentPerformer = PaymentPerformer(
                PaymentSystem(
                    PaymentType.CASH,
                    paymentDescription,
                    "ru.evotor.paymentSystem.cash.base"
                ),
                null,
                null,
                null,
                null
            )

            val valid = validatePaybackSum(BigDecimal(paymentTotal), paymentPerformer)

            if (valid) {
                onPaymentDelegatorSelected(
                    PaymentPurpose(
                        paymentId,
                        paymentPerformer.paymentSystem?.paymentSystemId,
                        paymentPerformer,
                        BigDecimal(paymentTotal),
                        null,
                        paymentDescription
                    ),
                    null
                )
            } else {
                Toast.makeText(this, "Сумма операции не должна превышать доступную сумму для возврата", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.payment_card).setOnClickListener {
            val paymentId = UUID.randomUUID().toString()
            val paymentTotal = findViewById<EditText>(R.id.sum_input).text.toString()

            val paymentDescription = "Банковская карта"
            val paymentPerformer = PaymentPerformer(
                PaymentSystem(
                    PaymentType.ELECTRON,
                    paymentDescription,
                    "ru.evotor.paymentSystem.cashless.base"
                ),
                null,
                null,
                null,
                null
            )

            val valid = validatePaybackSum(BigDecimal(paymentTotal), paymentPerformer)

            if (valid) {
                onPaymentDelegatorSelected(
                    PaymentPurpose(
                        paymentId,
                        paymentPerformer.paymentSystem?.paymentSystemId,
                        paymentPerformer,
                        BigDecimal(paymentTotal),
                        null,
                        paymentDescription
                    ),
                    null
                )
            } else {
                Toast.makeText(this, "Сумма операции не должна превышать доступную сумму для возврата", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onPaymentDelegatorSelected(paymentPurpose: PaymentPurpose, extra: SetExtra?) {
        setIntegrationResult(PaymentDelegatorSelectedEventResult(paymentPurpose, extra))
        finish()
    }

    private fun validatePaybackSum(sum: BigDecimal, performer: PaymentPerformer): Boolean {
        if (availablePaybackSums != null && availablePaybackSums!!.isNotEmpty() && (isPayback || isBuyback)) {
            val receipt = ReceiptApi.getReceipt(this, receiptUuid)
            val paybackSums = receipt?.getPayments()?.associate { payment ->
                payment.paymentPerformer.paymentSystem?.paymentSystemId to payment.value
            }
                ?: emptyMap()

            val availablePerformer = availablePaybackSums!!.firstOrNull {
                it.performer?.paymentSystem?.paymentSystemId == performer.paymentSystem?.paymentSystemId
            }

            availablePerformer?.let { p ->
                val availableSum = if (paybackSums.containsKey(p.performer?.paymentSystem?.paymentSystemId)) {
                    p.sum?.minus(paybackSums[p.performer?.paymentSystem?.paymentSystemId]!!)
                } else {
                    p.sum
                }

                return sum <= availableSum
            }

            return false
        }

        return true
    }

    companion object {
        const val KEY_RECEIPT_UUID = "receiptUuid"
        const val KEY_AVAILABLE_PAYBACK_SUM = "availablePaybackSum"
    }
}