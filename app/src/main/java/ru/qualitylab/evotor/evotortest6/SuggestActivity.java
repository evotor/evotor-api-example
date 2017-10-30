package ru.qualitylab.evotor.evotortest6;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    String uuidCoffee = "", prodUuidCoffee = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest);


        Button add = (Button) findViewById(R.id.btnAdd);
        Button del = (Button) findViewById(R.id.btnDel);
        Button cancel = (Button) findViewById(R.id.btnCancel);
        Button edit = (Button) findViewById(R.id.btnEdit);
        Button btnReceiptApi = (Button) findViewById(R.id.btnReceiptAPI);
        final EditText qty = (EditText) findViewById(R.id.etQty);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            uuidCoffee = extras.getString("uuidCoffee");
            prodUuidCoffee = extras.getString("prodUuidCoffee");
        }


        btnReceiptApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SuggestActivity.this, ReceiptApiActivity.class));
            }
        });

        final String finalUuidCoffee = uuidCoffee;
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<IPositionChange> changes = new ArrayList<>();
                changes.add(new PositionRemove(finalUuidCoffee));
                setIntegrationResult(new BeforePositionsEditedEventResult(changes, null));
                finish();
            }
        });


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String prod = "6c1ca1d9-9f38-42ee-aa63-c4fb046d6a95"; // приправа магги...
                String position = "";
                Receipt slip = ReceiptApi.getReceipt(SuggestActivity.this, Receipt.Type.SELL);
                if (slip != null) {
                    for (Position item : slip.getPositions()) {
                        if (item.getProductUuid().equals(prod)) {
                            position = item.getUuid();
                            Log.e("", "Product UUID: " + prod + " Position UUID: " + position);
                        }
                    }
                }

                List<IPositionChange> changes = new ArrayList<>();
                ProductItem.Product item = (ProductItem.Product) InventoryApi.getProductByUuid(SuggestActivity.this, prod);
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
                finish();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
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

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
