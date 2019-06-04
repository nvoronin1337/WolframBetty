package com.projects.nikita.wolframbetty.View;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.projects.nikita.wolframbetty.Fetchers.FullResultsFetcher;
import com.projects.nikita.wolframbetty.Model.FullResult;
import com.projects.nikita.wolframbetty.Model.FullResultsWrapper;
import com.projects.nikita.wolframbetty.R;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class DisplayResultFragment extends Fragment {

    private static final String TAG = "RESULTS FRAGMENT";
    private static final String KEY = "LIST";

    private FullResultsWrapper mResults;

    private TextView mTitle;
    private TextView mValue;
    private RecyclerView mResultRecyclerView;

    public static DisplayResultFragment newInstance(FullResultsWrapper results) { //(Bundle args)
        DisplayResultFragment fragment = new DisplayResultFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY, results);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if(mResults == null){
            Log.d(TAG, "is null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.result_pods_list, container, false);
        mResultRecyclerView = (RecyclerView) v.findViewById(R.id.result_recycler_view);
        mResultRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mResults = (FullResultsWrapper) getArguments().getSerializable(KEY);

        setupAdapter();
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setupAdapter() {
        if (isAdded())
            mResultRecyclerView.setAdapter(new PhotoAdapter(mResults));
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        private ImageView mItemImageView;



        public PhotoHolder(View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.title);
            mValue = itemView.findViewById(R.id.value);
            mItemImageView = itemView.findViewById(R.id.result_image_view);
        }

        public void bindTitle(String title){
            if(title != "Input interpretation"){
                mTitle.setVisibility(View.VISIBLE);
                mTitle.setText(title);
            }else{
                mTitle.setVisibility(View.INVISIBLE);
            }
            mValue.setVisibility(View.INVISIBLE);

        }

        public void bindText(String title, String value){
            if(title != "Input interpretation"){
                mTitle.setVisibility(View.VISIBLE);
                mValue.setVisibility(View.VISIBLE);
                mTitle.setText(title);
                mValue.setText(value);
            }else{
                mTitle.setVisibility(View.INVISIBLE);
                mValue.setVisibility(View.INVISIBLE);
            }

        }

        public void bindImage(String url, double height, double width){
            if(url != null){
                mItemImageView.setVisibility(View.VISIBLE);
                DisplayMetrics metrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                mItemImageView.requestLayout();

                mItemImageView.getLayoutParams().width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
                mItemImageView.getLayoutParams().width = mItemImageView.getLayoutParams().width - 8;
                Log.d("CHECK",mItemImageView.getLayoutParams().width + "." );

                double widthDP = convertPixelsToDp(width, getContext());
                double heightDP = convertPixelsToDp(height, getContext());

                // Change originalHeight and originalWidth
                mItemImageView.getLayoutParams().height = calculateNewDimensions(heightDP,widthDP, mItemImageView.getLayoutParams().width).intValue();

                RequestOptions options = new RequestOptions().centerCrop();
                Glide.with(getContext()).load(url).apply(options).into(mItemImageView);
            }
        }

        public void imageNotValid(){
            mItemImageView.setVisibility(View.INVISIBLE);
        }

        public Double calculateNewDimensions(double originalHeight, double originalWidth, double newWidth){
            double hypotenuse = Math.sqrt(Math.pow(originalWidth,2) + Math.pow(originalHeight,2)); // Pythagorean theorem
            double cosineTheta = originalWidth / hypotenuse;

            double newHypotenuse = newWidth / cosineTheta;
            return Math.sqrt(Math.pow(newHypotenuse,2) - Math.pow(newWidth,2));
        }

        /**
         * Found at: https://stackoverflow.com/questions/4605527/converting-pixels-to-dp
         *
         * This method converts dp unit to equivalent pixels, depending on device density.
         *
         * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
         * @param context Context to get resources and device specific display metrics
         * @return A float value to represent px equivalent to dp depending on device density
         */
        public float convertDpToPixel(float dp, Context context){
            Resources resources = context.getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
            return px;
        }

        /**
         * Found at: https://stackoverflow.com/questions/4605527/converting-pixels-to-dp
         *
         * This method converts device specific pixels to density independent pixels.
         *
         * @param px A value in px (pixels) unit. Which we need to convert into db
         * @param context Context to get resources and device specific display metrics
         * @return A float value to represent dp equivalent to px value
         */
        public double convertPixelsToDp(double px, Context context){
            return px / ((double) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        private FullResultsWrapper allResults;

        public PhotoAdapter(FullResultsWrapper results) {
            allResults = results;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.pod_item, viewGroup, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position) {
            ArrayList<FullResult> results = allResults.getResults();
            FullResult result = results.get(position);



            if(result.getImage_src() != null){
                //new ConvertSrcToBitmap(result, result.getImage_src());
                Log.d(TAG, "<" + result.getImage_src() + ">");
                if(getContext() != null){
                    String title = result.getTitle();
                    String value = result.getValue();

                    if(result.getSrc_width_px() >= 200) {
                        photoHolder.bindTitle(title);
                        photoHolder.bindImage(result.getImage_src(), result.getSrc_height_px(), result.getSrc_width_px());
                    }else{
                        photoHolder.imageNotValid();
                        photoHolder.bindText(title, value);
                    }

                }else{
                    Log.e(TAG, "no context found");
                }
            }else{
                Log.e(TAG, "no picture src found");
            }



        }

        @Override
        public int getItemCount() {
            return allResults.getSize();
        }

    }
}
