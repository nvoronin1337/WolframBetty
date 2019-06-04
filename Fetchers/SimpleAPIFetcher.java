package com.projects.nikita.wolframbetty.Fetchers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SimpleAPIFetcher {
    private final String TAG = "SIMPLE API FETCHER";

    private final String BASE_URL = "http://api.wolframalpha.com/v1/simple";
    private final String APPID = "YHURPP-9EH9LW9L3Q";    //YHURPP-9EH9LW9L3Q
    private final String BACKGROUND = "F5F5F5";

    private String query;

    /* Private method established connection with the specified URL which is passed as the argument.
     *  If connected, receives a text representation of the JSON file with the requested information.
     *  @return byte[] */
    private Bitmap getUrlBitmap(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try {
            //ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            return BitmapFactory.decodeStream(in);
        } finally {
            connection.disconnect();
        }
    }

    public Bitmap fetchRequest(String query){
        try{
            String url = Uri.parse(BASE_URL)
                    .buildUpon()
                    .appendQueryParameter("appid", APPID)
                    .appendQueryParameter("i", query)
                    .appendQueryParameter("background", BACKGROUND)
                    .build().toString();

            Log.i(TAG, "String url: " + url);
            return getUrlBitmap(url);
        }catch (IOException ex){
            Log.e(TAG, "failed to fetch items", ex);
        }catch (IllegalArgumentException ex){
            Log.e(TAG, ex.getMessage(), ex);
        }
        return null;
    }
}
