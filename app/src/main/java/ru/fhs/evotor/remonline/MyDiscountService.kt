package ru.fhs.evotor.remonline

import android.os.RemoteException
import org.json.JSONException
import org.json.JSONObject
import ru.evotor.framework.core.IntegrationService
import ru.evotor.framework.core.action.event.receipt.changes.position.IPositionChange
import ru.evotor.framework.core.action.event.receipt.changes.position.SetExtra
import ru.evotor.framework.core.action.event.receipt.discount.ReceiptDiscountEvent
import ru.evotor.framework.core.action.event.receipt.discount.ReceiptDiscountEventProcessor
import ru.evotor.framework.core.action.event.receipt.discount.ReceiptDiscountEventResult
import ru.evotor.framework.core.action.processor.ActionProcessor
import java.math.BigDecimal
import java.util.*

/**
 * Применение скидки на весь чек продажи
 */
class MyDiscountService : IntegrationService() {
    override fun createProcessors(): Map<String, ActionProcessor>? {
        val map: MutableMap<String, ActionProcessor> = HashMap()
        map[ReceiptDiscountEvent.NAME_SELL_RECEIPT] = object : ReceiptDiscountEventProcessor() {
            override fun call(
                action: String,
                event: ReceiptDiscountEvent,
                callback: Callback
            ) {
                try {
                    //Значение скидки на весь чек в рублях или иной валюте
                    val discount = BigDecimal(10)
                    val obj = JSONObject()
                    obj.put("someSuperKey", "AWESOME DISCOUNT")
                    val extra = SetExtra(obj)

                    val listOfChanges: List<IPositionChange> = ArrayList()
                    callback.onResult(
                        ReceiptDiscountEventResult(
                            discount,
                            extra,
                            listOfChanges
                        )
                    )
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
        }
        return map
    }
}