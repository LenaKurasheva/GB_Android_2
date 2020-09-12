package ru.geekbrains.gb_android_2.broadcastReceiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import androidx.core.app.NotificationCompat;

import ru.geekbrains.gb_android_2.R;

public class WifiConnectionReceiver extends BroadcastReceiver {
    private static int messageId = 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            int extra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            if (extra == WifiManager.WIFI_STATE_ENABLED) {
                clearNotification(context);
            } else if (extra == WifiManager.WIFI_STATE_DISABLED){
                    // создать нотификацию
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "2")
                            .setSmallIcon(R.drawable.ic_home)
                            .setContentTitle(context.getString(R.string.broadcastReceiver_wifi_conn_failed))
                            .setContentText(context.getString(R.string.broadcastReceiver_check_wifi));
                    NotificationManager notificationManager =
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify("wifi", ++messageId, builder.build());
            }
        }
    }
    public void clearNotification(Context context){
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel("wifi", messageId);
    }
}
