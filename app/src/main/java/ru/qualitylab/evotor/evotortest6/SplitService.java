package ru.qualitylab.evotor.evotortest6;

import android.os.RemoteException;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.evotor.framework.core.IntegrationService;
import ru.evotor.framework.core.action.event.receipt.changes.position.SetExtra;
import ru.evotor.framework.core.action.event.receipt.changes.position.SetPrintGroup;
import ru.evotor.framework.core.action.event.receipt.payment.PaymentSelectedEvent;
import ru.evotor.framework.core.action.event.receipt.payment.PaymentSelectedEventProcessor;
import ru.evotor.framework.core.action.event.receipt.payment.PaymentSelectedEventResult;
import ru.evotor.framework.core.action.event.receipt.print_group.PrintGroupRequiredEvent;
import ru.evotor.framework.core.action.event.receipt.print_group.PrintGroupRequiredEventProcessor;
import ru.evotor.framework.core.action.event.receipt.print_group.PrintGroupRequiredEventResult;
import ru.evotor.framework.core.action.processor.ActionProcessor;
import ru.evotor.framework.payment.PaymentPurpose;
import ru.evotor.framework.receipt.PrintGroup;
import ru.evotor.framework.receipt.Purchaser;
import ru.evotor.framework.receipt.PurchaserType;
import ru.evotor.framework.receipt.TaxationSystem;

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
        //Создаём реквизиты покупателей, которые участвуют в приобретении товара.
        Purchaser firstLegalEntity = new Purchaser(
                //Наименование покупателя, например, название организации. Данные сохраняются в теге 1227 фискального документа.
                "Legal Entity #1",
                //Номер документа покупателя, например, ИНН или номер паспорта иностранного гражданина. Данные сохраняются в теге 1228 фискального документа.
                "606053449439",
                //Тип покупателя, например, юр. лицо. Не сохраняется в фискальном документе.
                PurchaserType.LEGAL_ENTITY);

        //Создаём печатные группы (чеки), для каждого покупателя.
        PrintGroup firstReceipt = new PrintGroup(
                //Идентифискатор печатной группы (чека покупателя).
                "123456789qwertyu",
                //Тип чека, например, кассовый чек.
                PrintGroup.Type.CASH_RECEIPT,
                //Наименование покупателя.
                "OOO Vector",
                //ИНН покупателя.
                "606053449439",
                //Адрес покупателя.
                "12, 3k2, Dark street, Nsk, Russia",
                /*
                Система налогообложения, которая применялась при расчёте.
                Смарт-терминал печатает чеки с указанной системой налогообложения, если она попадает в список разрешённых систем. В противном случае смарт-терминал выбирает систему налогообложения, заданную по умолчанию.
                */
                TaxationSystem.SIMPLIFIED_INCOME,
                //Указывает на необходимость печати чека.
                false,
                //Реквизиты покупателя.
                firstLegalEntity);

        //Списки идентификаторов позиций каждого из покупателей.
        List<String> firstPurchaserpositions = new ArrayList<>();
        firstPurchaserpositions.add("241e9344-ef50-46bc-9ce2-443c38b649e5");



        //Списки идентификаторов платежей каждого из покупателей.
        List<String> firstPaymentPurposeId = new ArrayList<>();
        firstPaymentPurposeId.add("First purchaser payment ID");


        //Чеки каждого из покупателей.
        SetPrintGroup firstPrintGroup = new SetPrintGroup(firstReceipt, firstPaymentPurposeId, firstPurchaserpositions);

        final List<SetPrintGroup> setAllPurchaserReceipts = Arrays.asList(firstPrintGroup);

        //Создаём обработчик события печати чеков.
        PrintGroupRequiredEventProcessor eventProcessor = new PrintGroupRequiredEventProcessor() {
            @Override
            public void call(@NonNull String action, @NonNull PrintGroupRequiredEvent event, @NonNull Callback callback) {
                /*
                Все методы функции обратного вызова могут вернуть исключение RemoteException, которое необходимо правильно обработать.
                Например, с помощью конструкции try {} catch () {}.
                 */
                try {
                    /*
                    Вы также можете воспользоваться другими методами функции обратного вызова.
                    Например, запустить операцию с помощью startActivity(Intent intent) или отреагировть на ошибку с помощью одного из методов onError().
                     */
                    callback.onResult(new PrintGroupRequiredEventResult(
                            //Добавляем дополнительные данные в чек.
                            new SetExtra(null),
                            setAllPurchaserReceipts));
                } catch (RemoteException exception) {
                    exception.printStackTrace();
                }

            }
        };

        //Создаём и возвращаем в смарт-терминал результат обработки события в виде коллекиции пар "Событие":"Обработчик события".
        Map<String, ActionProcessor> eventProcessingResult = new HashMap<>();
        eventProcessingResult.put(PrintGroupRequiredEvent.NAME_SELL_RECEIPT, eventProcessor);

        return eventProcessingResult;
    }
}