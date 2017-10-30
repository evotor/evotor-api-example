package ru.qualitylab.evotor.evotortest6;

import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.evotor.framework.core.IntegrationService;
import ru.evotor.framework.core.action.event.receipt.payment.PaymentSelectedEvent;
import ru.evotor.framework.core.action.event.receipt.payment.PaymentSelectedEventProcessor;
import ru.evotor.framework.core.action.event.receipt.payment.PaymentSelectedEventResult;
import ru.evotor.framework.core.action.processor.ActionProcessor;
import ru.evotor.framework.payment.PaymentPurpose;

/**
 * Разделение чека на платежи
 * В манифесте указать для сервиса <action android:name="evo.v2.receipt.sell.payment.SELECTED" />
 * Внимание! Событие возникает только если пользователь выбрал безналичный расчёт
 * Разделение на различные системы оплаты (комбинированная оплата) не поддерживается
 */
public class SplitService extends IntegrationService {
    @Nullable
    @Override
    protected Map<String, ActionProcessor> createProcessors() {
        Map<String, ActionProcessor> map = new HashMap<>();
        map.put(
                PaymentSelectedEvent.NAME_SELL_RECEIPT,
                new PaymentSelectedEventProcessor() {
                    @Override
                    public void call(@NonNull String s, @NonNull PaymentSelectedEvent
                            paymentSelectedEvent, @NonNull Callback callback) {
                        List<PaymentPurpose> paymentParts = new ArrayList<>();
                        //Идентификатор платёжной системы и счёта в ней
                        String psId = paymentSelectedEvent.getPaymentSystem().getPaymentSystemId();
                        paymentParts.add(
                                new PaymentPurpose(
                                        //Собственный идентификатор объекта PaymentPurpose, который можно использовать для сопоставления платежа и печатной группы
                                        "-1-",
                                        psId,
                                        //Сумму платежа
                                        new BigDecimal(3),
                                        "0",
                                        //Текст, который отображается пользователю при проведении данного платежа
                                        "платёж клиента 1"
                                )
                        );
                        paymentParts.add(new PaymentPurpose("-2-", psId, new BigDecimal(5), "0", "платёж клиента 2"));
                        paymentParts.add(new PaymentPurpose("-3-", psId, new BigDecimal(2), "0", "платёж клиента 3"));
                        paymentParts.add(new PaymentPurpose("-4-", psId, new BigDecimal(10), "0", "платёж клиента 4"));

                        try {
                            callback.onResult(
                                    new PaymentSelectedEventResult(
                                            //Дополнительные данные для приложения
                                            null,
                                            paymentParts
                                    ).toBundle()
                            );
                        } catch (RemoteException exc) {
                            exc.printStackTrace();
                        }
                    }
                }
        );
        return map;
    }
}
