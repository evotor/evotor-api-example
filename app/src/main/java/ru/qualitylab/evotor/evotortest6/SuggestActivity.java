package ru.qualitylab.evotor.evotortest6;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import ru.evotor.framework.core.IntegrationAppCompatActivity;
import ru.evotor.framework.core.action.event.receipt.before_positions_edited.BeforePositionsEditedEventResult;
import ru.evotor.framework.core.action.event.receipt.changes.position.IPositionChange;
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd;
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionEdit;
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionRemove;
import ru.evotor.framework.core.action.event.receipt.changes.position.SetExtra;
import ru.evotor.framework.inventory.InventoryApi;
import ru.evotor.framework.inventory.ProductItem;
import ru.evotor.framework.receipt.ExtraKey;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;

public class SuggestActivity extends IntegrationAppCompatActivity {

    //Позиция товара переданная сервисом
//    Position foundPosition;
    String uuidCoffee, productUuid;
    EditText qty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest);

        qty = (EditText) findViewById(R.id.etQty);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
//            foundPosition = extras.getParcelable("foundPosition");
            uuidCoffee = extras.getString("uuidCoffee");
            productUuid = extras.getString("prodUuid");
        }


        findViewById(R.id.btnReceiptAPI).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SuggestActivity.this, ReceiptApiActivity.class));
            }
        });

        //Удалим переданную позицию товара
        findViewById(R.id.btnDel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<IPositionChange> changes = new ArrayList<>();
                changes.add(new PositionRemove(uuidCoffee));
                setIntegrationResult(new BeforePositionsEditedEventResult(changes, null));
                finish();
            }
        });


        findViewById(R.id.btnEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String prodUuid = "6c1ca1d9-9f38-42ee-aa63-c4fb046d6a95"; // приправа магги...
                String position = null;
                //Найдем товар в чеке по его UUID
                Receipt slip = ReceiptApi.getReceipt(SuggestActivity.this, Receipt.Type.SELL);
                if (slip != null) {
                    for (Position item : slip.getPositions()) {
                        if (item.getProductUuid().equals(prodUuid)) {
                            position = item.getUuid();
                            Log.e("", "Product UUID: " + prodUuid + " Position UUID: " + position);
                        }
                    }
                }

                if (position != null) {
                    //Изменим количество товара в чеке
                    List<IPositionChange> changes = new ArrayList<>();
                    ProductItem.Product item = (ProductItem.Product) InventoryApi.getProductByUuid(SuggestActivity.this, prodUuid);
                    changes.add(new PositionEdit(
                            Position.Builder.newInstance(
                                    position,
                                    item.getUuid(),
                                    item.getName(),
                                    item.getMeasureName(),
                                    item.getMeasurePrecision(),
                                    item.getPrice(),
                                    new BigDecimal(qty.getText().toString())
                            ).build()
                    ));
                    Set<ExtraKey> set = new HashSet<>();
                    set.add(new ExtraKey(null, null, "Тест EDIT"));
                    JSONObject object = new JSONObject();
                    try {
                        object.put("someSuperKey", "AWESOME EDIT");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    SetExtra extra = new SetExtra(object);
                    setIntegrationResult(new BeforePositionsEditedEventResult(changes, extra));
                }
                finish();
            }
        });

        //Добавим новый товар к чеку
        findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<IPositionChange> changes = new ArrayList<>();
                Position pos = Position.Builder.newInstance(
                        UUID.randomUUID().toString(),
                        null,
                        "Зажигалка",
                        "шт",
                        0,
                        new BigDecimal(30),
                        new BigDecimal(qty.getText().toString())
                ).build();
                Set<ExtraKey> set = new HashSet<ExtraKey>();
                set.add(new ExtraKey(null, null, "Тест ADD"));
                pos = Position.Builder.copyFrom(pos).setPriceWithDiscountPosition(new BigDecimal(27)).setExtraKeys(set).build();
                changes.add(new PositionAdd(pos));
                JSONObject object = new JSONObject();
                try {
                    object.put("someSuperKey", "AWESOME ADD");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SetExtra extra = new SetExtra(object);
                setIntegrationResult(new BeforePositionsEditedEventResult(changes, extra));
                finish();
            }
        });

        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
