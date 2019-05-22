package com.example.dbdelay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class RequestHandler extends BroadcastReceiver {
    private static final String TAG = "RequestHandler";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Intent received");
        // Create the notification
        String CHANNEL_ID = MainActivity.getChannelId();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Title")
                .setContentText("Content")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Send the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, builder.build());

    }
}
