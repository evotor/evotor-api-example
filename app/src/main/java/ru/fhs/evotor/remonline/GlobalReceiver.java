package ru.fhs.evotor.remonline;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Получение событий об открытии чека, обновлении базы продуктов или результате изменения чека
 * Смарт терминал не ждёт ответ от приложения на широковещательные сообщения.
 */
public class GlobalReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        Log.e(getClass().getSimpleName(), action);
/*
        if (action != null) {
            switch (action) {
                //Чек продажи был успешно открыт
                case "evotor.intent.action.receipt.sell.OPENED":
                    Log.e(getClass().getSimpleName(), "Data:" + ReceiptOpenedEvent.create(bundle).getReceiptUuid());
                    Toast.makeText(context, action + "\nData:" + ReceiptOpenedEvent.create(bundle).getReceiptUuid(), Toast.LENGTH_SHORT).show();
                    break;
                //Чек возврата был успешно открыт
                case "evotor.intent.action.receipt.payback.OPENED":
                    Log.e(getClass().getSimpleName(), "Data:" + ReceiptOpenedEvent.create(bundle).getReceiptUuid());
                    Toast.makeText(context, action + "\nData:" + ReceiptOpenedEvent.create(bundle).getReceiptUuid(), Toast.LENGTH_SHORT).show();
                    break;
                //Позиция была добавлена в чек продажи
                case "evotor.intent.action.receipt.sell.POSITION_ADDED":
                    Log.e(getClass().getSimpleName(), "Data:" + PositionAddedEvent.create(bundle).getReceiptUuid() + " " + PositionAddedEvent.create(bundle).getPosition().getName());
                    Toast.makeText(context, action + "\nData: " + PositionAddedEvent.create(bundle).getReceiptUuid() + " " + PositionAddedEvent.create(bundle).getPosition().getName(), Toast.LENGTH_SHORT).show();
                    break;
                //Позиция была добавлена в чек возврата
                case "evotor.intent.action.receipt.payback.POSITION_ADDED":
                    Log.e(getClass().getSimpleName(), "Data:" + PositionAddedEvent.create(bundle).getReceiptUuid() + " " + PositionAddedEvent.create(bundle).getPosition().getName());
                    Toast.makeText(context, action + "\nData: " + PositionAddedEvent.create(bundle).getReceiptUuid() + " " + PositionAddedEvent.create(bundle).getPosition().getName(), Toast.LENGTH_SHORT).show();
                    break;
                //Позиция была отредактирована в чеке продажи
                case "evotor.intent.action.receipt.sell.POSITION_EDITED":
                    Log.e(getClass().getSimpleName(), "Data:" + PositionEditedEvent.create(bundle).getReceiptUuid() + " " + PositionEditedEvent.create(bundle).getPosition().getName());
                    Toast.makeText(context, action + "\nData: " + PositionEditedEvent.create(bundle).getReceiptUuid() + " " + PositionEditedEvent.create(bundle).getPosition().getName(), Toast.LENGTH_SHORT).show();
                    break;
                //Позиция была отредактирована в чеке возврата
                case "evotor.intent.action.receipt.payback.POSITION_EDITED":
                    Log.e(getClass().getSimpleName(), "Data:" + PositionEditedEvent.create(bundle).getReceiptUuid() + " " + PositionEditedEvent.create(bundle).getPosition().getName());
                    Toast.makeText(context, action + "\nData: " + PositionEditedEvent.create(bundle).getReceiptUuid() + " " + PositionEditedEvent.create(bundle).getPosition().getName(), Toast.LENGTH_SHORT).show();
                    break;
                //Позиция была удалена из чека продажи
                case "evotor.intent.action.receipt.sell.POSITION_REMOVED":
                    Log.e(getClass().getSimpleName(), "Data:" + PositionRemovedEvent.create(bundle).getReceiptUuid() + " " + PositionRemovedEvent.create(bundle).getPosition().getName());
                    Toast.makeText(context, action + "\nData: " + PositionEditedEvent.create(bundle).getReceiptUuid() + " " + PositionRemovedEvent.create(bundle).getPosition().getName(), Toast.LENGTH_SHORT).show();
                    break;
                //Позиция была удалена из чека возврата
                case "evotor.intent.action.receipt.payback.POSITION_REMOVED":
                    Log.e(getClass().getSimpleName(), "Data:" + PositionRemovedEvent.create(bundle).getReceiptUuid() + " " + PositionRemovedEvent.create(bundle).getPosition().getName());
                    Toast.makeText(context, action + "\nData: " + PositionRemovedEvent.create(bundle).getReceiptUuid() + " " + PositionRemovedEvent.create(bundle).getPosition().getName(), Toast.LENGTH_SHORT).show();
                    break;
                //Обновление базы товаров
                case "evotor.intent.action.inventory.PRODUCTS_UPDATED":
                    Log.e(getClass().getSimpleName(), "Data: PRODUCTS_UPDATED");
                    Toast.makeText(context, "Data: PRODUCTS_UPDATED", Toast.LENGTH_SHORT).show();
                    break;
                //Чек продажи был очищен
                case "evotor.intent.action.receipt.sell.CLEARED":
                    Log.e(getClass().getSimpleName(), "Data:" + ReceiptClearedEvent.create(bundle).getReceiptUuid());
                    Toast.makeText(context, action + "\nData: " + ReceiptClearedEvent.create(bundle).getReceiptUuid(), Toast.LENGTH_SHORT).show();
                    break;
                //Чек возврата был очищен
                case "evotor.intent.action.receipt.payback.CLEARED":
                    Log.e(getClass().getSimpleName(), "Data:" + ReceiptClearedEvent.create(bundle).getReceiptUuid());
                    Toast.makeText(context, action + "\nData: " + ReceiptClearedEvent.create(bundle).getReceiptUuid(), Toast.LENGTH_SHORT).show();
                    break;
                //Чек продажи был успешно закрыт
                case "evotor.intent.action.receipt.sell.RECEIPT_CLOSED":
                    Log.e(getClass().getSimpleName(), "Data:" + ReceiptClosedEvent.create(bundle).getReceiptUuid());
                    Toast.makeText(context, action + "\nData: " + ReceiptClosedEvent.create(bundle).getReceiptUuid(), Toast.LENGTH_SHORT).show();
                    break;
                //Чек возврата был успешно закрыт
                case "evotor.intent.action.receipt.payback.RECEIPT_CLOSED":
                    Log.e(getClass().getSimpleName(), "Data:" + ReceiptClosedEvent.create(bundle).getReceiptUuid());
                    Toast.makeText(context, action + "\nData: " + ReceiptClosedEvent.create(bundle).getReceiptUuid(), Toast.LENGTH_SHORT).show();
                    break;
                //Внесение денег
                case "evotor.intent.action.cashOperation.IN":
                    Log.e(getClass().getSimpleName(), "Data:" + CashInEvent.create(bundle).getDocumentUuid() + " " + CashInEvent.create(bundle).getTotal().toPlainString());
                    Toast.makeText(context, action + "\nData:" + CashInEvent.create(bundle).getDocumentUuid() + " " + CashInEvent.create(bundle).getTotal().toPlainString(), Toast.LENGTH_SHORT).show();
                    break;
                //Выплата денег
                case "evotor.intent.action.cashOperation.CASH_OUT":
                    Log.e(getClass().getSimpleName(), "Data:" + CashOutEvent.create(bundle).getDocumentUuid() + " " + CashOutEvent.create(bundle).getTotal().toPlainString());
                    Toast.makeText(context, action + "\nData:" + CashOutEvent.create(bundle).getDocumentUuid() + " " + CashOutEvent.create(bundle).getTotal().toPlainString(), Toast.LENGTH_SHORT).show();
                    break;
                //Открытие карточки товара\товарной группы
                case "evotor.intent.action.inventory.CARD_OPEN":
                    Log.e(getClass().getSimpleName(), "Data:" + ProductCardOpenedEvent.create(bundle).getProductUuid());
                    Toast.makeText(context, action + "\nData:" + ProductCardOpenedEvent.create(bundle).getProductUuid(), Toast.LENGTH_SHORT).show();
                    break;
                //Открытие ящика для денег
                case "evotor.intent.action.cashDrawer.OPEN":
                    Log.e(getClass().getSimpleName(), "Data:" + CashDrawerOpenEvent.create(bundle).getCashDrawerId());
                    Toast.makeText(context, action + "\nData:" + CashDrawerOpenEvent.create(bundle).getCashDrawerId(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }*/
    }
}
