package com.projects.nikita.wolframbetty.Fetchers;

import android.net.Uri;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

/** Public class QueryRecognizerFetcher
 *  is used to check if query entered by the user may be recognized by
 *  the Wolfram Alpha compute engine. Using API: (Fast Query Recognizer)
 *  more info on this API: https://products.wolframalpha.com/query-recognizer/documentation/
 * */
public class QueryRecognizerFetcher {
    private final String TAG = "QUERY RECOGNIZER";

    private final String BASE_URL = "https://www.wolframalpha.com/queryrecognizer/query.jsp";
    private final String APPID = "DEMO";   //YHURPP-9EH9LW9L3Q
    private final String MODE = "Default";
    private final String OUTPUT = "json";

    private final String ARRAY = "query";
    private final String VALUE = "accepted";

    /* Private method established connection with the specified URL which is passed as the argument.
     *  If connected, receives a text representation of the JSON file with the requested information.
     *  @return byte[] */
    private byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
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

    public boolean fetchRequest(String query){
        try{
            String url = Uri.parse(BASE_URL)
                    .buildUpon()
                    .appendQueryParameter("mode", MODE)
                    .appendQueryParameter("i", query)
                    .appendQueryParameter("appid", APPID)
                    .appendQueryParameter("output", OUTPUT)
                    .build().toString();
            Log.i(TAG, "String JSON: " + url);
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            return parseJson(jsonBody);
        }catch (IOException ex){
            Log.e(TAG, "failed to fetch items", ex);
        }catch (JSONException ex){
            Log.e(TAG, "failed to parse JSON", ex);
        }catch (IllegalArgumentException ex){
            Log.e(TAG, ex.getMessage(), ex);
        }
        return false;
    }

    private boolean parseJson(JSONObject jsonBody) throws JSONException{
        JSONArray queryArray = jsonBody.getJSONArray(ARRAY);
        if(queryArray.length() > 1){
            throw new IllegalArgumentException("too many objects retrieved");
        }
        JSONObject query = queryArray.getJSONObject(0);
        return query.getBoolean(VALUE);
    }
}
