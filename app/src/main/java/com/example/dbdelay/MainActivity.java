package com.example.dbdelay;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    // Logging tag
    private static final String TAG = "MainActivity";

    // Point in time
    private Calendar calendar;
    private static final int HOUR_OF_DAY = 5;
    private static final int MINUTE = 0;
    private static final int SECOND = 0;

    // Notification channel id
    private static final String CHANNEL_ID = "NotificationChannel";
    public static String getChannelId() {
        return CHANNEL_ID;
    }

    // Alarm
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Notification channel
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "DBDelay",
                NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        // Specify the time to perform the intent
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
        calendar.set(Calendar.MINUTE, MINUTE);
        calendar.set(Calendar.SECOND, SECOND);

        // Create the intent to perform
        Intent intent = new Intent(this, RequestHandler.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Create alarm manager
        alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Get buttons
        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);

        // Set click listeners
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Start");
                // Cancel existing alarm
                alarmMgr.cancel(alarmIntent);
                // Set alarm and repeat once a day
                alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, alarmIntent);

            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Stop");
                // Cancel alarm
                alarmMgr.cancel(alarmIntent);
            }
        });
    }
}
