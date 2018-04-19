package ru.qualitylab.evotor.evotortest6;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import ru.evotor.pushNotifications.PushNotificationReceiver;


public class PushReceiver extends PushNotificationReceiver {

    @Override
    public void onReceivePushNotification(Context context, Bundle data, long messageId) {
        //...receive push notification
        Toast.makeText(context, data.getString("header") + " " + data.getString("description")
                + " " + messageId, Toast.LENGTH_SHORT).show();
    }
}
