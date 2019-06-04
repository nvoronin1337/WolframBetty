package com.projects.nikita.wolframbetty.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FullResult implements Serializable{

    private String title;
    private String value;
    private String image_src;
    private double src_width_px;
    private double src_height_px;


    public FullResult(String title, String value, String imageSource) {
        this.title = title;
        this.value = value;
        this.image_src = imageSource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.title + ": " + this.value + ".\n";
    }



    public String getImage_src() {
        return image_src;
    }

    public void setImage_src(String image_src) {
        this.image_src = image_src;
    }


    public double getSrc_width_px() {
        return src_width_px;
    }

    public void setSrc_width_px(double src_width_px) {
        this.src_width_px = src_width_px;
    }

    public double getSrc_height_px() {
        return src_height_px;
    }

    public void setSrc_height_px(double src_height_px) {
        this.src_height_px = src_height_px;
    }
}
