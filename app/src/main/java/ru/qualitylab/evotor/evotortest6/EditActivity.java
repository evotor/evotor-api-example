package ru.qualitylab.evotor.evotortest6;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.evotor.framework.core.IntegrationAppCompatActivity;
import ru.evotor.framework.core.action.event.receipt.before_positions_edited.BeforePositionsEditedEventResult;
import ru.evotor.framework.core.action.event.receipt.changes.position.IPositionChange;
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd;
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionRemove;
import ru.evotor.framework.receipt.Measure;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;

public class EditActivity extends IntegrationAppCompatActivity {
    ListView list;
    Button btnOK, btnAdd, btnDel;
    EditText etName, etPrice, etQty;
    List<CustomEditObject> objects;
    List<IPositionChange> changes;
    CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        list = (ListView) findViewById(R.id.list);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnOK = (Button) findViewById(R.id.btnOk);
        btnDel = (Button) findViewById(R.id.btnDel);
        etName = (EditText) findViewById(R.id.etName);
        etPrice = (EditText) findViewById(R.id.etPrice);
        etQty = (EditText) findViewById(R.id.etQty);

        objects = new ArrayList<>();
        adapter = new CustomAdapter(this, R.layout.customeditrow, objects);
        list.setAdapter(adapter);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                objects.add(new CustomEditObject(UUID.randomUUID().toString(), etName.getText().toString(), etPrice.getText().toString(), etQty.getText().toString()));
            }
        });


        Receipt receipt = ReceiptApi.getReceipt(this, Receipt.Type.SELL);
        if (receipt != null) {
            for (Position item : receipt.getPositions()) {
                if(item != null){
                    objects.add(new CustomEditObject(item.getProductUuid(), item.getName(), item.getPrice().toString(), item.getQuantity().toString()));
                }
            }
            adapter.notifyDataSetChanged();
        }

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (objects != null) {
                    changes = new ArrayList<>();
                    for (CustomEditObject item : objects) {
                        changes.add(new PositionRemove(item.getUuid()));
                    }
                    setIntegrationResult(new BeforePositionsEditedEventResult(changes, null, null));
                    finish();
                }
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (objects != null) {
                    changes = new ArrayList<>();
                    for (CustomEditObject item : objects) {
                        changes.add(new PositionAdd(Position.Builder.newInstance(
                                UUID.randomUUID().toString(),
                                item.getUuid(),
                                item.getName(),
                                new Measure("шт", 0, 0),
                                new BigDecimal(item.getPrice()),
                                new BigDecimal(item.getQty())
                        ).build()));
                    }
                    setIntegrationResult(new BeforePositionsEditedEventResult(changes, null, null));
                    finish();
                }
            }
        });
    }
}
