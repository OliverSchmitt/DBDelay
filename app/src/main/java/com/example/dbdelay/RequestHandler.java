package com.example.dbdelay;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class RequestHandler extends BroadcastReceiver {
    private static final String TAG = "RequestHandler";
    private static final String CHANNEL_ID = "NotificationChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Intent received");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Title")
                .setContentText("Content")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(0, builder.build());

    }
}
