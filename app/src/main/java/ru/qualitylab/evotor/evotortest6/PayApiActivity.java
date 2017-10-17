package ru.qualitylab.evotor.evotortest6;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import kotlin.Pair;
import ru.evotor.framework.core.IntegrationAppCompatActivity;
import ru.evotor.framework.payment.PaymentAccount;
import ru.evotor.framework.payment.PaymentSystem;
import ru.evotor.framework.payment.PaymentSystemApi;

public class PayApiActivity extends IntegrationAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_api);

        Button getInfo = (Button) findViewById(R.id.btnGet);
        final TextView log = (TextView) findViewById(R.id.textView);

        getInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Pair<PaymentSystem, List<PaymentAccount>>> data = PaymentSystemApi.getPaymentSystems(PayApiActivity.this);
                StringBuffer sb = new StringBuffer();
                for (Pair item : data) {
                    sb.append(((PaymentSystem) item.component1()).toString());
                    sb.append("===PaymentAccount===");
                    for (PaymentAccount acc : (List<PaymentAccount>) item.component2()) {
                        sb.append("AccountId:").append(acc.getAccountId()).append("\n");
                        sb.append("UserDescription:").append(acc.getUserDescription());
                    }
                }
                log.setText(sb.toString());
            }
        });
    }
}
