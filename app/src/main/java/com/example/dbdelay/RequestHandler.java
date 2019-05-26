package com.example.dbdelay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Html;
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

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class RequestHandler extends BroadcastReceiver {
    private static final String TAG = "RequestHandler";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(TAG, "onReceive: Intent received");

        // Instantiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://www.bahn.de/p/view/service/aktuell/index.shtml?dbkanal_007=L01_S01_D001_KIN0011_-_rs_auskunft_NAVIGATION-aktuell_LZ01";

        // Request response
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    ArrayList<String> keywords = new ArrayList<String>(Arrays.asList("Heidelberg", "Karlsruhe", "Niederlande", "Berlin"));

                    private boolean isRelevant(Element article) {
                        for(String keyword : keywords)
                            if(article.text().toLowerCase().contains(keyword.toLowerCase()))
                                return true;
                        return false;
                    }

                    private String fixEncodingUnicode(String response) {
                        return new String(response.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: ");

                        // Parse response
                        Document doc = Jsoup.parse(fixEncodingUnicode(response));
                        ArrayList<Element> articles = doc.getElementsByClass("incident");

                        // Filter articles
                        StringBuilder contentText = new StringBuilder();
                        for(Element article : articles) {
                            String text = article.text();
                            if(isRelevant(article)) {
                                contentText.append(text.substring(0, 80)).append("\n\n");
                            }
                        }

                        // Create the notification
                        String CHANNEL_ID = MainActivity.getChannelId();
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_launcher_foreground)
                                .setContentTitle(doc.title())
                                .setContentText(contentText.toString().substring(0, 50))
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(contentText.toString()))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        // Send the notification
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                        notificationManager.notify(0, builder.build());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.toString());
            }
        });

        // Add the request to the RequestQueue
        queue.add(stringRequest);
    }
}
