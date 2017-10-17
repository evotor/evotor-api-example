package ru.qualitylab.evotor.evotortest6;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class InventoryApiActivity extends IntegrationAppCompatActivity {

    EditText ed;
    TextView log;
    Button btnGetAllBarcodesForProduct, btnSearch, btnGetFields, btnGetValueForField;
    BarcodeBroadcastReceiver mBarcodeBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_api);
        ed = (EditText) findViewById(R.id.editText2);
        log = (TextView) findViewById(R.id.tvLog);
        btnGetAllBarcodesForProduct = (Button) findViewById(R.id.btnGetAllBarcodesForProduct);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnGetFields = (Button) findViewById(R.id.btnGetFields);
        btnGetValueForField = (Button) findViewById(R.id.btnGetValueForField);

        btnGetAllBarcodesForProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log.setText("Получить все штрихкоды товара\n");
                StringBuffer sb = new StringBuffer();
                List<String> list = InventoryApi.getAllBarcodesForProduct(InventoryApiActivity.this, ed.getText().toString());
                for (String item : list) {
                    sb.append(item).append("\n");
                }
                log.setText(sb.toString());
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log.setText("Поиск товара по идентификатору\n");
                StringBuffer sb = new StringBuffer();
                ProductItem.Product prod = (ProductItem.Product) InventoryApi.getProductByUuid(InventoryApiActivity.this, ed.getText().toString());
                sb.append("Code: ").append(prod.getCode()).
                        append("\nName: ").append(prod.getName()).
                        append("\nUUID: ").append(prod.getUuid());
                sb.append("===\n").append("Description: ").append(prod.getDescription()).
                        append("\nMeasureName: ").append(prod.getMeasureName()).
                        append("\nPrice: ").append(prod.getPrice().toString()).
                        append("\nQuantity: ").append(prod.getQuantity()).
                        append("\nType: ").append(prod.getType().toString());
                log.setText(sb.toString());
            }
        });

        btnGetFields.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log.setText("Получить возможные дополнительные поля\n");
                StringBuffer sb = new StringBuffer();//1eaca77f-1579-5078-a726-83c892348ghj
                Field field = InventoryApi.getField(InventoryApiActivity.this, "70fa8b2c-ba1f-4a81-a17e-8f48bfca19ec");//48cdae58-1941-499e-942b-9aa281ff5b96
                if (field != null)
                    log.setText(field.toString());
            }
        });

        btnGetValueForField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log.setText("Получить значения дополнительных полей товара\n");
                StringBuffer sb = new StringBuffer();
                List<ProductExtra> list = InventoryApi.getProductExtras(InventoryApiActivity.this, ed.getText().toString());
                for (ProductExtra item : list) {
                    sb.append(item.toString()).append("\n");
                }
                log.setText(sb.toString());
            }
        });

        mBarcodeBroadcastReceiver = new BarcodeBroadcastReceiver() {
            @Override
            public void onBarcodeReceived(String barcode, Context context) {
                List<Position> list = ReceiptApi.getPositionsByBarcode(InventoryApiActivity.this, barcode);
                if (list.size() > 0) {
                    ed.setText(list.get(0).getProductUuid());
                }
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBarcodeBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBarcodeBroadcastReceiver, BarcodeBroadcastReceiver.BARCODE_INTENT_FILTER, BarcodeBroadcastReceiver.SENDER_PERMISSION, null);
    }
}
