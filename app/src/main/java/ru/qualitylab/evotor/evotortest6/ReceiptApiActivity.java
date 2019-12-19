package ru.qualitylab.evotor.evotortest6;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.evotor.framework.core.IntegrationAppCompatActivity;
import ru.evotor.framework.receipt.ExtraKey;
import ru.evotor.framework.receipt.Payment;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;
import ru.evotor.framework.receipt.ReceiptHeaderTable;
import ru.evotor.integrations.BarcodeBroadcastReceiver;
import ru.evotor.query.Cursor;

/**
 * Пример работы с ReceiptAPI
 */
public class ReceiptApiActivity extends IntegrationAppCompatActivity {

    RadioButton rbSell, rbPayback;
    TextView tvLog;
    EditText ed;
    BarcodeBroadcastReceiver mBarcodeBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_api);

        rbSell = (RadioButton) findViewById(R.id.rbSell);
        rbPayback = (RadioButton) findViewById(R.id.rbPayback);
        tvLog = (TextView) findViewById(R.id.tvLog);
        ed = (EditText) findViewById(R.id.editText);

        //Получение заголовков открытого чека
        findViewById(R.id.btnGetHeaders).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvLog.setText("Headers type:" + (rbSell.isChecked() ? "SELL" : "PAYBACK") + "\n");
                StringBuffer sb = new StringBuffer();
                Cursor<Receipt.Header> cursor = ReceiptApi.getReceiptHeaders(ReceiptApiActivity.this, rbSell.isChecked() ? Receipt.Type.SELL : Receipt.Type.PAYBACK);
                try {
                    if (cursor != null) {
                        cursor.moveToFirst();
                        while (cursor.moveToNext()) {
                            sb.append("NUMBER: ").append(cursor.getString(cursor.getColumnIndex(ReceiptHeaderTable.COLUMN_NUMBER))).append("\n");
                            sb.append("UUID: ").append(cursor.getString(cursor.getColumnIndex(ReceiptHeaderTable.COLUMN_UUID))).append("\n");
                            sb.append("CLIENT_EMAIL: ").append(cursor.getString(cursor.getColumnIndex(ReceiptHeaderTable.COLUMN_CLIENT_EMAIL))).append("\n");
                            sb.append("CLIENT_PHONE: ").append(cursor.getString(cursor.getColumnIndex(ReceiptHeaderTable.COLUMN_CLIENT_PHONE))).append("\n");
                            sb.append("DATE: ").append(cursor.getString(cursor.getColumnIndex(ReceiptHeaderTable.COLUMN_DATE))).append("\n");
                            //sb.append("EXTRA: ").append(cursor.getString(cursor.getColumnIndex(ReceiptHeaderTable.COLUMN_EXTRA))).append("\n");
                            sb.append("TYPE: ").append(cursor.getString(cursor.getColumnIndex(ReceiptHeaderTable.COLUMN_TYPE))).append("\n===\n");
                        }
                        tvLog.setText(sb.toString());
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        });

        //Поиск товаров в чеке по штрихкоду
        findViewById(R.id.btnReceiptBarcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvLog.setText("Receipt by Barcode\n");
                StringBuffer sb = new StringBuffer();
                List<Position> list = ReceiptApi.getPositionsByBarcode(ReceiptApiActivity.this, ed.getText().toString());
                if (list != null) {
                    sb.append("===Get positions===\n");
                    for (Position item : list) {
                        sb.append(item.toString()).append("\n");
                    }
                    tvLog.setText(sb.toString());
                } else {
                    tvLog.setText("Receipt: NULL");
                }
            }
        });

        //Получение чека по его UUID
        findViewById(R.id.btnReceiptById).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvLog.setText("Receipt by UUID\n");
                StringBuffer sb = new StringBuffer();
                Receipt slip = ReceiptApi.getReceipt(ReceiptApiActivity.this, ed.getText().toString());
                if (slip != null) {
                    sb.append("===Get positions===\n");
                    for (Position item : slip.getPositions()) {
                        sb.append(item.toString()).append("\n");
                    }
                    sb.append("==Header===\n").append(slip.getHeader().toString()).append("\n");
                    sb.append("===Payments===\n");
                    for (Payment item : slip.getPayments()) {
                        sb.append(item.toString()).append("\n");
                    }
                    tvLog.setText(sb.toString());
                } else {
                    tvLog.setText("Receipt: NULL");
                }
            }
        });

        //Получение открытого чека
        findViewById(R.id.btnGetReceiptOpen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvLog.setText("Receipt.Type." + (rbSell.isChecked() ? "SELL" : "PAYBACK") + "\n");
                StringBuffer sb = new StringBuffer();
                Receipt slip = ReceiptApi.getReceipt(ReceiptApiActivity.this, rbSell.isChecked() ? Receipt.Type.SELL : Receipt.Type.PAYBACK);
                if (slip != null) {
                    sb.append("===Get positions===\n");
                    for (Position item : slip.getPositions()) {
                        sb.append(item.toString()).append("\n");
                        List<ExtraKey> list = new ArrayList<>(item.getExtraKeys());
                        sb.append("Extra:\n").append((list.size() > 0 ? list.get(0).getDescription() : "")).append("\n");
                    }
                    sb.append("==Header===\n").append(slip.getHeader().toString()).append("\n");
                    sb.append("===Payments===\n");
                    for (Payment item : slip.getPayments()) {
                        sb.append(item.toString()).append("\n");
                    }
                    tvLog.setText(sb.toString());
                } else {
                    tvLog.setText("Receipt: NULL");
                }
            }
        });

        mBarcodeBroadcastReceiver = new BarcodeBroadcastReceiver() {
            @Override
            public void onBarcodeReceived(String barcode, Context context) {
                ed.setText(barcode);
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
