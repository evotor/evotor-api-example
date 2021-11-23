package ru.fhs.evotor.remonline

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.input
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_input_code.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.evotor.framework.core.IntegrationException
import ru.evotor.framework.core.IntegrationManagerFuture
import ru.evotor.framework.core.action.command.open_receipt_command.OpenSellReceiptCommand
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd
import ru.evotor.framework.navigation.NavigationApi.createIntentForSellReceiptEdit
import ru.evotor.framework.receipt.ExtraKey
import ru.evotor.framework.receipt.Position
import ru.evotor.framework.receipt.Receipt
import ru.fhs.evotor.remonline.adapter.ReceiptItem
import ru.fhs.evotor.remonline.model.ReceiptModel
import ru.fhs.evotor.remonline.network.ApiHelper
import ru.fhs.evotor.remonline.network.RetrofitBuilder
import ru.fhs.evotor.remonline.network.data.request.InitDeviceRequest
import java.math.BigDecimal
import java.util.*

class MainActivity : AppCompatActivity(), ReceiptItem.OnReceiptEventListener {
    private val adapter = GroupAdapter<GroupieViewHolder>()

    private val apiService by lazy {
        ApiHelper(this, RetrofitBuilder.apiService)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = getString(R.string.app_name)

        setListeners()
        initApi()
    }

    private fun setListeners() {
        rvReceipts.layoutManager = LinearLayoutManager(this)
        rvReceipts.adapter = adapter

        swipeRefreshLayout.setOnRefreshListener {
            loadReceipts()
        }
    }

    private fun initApi() {
        if (apiService.hasApiCode) {
            loadReceipts()
        } else {
            showInputCodeDialog()
        }
    }

    @SuppressLint("CheckResult")
    private fun showInputCodeDialog() {
        val dialog = MaterialDialog(this).show {
            customView(R.layout.dialog_input_code, noVerticalPadding = true)
            cancelOnTouchOutside(false)
        }
        val view = dialog.getCustomView()
        val etCode = view.etCode
        val btDone = view.btDone
        val vLoading = view.vLoading

        etCode.doAfterTextChanged {
            btDone.isEnabled = it?.toString().orEmpty().toIntOrNull() != null
        }

        btDone.setOnClickListener {
            val code = etCode.text?.toString().orEmpty().toIntOrNull() ?: 0

            lifecycleScope.launch {
                vLoading.visibility = View.VISIBLE
                val result = withContext(Dispatchers.IO) { initDevice(code) }
                vLoading.visibility = View.GONE

                if (result) {
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    private suspend fun initDevice(code: Int): Boolean {
        val result = apiService.initDevice(InitDeviceRequest(code))
        if (result.isSuccess) {
            apiService.saveApiKey(result.unwrappedData.code)
            loadReceipts()
        } else {
            showError(result.error)
        }
        return result.isSuccess
    }

    private fun loadReceipts() {
        lifecycleScope.launch(Dispatchers.Main) {
            swipeRefreshLayout.isRefreshing = true
            val result = withContext(Dispatchers.IO) { apiService.getReceipts() }

            if (result.isSuccess) {
                adapter.clear()
                adapter.addAll(result.unwrappedData.map { ReceiptItem(it, this@MainActivity) })
            } else {
                showError(result.error)
            }

            swipeRefreshLayout.isRefreshing = false
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

    private fun showError(text: String?) {
        text?.let {
            lifecycleScope.launch(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onReceiptClick(receipt: ReceiptModel) {

    }
}