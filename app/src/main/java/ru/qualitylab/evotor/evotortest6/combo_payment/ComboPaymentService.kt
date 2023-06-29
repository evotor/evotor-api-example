package ru.qualitylab.evotor.evotortest6.combo_payment

import android.content.Intent
import ru.evotor.framework.component.PaymentPerformer
import ru.evotor.framework.core.IntegrationService
import ru.evotor.framework.core.action.event.receipt.payment.combined.PaybackPaymentDelegatorEventProcessor
import ru.evotor.framework.core.action.event.receipt.payment.combined.PaymentDelegatorEventProcessor
import ru.evotor.framework.core.action.event.receipt.payment.combined.event.PaybackPaymentDelegatorEvent
import ru.evotor.framework.core.action.event.receipt.payment.combined.event.PaymentDelegatorEvent
import ru.evotor.framework.core.action.processor.ActionProcessor
import ru.evotor.framework.payment.PaymentSystem
import ru.evotor.framework.payment.PaymentType

class ComboPaymentService : IntegrationService() {
    override fun createProcessors(): MutableMap<String, ActionProcessor>? = mutableMapOf(
        Pair(
            PaymentDelegatorEvent.NAME_ACTION,
            object : PaymentDelegatorEventProcessor() {
                override fun call(action: String, event: PaymentDelegatorEvent, callback: Callback) {
                    callback.startActivity(
                        Intent(this@ComboPaymentService, ComboPaymentActivity::class.java)
                            .putExtra(ComboPaymentActivity.KEY_RECEIPT_UUID, event.receiptUuid)
                    )
                }
            }
        ),

        Pair(
            PaybackPaymentDelegatorEvent.NAME_ACTION,
            object : PaybackPaymentDelegatorEventProcessor() {
                override fun call(
                    action: String,
                    event: PaybackPaymentDelegatorEvent,
                    callback: Callback
                ) {
                    callback.startActivity(
                        Intent(this@ComboPaymentService, ComboPaymentActivity::class.java)
                            .putExtra(ComboPaymentActivity.KEY_RECEIPT_UUID, event.receiptUuid)
                            .putExtra(ComboPaymentActivity.KEY_AVAILABLE_PAYBACK_SUM, event.availablePaybackSum)
                    )
                }
            }
        )
    )
}