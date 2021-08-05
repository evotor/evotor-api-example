package ru.fhs.evotor.remonline

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ru.evotor.framework.core.IntegrationException
import ru.evotor.framework.core.IntegrationManagerFuture
import ru.evotor.framework.core.action.command.open_receipt_command.OpenSellReceiptCommand
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd
import ru.evotor.framework.navigation.NavigationApi.createIntentForSellReceiptEdit
import ru.evotor.framework.receipt.ExtraKey
import ru.evotor.framework.receipt.Position
import java.math.BigDecimal
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = applicationContext.packageName
        findViewById<View>(R.id.btnOpenReceipt).setOnClickListener { view: View? -> openReceipt() }
        findViewById<View>(R.id.btnReceiptAPI).setOnClickListener { view: View? ->
            startActivity(
                Intent(
                    this@MainActivity,
                    ReceiptApiActivity::class.java
                )
            )
        }
    }

    private fun openReceipt() {
        //Дополнительное поле для позиции. В списке наименований расположено под количеством и выделяется синим цветом
        val set: MutableSet<ExtraKey> = HashSet()
        set.add(ExtraKey(null, null, "Тест Зубочистки"))
        //Создание списка товаров чека
        val positionAddList: MutableList<PositionAdd> = ArrayList()
        positionAddList.add(
            PositionAdd(
                Position.Builder.newInstance( //UUID позиции
                    UUID.randomUUID().toString(),  //UUID товара
                    null,  //Наименование
                    "Зубочистки",  //Наименование единицы измерения
                    "кг",  //Точность единицы измерения
                    0,  //Цена без скидок
                    BigDecimal(400),  //Количество
                    BigDecimal(1) //Добавление цены с учетом скидки на позицию. Итог = price - priceWithDiscountPosition
                ).setPriceWithDiscountPosition(BigDecimal(100))
                    .setExtraKeys(set).build()
            )
        )


        //Открытие чека продажи. Передаются: список наименований, дополнительные поля для приложения
        OpenSellReceiptCommand(positionAddList, null).process(this@MainActivity) { future: IntegrationManagerFuture ->
            try {
                val result = future.result
                if (result.type == IntegrationManagerFuture.Result.Type.OK) {
                    startActivity(createIntentForSellReceiptEdit(true))
                }
            } catch (e: IntegrationException) {
                e.printStackTrace()
            }
        }
    }
}