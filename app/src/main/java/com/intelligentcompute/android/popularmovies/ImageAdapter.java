package com.intelligentcompute.android.popularmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> posterThumbnails;

    public ImageAdapter(Context c, ArrayList<String> pThumbs) {
        context = c;
        posterThumbnails = pThumbs;
    }

    public int getCount() {
        return posterThumbnails.size();
    }

    public String getItem(int position) {
        return posterThumbnails.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            /*imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);*/
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(context).load(posterThumbnails.get(position)).into(imageView);

        return imageView;
    }





    public void updateThumbnails(ArrayList<String> imageURLs) {
        this.posterThumbnails = imageURLs;
        notifyDataSetChanged();

    }
}