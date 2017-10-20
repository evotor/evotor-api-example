package ru.qualitylab.evotor.evotortest6;

import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ru.evotor.framework.core.IntegrationService;
import ru.evotor.framework.core.action.event.receipt.changes.position.SetPrintGroup;
import ru.evotor.framework.core.action.event.receipt.print_group.PrintGroupRequiredEvent;
import ru.evotor.framework.core.action.event.receipt.print_group.PrintGroupRequiredEventProcessor;
import ru.evotor.framework.core.action.event.receipt.print_group.PrintGroupRequiredEventResult;
import ru.evotor.framework.core.action.processor.ActionProcessor;
import ru.evotor.framework.receipt.PrintGroup;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;
import ru.evotor.framework.receipt.TaxationSystem;

public class SplitGroupService extends IntegrationService {
    @Nullable
    @Override
    protected Map<String, ActionProcessor> createProcessors() {
        Map<String, ActionProcessor> map = new HashMap<>();
        map.put(
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
        );
        return map;
    }
}
