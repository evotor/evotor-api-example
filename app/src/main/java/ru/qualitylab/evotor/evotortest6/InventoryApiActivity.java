package ru.qualitylab.evotor.evotortest6;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import ru.evotor.framework.core.IntegrationAppCompatActivity;
import ru.evotor.framework.inventory.InventoryApi;
import ru.evotor.framework.inventory.ProductExtra;
import ru.evotor.framework.inventory.ProductItem;
import ru.evotor.framework.inventory.field.Field;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.ReceiptApi;
import ru.evotor.integrations.BarcodeBroadcastReceiver;

/**
 * Рассмотрена работа со сканером штрихкода
 * Получение штрихкодов товара по его уникальному номеру
 * Поиск товара по уникальному идентификатору
 * Получить возможные дополнительные поля товара
 * Получить значения ProductExtra дополнительных полей товара
 */
public class InventoryApiActivity extends IntegrationAppCompatActivity {

    EditText etSearch;
    TextView tvLog;
    BarcodeBroadcastReceiver mBarcodeBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_api);

        etSearch = (EditText) findViewById(R.id.etSearch);
        tvLog = (TextView) findViewById(R.id.tvLog);

        findViewById(R.id.btnGetAllBarcodesForProduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Получение всех штрихкодов для выбранного товара
                //getAllBarcodesForProduct(Context, ProductUUIDString)
                tvLog.setText("Получить все штрихкоды товара\n");
                StringBuffer sb = new StringBuffer();
                List<String> list = InventoryApi.getAllBarcodesForProduct(InventoryApiActivity.this, etSearch.getText().toString());
                for (String item : list) {
                    sb.append(item).append("\n");
                }
                tvLog.setText(sb.toString());
            }
        });

        findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Поиск товара по UUID
                //getProductByUuid(Context, ProductUUIDString)
                tvLog.setText("Поиск товара по идентификатору\n");
                StringBuffer sb = new StringBuffer();
                ProductItem.Product prod = (ProductItem.Product) InventoryApi.getProductByUuid(InventoryApiActivity.this, etSearch.getText().toString());
                if (prod != null) {
                    sb.append("Code: ")
                            .append(prod.getCode())
                            .append("\nName: ")
                            .append(prod.getName())
                            .append("\nUUID: ")
                            .append(prod.getUuid());
                    sb.append("===\n")
                            .append("Description: ")
                            .append(prod.getDescription())
                            .append("\nMeasureName: ")
                            .append(prod.getMeasure().getName())
                            .append("\nPrice: ")
                            .append(prod.getPrice().toString())
                            .append("\nQuantity: ")
                            .append(prod.getQuantity())
                            .append("\nType: ")
                            .append(prod.getType().toString());
                } else {
                    sb.append("Not found");
                }
                tvLog.setText(sb.toString());
            }
        });

        findViewById(R.id.btnGetFields).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvLog.setText("Получить возможные дополнительные поля\n");
                StringBuffer sb = new StringBuffer();
                Field field = InventoryApi.getField(InventoryApiActivity.this, "70fa8b2c-ba1f-4a81-a17e-8f48bfca19ec");
                if (field != null)
                    tvLog.setText(field.toString());
            }
        });

        findViewById(R.id.btnGetValueForField).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvLog.setText("Получить значения дополнительных полей товара\n");
                StringBuffer sb = new StringBuffer();
                List<ProductExtra> list = InventoryApi.getProductExtras(InventoryApiActivity.this, etSearch.getText().toString());
                for (ProductExtra item : list) {
                    sb.append(item.toString()).append("\n");
                }
                tvLog.setText(sb.toString());
            }
        });

        //Получение штрихкода со сканера
        mBarcodeBroadcastReceiver = new BarcodeBroadcastReceiver() {
            @Override
            public void onBarcodeReceived(String barcode, Context context) {
                //Поиск товара по штрихкоду
                List<Position> list = ReceiptApi.getPositionsByBarcode(InventoryApiActivity.this, barcode);
                if (list.size() > 0) {
                    etSearch.setText(list.get(0).getProductUuid());
                }
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Останавливаем подписку для событий сканера
        unregisterReceiver(mBarcodeBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Регистрируем подписку на события сканера
        registerReceiver(mBarcodeBroadcastReceiver, BarcodeBroadcastReceiver.BARCODE_INTENT_FILTER, BarcodeBroadcastReceiver.SENDER_PERMISSION, null);
    }
}
