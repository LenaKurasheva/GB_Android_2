package ru.geekbrains.gb_android_2.broadcastReceiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import ru.geekbrains.gb_android_2.CurrentDataContainer;
import ru.geekbrains.gb_android_2.R;

public class InternetConnectionReceiver extends BroadcastReceiver {
    private static int messageId = 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("app","Network connectivity change");
            if(intent.getExtras()!=null) {
                if(!intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)){
                    Log.i("app","Network connected");
                    CurrentDataContainer.isNetworkConnected = true;
                    clearNotification(context);

                } else if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
                    Log.d("app","There's no network connectivity");
                    CurrentDataContainer.isNetworkConnected = false;
                    // создать нотификацию
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1")
                            .setSmallIcon(R.drawable.ic_home)
                            .setContentTitle(context.getString(R.string.broadcastReceiver_internet_conn_failed))
                            .setContentText(context.getString(R.string.broadcastReceiver_check_internet));
                    NotificationManager notificationManager =
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify("internet", ++messageId, builder.build());
                }
            }
    }

    public void clearNotification(Context context){
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel("internet", messageId);
    }
}

