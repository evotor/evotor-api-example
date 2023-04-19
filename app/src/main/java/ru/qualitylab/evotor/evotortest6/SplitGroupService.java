package ru.qualitylab.evotor.evotortest6;

import android.os.RemoteException;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

/**
 * Разделение чека на несколько печатных групп
 * В манифесте указать для сервиса <action android:name="evo.v2.receipt.sell.printGroup.REQUIRED" />
 */
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
                        //Печатная группа
                        PrintGroup printGroup = new PrintGroup(
                                //Идентификатор печатной группы
                                UUID.randomUUID().toString(),
                                //Тип чека
                                //фискальный – CASH_RECEIPT
                                //квитанция (нефискальный чек) – INVOICE
                                //ЕНВД (нефискальный чек) – STRING_UTII
                                PrintGroup.Type.INVOICE,
                                //Реквизиты организации
                                "ООО \"Пример\"",
                                "012345678901",
                                "г. Москва",
                                //Систему налогообложения
                                TaxationSystem.PATENT,
                                //Необходимость печати чека
                                true
                        );
                        //Список идентификаторов платежей
                        List<String> paymentPurposeIds = new ArrayList<>();
                        paymentPurposeIds.add("-1-");
                        paymentPurposeIds.add("-3-");
                        //Список иденификаторов позиций в формате uuid4
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
                                            //Дополнительные данные для приложения
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
