package com.projects.nikita.wolframbetty.Fetchers;

import android.net.Uri;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class ShortAnswerFetcher {

    private final String TAG = "SHORT ANSWER";

    private final String BASE_URL = "http://api.wolframalpha.com/v1/result";
    private final String APPID = "YHURPP-9EH9LW9L3Q";    //YHURPP-9EH9LW9L3Q
    private final String OUTPUT = "json";

    /* Private method established connection with the specified URL which is passed as the argument.
     *  If connected, receives a text representation of the JSON file with the requested information.
     *  @return byte[] */
    private byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    private String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public String fetchRequest(String query){
        try{
            String url = Uri.parse(BASE_URL)
                    .buildUpon()
                    .appendQueryParameter("appid", APPID)
                    .appendQueryParameter("i", query)
                    .appendQueryParameter("output", OUTPUT)
                    .build().toString();

            Log.i(TAG, "String JSON: " + url);
            String response = getUrlString(url);

            Log.i(TAG, "Received String from URL: " + response);
            return response;

        }catch (IOException ex){
            Log.e(TAG, "failed to fetch items", ex);
        }catch (IllegalArgumentException ex){
            Log.e(TAG, ex.getMessage(), ex);
        }
        return null;
    }
}
