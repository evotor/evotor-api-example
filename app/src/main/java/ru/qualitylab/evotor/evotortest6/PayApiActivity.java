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

/**
 * Платежные системы PaymentSystem API
 */
public class PayApiActivity extends IntegrationAppCompatActivity {

    TextView log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_api);

        log = (TextView) findViewById(R.id.textView);

        findViewById(R.id.btnGet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Получить список платёжных систем, а также их учётных записей, доступных на смарт-терминале
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
