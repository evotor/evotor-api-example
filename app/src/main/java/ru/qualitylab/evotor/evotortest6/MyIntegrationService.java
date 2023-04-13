package ru.qualitylab.evotor.evotortest6;

import android.content.Intent;
import android.os.RemoteException;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import ru.evotor.framework.core.IntegrationService;
import ru.evotor.framework.core.action.event.receipt.before_positions_edited.BeforePositionsEditedEvent;
import ru.evotor.framework.core.action.event.receipt.before_positions_edited.BeforePositionsEditedEventProcessor;
import ru.evotor.framework.core.action.event.receipt.changes.position.IPositionChange;
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd;
import ru.evotor.framework.core.action.processor.ActionProcessor;
import ru.evotor.framework.receipt.Position;

/**
 * Сервис обработки сообщений терминала
 */
public class MyIntegrationService extends IntegrationService {
    @Nullable
    @Override
    protected Map<String, ActionProcessor> createProcessors() {
        Map<String, ActionProcessor> map = new HashMap<>();
        map.put(BeforePositionsEditedEvent.NAME_SELL_RECEIPT, new BeforePositionsEditedEventProcessor() {
            @Override
            public void call(@NonNull String action, @NonNull BeforePositionsEditedEvent event, @NonNull Callback callback) {
                boolean hasCoffee = false;
                Position foundPosition = null;
                String uuid = null, productUuid = null;
                //Просматриваем все изменения в чеке
                for (IPositionChange change : event.getChanges()) {
                    //Если добавлен необходимый нам товар в чек - сохраним позицию товара
                    if (change instanceof PositionAdd) {
                        foundPosition = ((PositionAdd) change).getPosition();
                        if (foundPosition.getName().toLowerCase().contains("кофе")) {
                            uuid = foundPosition.getUuid();
                            productUuid = foundPosition.getProductUuid();
                            hasCoffee = true;
                            break;
                        }
                    }
                }

                try {
                    if (hasCoffee) {
                        //Передадим полученные данные о товаре в Activity
                        Intent intent = new Intent(getApplicationContext(), EditActivity.class);
//                        intent.putExtra("foundPosition", foundPosition);
                        intent.putExtra("uuid", uuid);
                        intent.putExtra("productUuid", productUuid);
                        callback.startActivity(intent);
                    } else {
                        callback.skip();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        return map;
    }
}
