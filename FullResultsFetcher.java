package com.projects.nikita.wolframbetty.Fetchers;

import android.net.Uri;
import android.util.Log;
import com.projects.nikita.wolframbetty.Model.FullResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FullResultsFetcher {

    private final String TAG = "FULL RESULTS";

    private final String BASE_URL = "http://api.wolframalpha.com/v2/query";
    private final String FORMAT = "plaintext";
    private final String APPID = "YHURPP-9EH9LW9L3Q";
    private final String OUTPUT = "json";

    private final String QUERY_RESULT = "queryresult";
    private final String ARRAY = "pods";
    private final String SUBARRAY = "subpods";
    private final String TITLE = "title";
    private final String VALUE = "plaintext";
    private final String IMAGE = "img";
    private final String SRC = "src";

    private final String HEIGHT = "height";
    private final String WIDTH = "width";

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

    public ArrayList<FullResult> fetchRequest(String query){
        try{
            String url = Uri.parse(BASE_URL)
                    .buildUpon()
                    .appendQueryParameter("appid", APPID)
                    .appendQueryParameter("input", query)
                    //.appendQueryParameter("format", FORMAT)
                    .appendQueryParameter("output", OUTPUT)
                    .build().toString();

            Log.i(TAG, "String JSON: " + url);
            String jsonString = getUrlString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            //JSONArray array = jsonBody.getJSONArray("pods");

            Log.i(TAG, "Received String from URL: " + jsonString);
            return parseJson(jsonBody);

        }catch (IOException ex){
            Log.e(TAG, "failed to fetch items", ex);
        }catch (JSONException ex){
            Log.e(TAG, "failed to parse json", ex);
        }catch (IllegalArgumentException ex){
            Log.e(TAG, ex.getMessage(), ex);
        }
        return null;
    }

    private ArrayList<FullResult> parseJson(JSONObject jsonBody) throws JSONException, IOException{
        ArrayList<FullResult> results = new ArrayList<>();  // Here you create array of Strings or other objects in which u save data

        JSONObject queryResultJsonObject = jsonBody.getJSONObject(QUERY_RESULT);    //"queryresult"
        JSONArray queryArray = queryResultJsonObject.getJSONArray(ARRAY);   // "pods"

        for(int i = 0; i < queryArray.length(); i++){
            JSONObject jsonObject = queryArray.getJSONObject(i);

            String title = jsonObject.getString(TITLE); // thats how u can read a title of the info (Input Interpretation, Result, Location, etc.)
            if(title.equals(""))    // skips empty titles
                continue;

            JSONArray jsonSubArray = jsonObject.getJSONArray(SUBARRAY); // "subpods"
            JSONObject jsonSubObject = jsonSubArray.getJSONObject(0);   //the one and only jsonobject in subpods element

            String value = jsonSubObject.getString(VALUE);// "plaintext"    // you have the result here
            if(value.equals(""))    // skips empty results
                continue;

            /** All of this is for image*/
            JSONObject imageJsonObject = jsonSubObject.getJSONObject(IMAGE);
            String imageSource = imageJsonObject.getString(SRC);
            double imageHeightPX = imageJsonObject.getDouble(HEIGHT);
            double imageWidthPX = imageJsonObject.getDouble(WIDTH);

            /** This is my way of storing items */
            FullResult result = new FullResult(title,value,imageSource);
            result.setSrc_height_px(imageHeightPX);
            result.setSrc_width_px(imageWidthPX);
            Log.i(TAG, result.getImage_src());

            results.add(result);
        }
        return results; // here return the result (I recommend you create your own class Result which has String title and String result)
    }
}
