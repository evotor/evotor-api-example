package ru.qualitylab.evotor.evotortest6;

import android.content.Intent;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import ru.evotor.framework.core.IntegrationService;
import ru.evotor.framework.core.action.event.receipt.before_positions_edited.BeforePositionsEditedEvent;
import ru.evotor.framework.core.action.event.receipt.before_positions_edited.BeforePositionsEditedEventProcessor;
import ru.evotor.framework.core.action.event.receipt.before_positions_edited.BeforePositionsEditedEventResult;
import ru.evotor.framework.core.action.event.receipt.changes.position.IPositionChange;
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd;
import ru.evotor.framework.core.action.processor.ActionProcessor;
import ru.evotor.framework.receipt.Position;

public class MyIntegrationService extends IntegrationService {
    @Nullable
    @Override
    protected Map<String, ActionProcessor> createProcessors() {
        Map<String, ActionProcessor> map = new HashMap<>();
        map.put(BeforePositionsEditedEvent.NAME_SELL_RECEIPT, new BeforePositionsEditedEventProcessor() {
            @Override
            public void call(@NonNull String action, @NonNull BeforePositionsEditedEvent event, @NonNull Callback callback) {
                boolean hasCoffee = false;
                String uuidCoffee = "", prodUuidCoffee = "";
                for (IPositionChange change : event.getChanges()) {
                    if (change instanceof PositionAdd) {
                        Position position = ((PositionAdd) change).getPosition();
                        if (position.getName().toLowerCase().contains("кофе")) {
                            uuidCoffee = position.getUuid();
                            prodUuidCoffee = position.getProductUuid();
                            hasCoffee = true;
                            break;
                        }
                    }
                }

                try {
                    if (hasCoffee) {
                        Intent intent = new Intent(getApplicationContext(), SuggestActivity.class);
                        intent.putExtra("uuidCoffee", uuidCoffee);
                        intent.putExtra("prodUuidCoffee", prodUuidCoffee);
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
