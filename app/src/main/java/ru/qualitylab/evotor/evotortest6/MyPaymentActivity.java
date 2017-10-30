package ru.qualitylab.evotor.evotortest6;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ru.evotor.framework.core.IntegrationActivity;
import ru.evotor.framework.core.action.event.receipt.payment.system.result.PaymentSystemPaymentErrorResult;
import ru.evotor.framework.core.action.event.receipt.payment.system.result.PaymentSystemPaymentOkResult;
import ru.evotor.framework.payment.PaymentType;

public class MyPaymentActivity extends IntegrationActivity {
    public static final String EXTRA_NAME_OPERATION = "EXTRA_NAME_OPERATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_payment);

        //В случае успешной обработки события служба должна возвращать результат PaymentSystemPaymentOkResult
        findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Уникальный идентификатор платежа, который понадобится при отмене транзакции
                StringBuilder rrn = new StringBuilder();
                Random random = new Random();
                for (int i = 0; i < 10; i++)
                    rrn.append(random.nextInt(10));
                //Текст, который будет напечатан на чеке в двух экземплярах
                List<String> slip = new ArrayList<>();
                slip.add("SLIP START");
                slip.add("RRN:");
                slip.add(rrn.toString());
                slip.add("SLIP EMD");
                setIntegrationResult(new PaymentSystemPaymentOkResult(rrn.toString(), slip, "123qwe", PaymentType.ELECTRON));
                finish();
            }
        });

        //В случае ошибки служба должна возвращать результат PaymentSystemPaymentErrorResult
        findViewById(R.id.btnError).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setIntegrationResult(new PaymentSystemPaymentErrorResult("Error was happened"));
                finish();
            }
        });

        //Тип текущей операции с платежной системой
        if (getIntent().hasExtra(EXTRA_NAME_OPERATION)) {
            ((TextView) findViewById(R.id.tvOperation)).setText(getIntent().getStringExtra(EXTRA_NAME_OPERATION));
        }
    }

    @Override
    public void onBackPressed() {
        setIntegrationResult(new PaymentSystemPaymentErrorResult("onBackPressed was happened"));
        finish();
    }
}
