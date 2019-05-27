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
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    // Logging tag
    private static final String TAG = "MainActivity";

    // Point in time
    private Calendar calendar;
    private static final int HOUR_OF_DAY = 6;
    private static final int MINUTE = 0;
    private static final int SECOND = 0;

    // Notification channel id
    private static final String CHANNEL_ID = "NotificationChannel";
    public static String getChannelId() { return CHANNEL_ID; }

    // Alarm
    private AlarmManager alarmMgr;
    private Intent intent;

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
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
        calendar.set(Calendar.MINUTE, MINUTE);
        calendar.set(Calendar.SECOND, SECOND);

        // Create intent
        intent = new Intent(this, RequestHandler.class);

        // Create alarm manager
        alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Get buttons
        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);

        boolean alarmUp = (PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_NO_CREATE) != null);

        final TextView textView = findViewById(R.id.textView);
        textView.setText("Alarm is " + ((alarmUp) ? "" : "not ") + "set");

        // Set click listeners
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Start");
                // Cancel existing alarm
//                alarmMgr.cancel(alarmIntent);
//                alarmIntent.cancel();

                // Create the intent to perform
                PendingIntent alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent,
                        PendingIntent.FLAG_CANCEL_CURRENT);

                // Set alarm and repeat once a day
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, alarmIntent);
                textView.setText("Alarm is set");
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Stop");
                // Create the intent to perform
                PendingIntent alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent,
                        PendingIntent.FLAG_CANCEL_CURRENT);

                // Cancel alarm
                alarmMgr.cancel(alarmIntent);
                alarmIntent.cancel();
                textView.setText("Alarm is not set");
            }
        });
    }
}
