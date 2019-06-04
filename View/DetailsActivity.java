package com.projects.nikita.wolframbetty.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.projects.nikita.wolframbetty.Fetchers.QueryRecognizerFetcher;
import com.projects.nikita.wolframbetty.Fetchers.SimpleAPIFetcher;
import com.projects.nikita.wolframbetty.R;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import com.bumptech.glide.request.target.DrawableImageViewTarget;


public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "Details Activity";
    private static final String QUERY = "query";
    private static final String BITMAP = "bitmap";

    private ImageView image;
    private String query;
    private Bitmap bitmapImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
        setContentView(R.layout.activity_details);

        image = findViewById(R.id.image_details);

        if(savedInstanceState != null){
            if(savedInstanceState.getString(QUERY) != null)
                query = savedInstanceState.getString(QUERY);
            if(savedInstanceState.getParcelable(BITMAP) != null) {
                bitmapImage = savedInstanceState.getParcelable(BITMAP);
                image.setImageBitmap(bitmapImage);
            }
        }else{
            DrawableImageViewTarget imageViewTarget = new DrawableImageViewTarget(image);
            Glide.with(this).load(R.raw.ping_pong_loader).into(imageViewTarget);
            if(getIntent() != null){
                if(getIntent().hasExtra(QUERY)){
                    query = getIntent().getStringExtra(QUERY);
                    new FetchSimpleAPITask(this).execute();
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if(query != null)
            savedInstanceState.putString(QUERY,query);
        if(bitmapImage != null)
            savedInstanceState.putParcelable(BITMAP,bitmapImage);
    }

    /* ************************************************************************************************************ */
    /** Inner private static class extends AsyncTask to get image using the Simple API by Wolfram Alpha.
     *
     *  It is important to note that class is static in order to prevent memory leaks.
     *  This leaks may occur in non-static class because Task will not be destroyed
     *  when the Activity is destroyed.
     *
     *  Context of the MainSubwayTrackerActivity is passed as the only argument in the constructor
     *  for this inner class. That is done in order to give this class access to the views of the
     *  Activity without any memory leaks.
     *  */
    private static class FetchSimpleAPITask extends AsyncTask<Void,Void,Bitmap> {
        private WeakReference<DetailsActivity> weakActivityRef;

        FetchSimpleAPITask(DetailsActivity context){
            weakActivityRef = new WeakReference<>(context);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            DetailsActivity context = weakActivityRef.get();
            return new SimpleAPIFetcher().fetchRequest(context.query);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            DetailsActivity context = weakActivityRef.get();
            Log.d("DETAILD", "onPostExecute called");
            if(result != null){
                context.bitmapImage = result;
                context.image.setImageBitmap(result);
            }
            Looper.loop();
            Looper.myLooper().quit();
        }
    }
}
