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

public class SplitService extends IntegrationService {
    @Nullable
    @Override
    protected Map<String, ActionProcessor> createProcessors() {
        Map<String, ActionProcessor> map = new HashMap<>();
        /*map.put(
                PrintGroupRequiredEvent.NAME_SELL_RECEIPT,
                new PrintGroupRequiredEventProcessor() {
                    @Override
                    public void call(@NonNull String s, @NonNull PrintGroupRequiredEvent printGroupRequiredEvent, @NonNull Callback callback) {
                        List<SetPrintGroup> setPrintGroups = new ArrayList<>();
                        PrintGroup printGroup = new PrintGroup(
                                UUID.randomUUID().toString(),
                                PrintGroup.Type.INVOICE,
                                "ООО \"Пример\"",
                                "012345678901",
                                "г. Москва",
                                TaxationSystem.PATENT,
                                true
                        );
                        List<String> paymentPurposeIds = new ArrayList<>();
                        paymentPurposeIds.add("-1-");
                        paymentPurposeIds.add("-3-");
                        List<String> positionUuids = new ArrayList<>();
                        Receipt a = ReceiptApi.getReceipt(getApplicationContext(), Receipt.Type.SELL);
                        if (a != null) {
                            positionUuids.add(a.getPositions().get(0).getUuid());
                        }
                        setPrintGroups.add(new SetPrintGroup(
                                printGroup,
                                paymentPurposeIds,
                                positionUuids
                        ));
                        try {
                            callback.onResult(
                                    new PrintGroupRequiredEventResult(
                                            null,
                                            setPrintGroups
                                    )
                            );
                        } catch (RemoteException exc) {
                            exc.printStackTrace();
                        }
                    }
                }
        );*/
        map.put(
                PaymentSelectedEvent.NAME_SELL_RECEIPT,
                new PaymentSelectedEventProcessor() {
                    @Override
                    public void call(@NonNull String s, @NonNull PaymentSelectedEvent
                            paymentSelectedEvent, @NonNull Callback callback) {
                        List<PaymentPurpose> paymentParts = new ArrayList<>();
                        String psId = paymentSelectedEvent.getPaymentSystem().getPaymentSystemId();
                        paymentParts.add(new PaymentPurpose("-1-", psId, new BigDecimal(3), "0", "платёж клиента 1"));
                        paymentParts.add(new PaymentPurpose("-2-", psId, new BigDecimal(5), "0", "платёж клиента 2"));
                        paymentParts.add(new PaymentPurpose("-3-", psId, new BigDecimal(2), "0", "платёж клиента 3"));
                        paymentParts.add(new PaymentPurpose("-4-", psId, new BigDecimal(10), "0", "платёж клиента 4"));

                        try {
                            callback.onResult(
                                    new PaymentSelectedEventResult(
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
