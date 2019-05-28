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
import java.text.DateFormat;

public class MainActivity extends AppCompatActivity {
    // Logging tag
    private static final String TAG = "MainActivity";

    // Point in time
    private Calendar time;
    private static final int HOUR_OF_DAY = 6;
    private static final int MINUTE = 0;
    private static final int SECOND = 0;

    // Notification channel id
    private static final String CHANNEL_ID = "NotificationChannel";
    public static String getChannelId() { return CHANNEL_ID; }

    // Check
    private AlarmManager alarmMgr;
    private Intent checkIntent;

    TextView textView;
    TextView indicator;

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "DBDelay",
                NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void createTimePoint() {
        time = Calendar.getInstance();
        time.setTimeInMillis(System.currentTimeMillis());
        time.add(Calendar.DAY_OF_MONTH, 1);
        time.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
        time.set(Calendar.MINUTE, MINUTE);
        time.set(Calendar.SECOND, SECOND);
    }

    private boolean checkIsActive() {
        return (PendingIntent.getBroadcast(this, 0, checkIntent,
                PendingIntent.FLAG_NO_CREATE) != null);
    }

    private PendingIntent getPendingIntent() {
        return PendingIntent.getBroadcast(MainActivity.this, 0, checkIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Create");

        createNotificationChannel();
        createTimePoint();

        // Create checkIntent
        checkIntent = new Intent(this, RequestHandler.class);
        // Create alarm manager
        alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Get buttons
        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);

        // Create text and indicator
        boolean checkIsActive = checkIsActive();
        textView = findViewById(R.id.textView);
        indicator = findViewById(R.id.indicator);
        updateDisplay(checkIsActive);

        // Set click listeners
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Activate");
                activate(v);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Deactivate");
                deactivate(v);
            }
        });
    }

    private void activate(View v) {
        // Create the checkIntent to perform
        PendingIntent alarmIntent = getPendingIntent();

        // Set alarm and repeat once a day
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);

        updateDisplay(true);

        Log.d(TAG, "activate: Activated at " + DateFormat.getDateTimeInstance().format(time.getTime()));
    }

    private void deactivate(View v) {
        // Create the checkIntent to perform
        PendingIntent alarmIntent = getPendingIntent();

        // Cancel alarm
        alarmMgr.cancel(alarmIntent);
        alarmIntent.cancel();

        updateDisplay(false);

        Log.d(TAG, "deactivate: Deactivated");
    }

    private void updateDisplay(boolean checkIsActive) {
        textView.setText(getString(R.string.isActive, (checkIsActive) ? "" : "not "));
        indicator.setText(textView.getText());
        int color = getColor((checkIsActive) ? R.color.active : R.color.inactive);
        indicator.setTextColor(color);
        indicator.setBackgroundColor(color);
    }
}
