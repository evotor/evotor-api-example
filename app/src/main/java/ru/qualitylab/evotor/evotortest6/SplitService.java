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

import ru.evotor.framework.component.PaymentPerformer;
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
import ru.evotor.framework.payment.PaymentSystem;
import ru.evotor.framework.payment.PaymentType;
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
        /*
        Указываем установленное на смарт-терминале приложение или его компонент, которое исполнит платежи.
        Исполнителя платежа также можно указать внутри метода call следущим образом: event.getPaymentPurpose().getPaymentPerformer();
         */
        PaymentPerformer paymentPerformerApplicationOrComponent = new PaymentPerformer(
                //Объект с описанием платёжной системы, которое использует приложение, исполняющее платёж.
                new PaymentSystem(PaymentType.ELECTRON, "Some description", "Payment system ID"),
                //Пакет, в котором расположен компонент, исполняющий платёж.
                "ru.evotor.paymentapp",
                //Название компонента, исполняющего платёж.
                "ComponentName",
                //Идентификатор уникальный идентификатор приложения, исполняющего платёж.
                "App identifier",
                //Название приложения, исполняющего платёж
                "App name");

        //Создаём платежи для нескольких юридических лиц и добавляем их в список.
        PaymentPurpose firstLegalEntityPayment = new PaymentPurpose(
                //Идентификатор платежа.
                "First payment identifier",
                //Идентификатор платёжной системы. Устаревший параметр.
                "Deprecated PaymentSystemId or Null",
                //Установленное на смарт-терминале приложение или его компонент, выполняющее платёж.
                paymentPerformerApplicationOrComponent,
                //Сумма платежа.
                new BigDecimal(50000),
                "Payment account identifier",
                //Сообщение для пользователя.
                "Your payment has proceeded successfully.");

        final List<PaymentPurpose> listOfAllPayments = Arrays.asList(firstLegalEntityPayment);

        //Создаём обработчик события выбора оплаты.
        PaymentSelectedEventProcessor yourEventProcessor = new PaymentSelectedEventProcessor() {
            @Override
            public void call(@NonNull String action, @NonNull PaymentSelectedEvent event, @NonNull Callback callback) {
                /*
                Все методы функции обратного вызова могут вернуть исключение RemoteException, которое необходимо правильно обработать.
                Например, с помощью конструкции try {} catch () {}.
                 */
                try {
                    /*
                    Вы также можете воспользоваться другими методами функции обратного вызова.
                    Например, запустить операцию с помощью startActivity(Intent intent) или отреагировть на ошибку с помощью одного из методов onError().
                     */
                    callback.onResult(new PaymentSelectedEventResult(
                            //Добавляем дополнительные данные в чек.
                            new SetExtra(null),
                            listOfAllPayments));
                } catch (RemoteException exception) {
                    exception.printStackTrace();
                }
            }
        };

        //Создаём и возвращаем в смарт-терминал результат обработки события в виде коллекиции пар "Событие":"Обработчик события".
        Map<String, ActionProcessor> eventProcessingResult = new HashMap<>();
        eventProcessingResult.put(PaymentSelectedEvent.NAME_SELL_RECEIPT, yourEventProcessor);

        return eventProcessingResult;
    }
}