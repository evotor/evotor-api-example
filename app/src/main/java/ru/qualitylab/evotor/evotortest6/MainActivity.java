package ru.qualitylab.evotor.evotortest6;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import ru.evotor.framework.core.IntegrationAppCompatActivity;
import ru.evotor.framework.core.IntegrationException;
import ru.evotor.framework.core.IntegrationManagerCallback;
import ru.evotor.framework.core.IntegrationManagerFuture;
import ru.evotor.framework.core.action.command.open_receipt_command.OpenPaybackReceiptCommand;
import ru.evotor.framework.core.action.command.open_receipt_command.OpenSellReceiptCommand;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintReceiptCommandResult;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintSellReceiptCommand;
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd;
import ru.evotor.framework.core.action.event.receipt.changes.position.SetExtra;
import ru.evotor.framework.navigation.NavigationApi;
import ru.evotor.framework.payment.PaymentSystem;
import ru.evotor.framework.payment.PaymentType;
import ru.evotor.framework.receipt.ExtraKey;
import ru.evotor.framework.receipt.Payment;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.PrintGroup;
import ru.evotor.framework.receipt.Receipt;

public class MainActivity extends IntegrationAppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(getApplicationContext().getPackageName());

        findViewById(R.id.btnOpenReceipt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReceipt();
            }
        });
        findViewById(R.id.btnOpenPayback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPayback();
            }
        });
        findViewById(R.id.btnPrefs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(NavigationApi.createIntentForCashReceiptSettings());
            }
        });
        findViewById(R.id.btnPrefsC).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(NavigationApi.createIntentForCashRegisterReport());
            }
        });
        findViewById(R.id.btnUserApi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UserApiActivity.class));
            }
        });
        findViewById(R.id.btnPayAPI).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PayApiActivity.class));
            }
        });
        findViewById(R.id.btnReceiptAPI).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ReceiptApiActivity.class));
            }
        });
        findViewById(R.id.btnOpenAndEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReceiptAndEmail();
            }
        });
        findViewById(R.id.btnPrint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PrintActivity.class));
            }
        });
        findViewById(R.id.btnInventoryApi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, InventoryApiActivity.class));
            }
        });

    }

    /*
    Пример создания и отправки электронного чека
    на email или телефон в виде СМС
    Номер телефона указывать в формате 79011234567
    */
    public void openReceiptAndEmail() {
        //Создание списка товаров чека
        List<Position> list = new ArrayList<>();
        list.add(
                Position.Builder.newInstance(
                        //UUID позиции
                        UUID.randomUUID().toString(),
                        //UUID товара
                        null,
                        //Наименование
                        "1234",
                        //Наименование единицы измерения
                        "12",
                        //Точность единицы измерения
                        0,
                        //Цена без скидок
                        new BigDecimal(1000),
                        //Количество
                        BigDecimal.TEN
                ).build()
        );
        list.add(
                Position.Builder.newInstance(
                        UUID.randomUUID().toString(),
                        null,
                        "1234",
                        "12",
                        0,
                        new BigDecimal(500),
                        BigDecimal.ONE)
                        //Добавление цены с учетом скидки на позицию. Итог = price - priceWithDiscountPosition
                        .setPriceWithDiscountPosition(new BigDecimal(300)).build()
        );
        //Способ оплаты
        HashMap payments = new HashMap<Payment, BigDecimal>();
        payments.put(new Payment(
                UUID.randomUUID().toString(),
                new BigDecimal(9300),
                //PaymentType задает тип оплаты
                new PaymentSystem(PaymentType.ELECTRON, "Internet", "12424"),
                null,
                null,
                null,
                null
        ), new BigDecimal(9300));
        PrintGroup printGroup = new PrintGroup(UUID.randomUUID().toString(),
                PrintGroup.Type.CASH_RECEIPT, null, null, null, null, false);
        Receipt.PrintReceipt printReceipt = new Receipt.PrintReceipt(
                printGroup,
                list,
                payments,
                new HashMap<Payment, BigDecimal>(), new HashMap<String, BigDecimal>()
        );

        ArrayList<Receipt.PrintReceipt> listDocs = new ArrayList<>();
        listDocs.add(printReceipt);
        //Добавление скидки на чек
        BigDecimal receiptDiscount = new BigDecimal(1000);
        new PrintSellReceiptCommand(listDocs, null, "79011234567", "example@example.com", receiptDiscount).process(MainActivity.this, new IntegrationManagerCallback() {
            @Override
            public void run(IntegrationManagerFuture integrationManagerFuture) {
                try {
                    IntegrationManagerFuture.Result result = integrationManagerFuture.getResult();
                    switch (result.getType()) {
                        case OK:
                            PrintReceiptCommandResult printSellReceiptResult = PrintReceiptCommandResult.create(result.getData());
                            Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_LONG).show();
                            break;
                        case ERROR:
                            Toast.makeText(MainActivity.this, result.getError().getMessage(), Toast.LENGTH_LONG).show();
                            break;
                    }
                } catch (IntegrationException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void openReceipt() {
        //Дополнительное поле для позиции. В списке наименований расположено под количеством и выделяется синим цветом
        Set<ExtraKey> set = new HashSet<>();
        set.add(new ExtraKey(null, null, "Тест Зубочистки"));
        //Создание списка товаров чека
        List<PositionAdd> positionAddList = new ArrayList<>();
        positionAddList.add(
                new PositionAdd(
                        Position.Builder.newInstance(
                                //UUID позиции
                                UUID.randomUUID().toString(),
                                //UUID товара
                                null,
                                //Наименование
                                "Зубочистки",
                                //Наименование единицы измерения
                                "кг",
                                //Точность единицы измерения
                                0,
                                //Цена без скидок
                                new BigDecimal(200),
                                //Количество
                                new BigDecimal(1)
                                //Добавление цены с учетом скидки на позицию. Итог = price - priceWithDiscountPosition
                        ).setPriceWithDiscountPosition(new BigDecimal(100))
                                .setExtraKeys(set).build()
                )
        );

        //Дополнительные поля в чеке для использования в приложении
        JSONObject object = new JSONObject();
        try {
            object.put("someSuperKey", "AWESOME RECEIPT OPEN");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SetExtra extra = new SetExtra(object);

        //Открытие чека продажи. Передаются: список наименований, дополнительные поля для приложения
        new OpenSellReceiptCommand(positionAddList, extra).process(MainActivity.this, new IntegrationManagerCallback() {
            @Override
            public void run(IntegrationManagerFuture future) {
                try {
                    IntegrationManagerFuture.Result result = future.getResult();
                    if (result.getType() == IntegrationManagerFuture.Result.Type.OK) {
                        startActivity(new Intent("evotor.intent.action.payment.SELL"));
                    }
                } catch (IntegrationException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //Открытие чека возврата
    public void openPayback() {
        //Добавление позиций возврата
        List<PositionAdd> positionAddList = new ArrayList<>();
        Set<ExtraKey> set = new HashSet<>();
        set.add(new ExtraKey(null, null, "Тест Возврат Зубочистки"));
        positionAddList.add(
                new PositionAdd(
                        Position.Builder.newInstance(
                                UUID.randomUUID().toString(),
                                null,
                                "Зубочистки",
                                "кг",
                                0,
                                new BigDecimal(200),
                                new BigDecimal(1)
                        ).setExtraKeys(set).build()));
        JSONObject object = new JSONObject();
        try {
            object.put("someSuperKey", "AWESOME PAYBACK OPEN");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SetExtra extra = new SetExtra(object);
        //Открытие чека возврата. Передаются: список наименований, дополнительные поля для приложения
        new OpenPaybackReceiptCommand(positionAddList, extra).process(MainActivity.this, new IntegrationManagerCallback() {
            @Override
            public void run(IntegrationManagerFuture future) {
                try {
                    IntegrationManagerFuture.Result result = future.getResult();
                    if (result.getType() == IntegrationManagerFuture.Result.Type.OK) {
                        startActivity(new Intent("evotor.intent.action.payment.PAYBACK"));
                    }
                } catch (IntegrationException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
