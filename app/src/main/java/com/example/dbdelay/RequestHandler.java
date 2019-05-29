package com.example.dbdelay;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class RequestHandler extends BroadcastReceiver {
    private static final String TAG = "RequestHandler";
    private static final String URL = "https://www.bahn.de/p/view/service/aktuell/index.shtml?dbkanal_007=L01_S01_D001_KIN0011_-_rs_auskunft_NAVIGATION-aktuell_LZ01";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(TAG, "onReceive: Intent received");

        // Instantiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(context);

        // Request response
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new MyResponseListener(context), new MyResponseErrorListener());

        // Add the request to the RequestQueue
        queue.add(stringRequest);
    }

    private class MyResponseListener implements Response.Listener<String> {
        // Class of the articles
        private static final String CLASS = "incident";
        // Displayed in the notification if there are no notices
        private static final String NO_NOTICE_STRING = "Keine relevanten Meldungen vorhanden";

        // Keywords to look for
        ArrayList<String> keywords = new ArrayList<>(Arrays.asList("Heidelberg",
                "Kirchheim", "Rohrbach", "St. Ilgen", "Sankt Ilgen", "Sandhausen",
                "Wiesloch", "Walldorf", "Rot", "St. Leon", "Sankt Leon", "Malsch",
                "Bad Schönborn", "Bad Schoenborn", "Kronau", "Ubstadt-Weiher",
                "Bruchsal", "Karlsruhe"));

        // Store a reference to the context
        Context context;

        private MyResponseListener(Context context) {
            this.context = context;
        }

        // Test if the article contains a keyword
        private boolean isRelevant(Element article) {
            for(String keyword : keywords)
                if(article.text().toLowerCase().contains(keyword.toLowerCase()))
                    return true;
            return false;
        }

        // Response is in ISO charset
        private String fixEncodingUnicode(String response) {
            return new String(response.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        }

        private void logTime() {
            DateFormat dateFormat = DateFormat.getDateTimeInstance();
            Calendar calendar = Calendar.getInstance();
            Log.d(TAG, "logTime: " + dateFormat.format(calendar.getTime()));
        }

        private String getContentText(ArrayList<Element> articles) {
            // Filter articles
            StringBuilder contentText = new StringBuilder();
            for(Element article : articles) {
                if(isRelevant(article)) {
                    String text = article.text();
                    String toAdd = (text.length() >= 80) ? text.substring(0, 80) : text;
                    contentText.append(toAdd).append("\n\n");
                }
            }
            return contentText.toString();
        }

        @Override
        public void onResponse(String response) {
            Log.d(TAG, "onResponse: ");
            logTime();

            // Parse response
            Document doc = Jsoup.parse(fixEncodingUnicode(response));
            ArrayList<Element> articles = doc.getElementsByClass(CLASS);

            // Filter articles
            String contentText = getContentText(articles);
            if(contentText.isEmpty())
                contentText = NO_NOTICE_STRING;

            // Notification tap intent
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    browserIntent, 0);

            String shortContent = (contentText.length() >= 50) ? contentText.substring(0, 50) : contentText;

            // Create the notification
            String CHANNEL_ID = MainActivity.getChannelId();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_icon)
                    .setContentTitle(doc.title())
                    .setContentText(shortContent)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(contentText))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent);

            // Send the notification
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(0, builder.build());
        }
    }

    private class MyResponseErrorListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d(TAG, "onErrorResponse: " + error.toString());
        }
    }
}
