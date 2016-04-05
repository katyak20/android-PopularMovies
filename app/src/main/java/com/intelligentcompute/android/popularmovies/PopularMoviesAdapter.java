package com.intelligentcompute.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by katya on 30/03/2016.
 */
public class PopularMoviesAdapter extends CursorAdapter {

    private Context context;
    private Cursor c;
    private ArrayList<String> posterThumbnails;


    public PopularMoviesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String imageURL = cursor.getString(PopularMoviesFragment.COL_POSTER_PATH);
        String constructedURL = "http://image.tmdb.org/t/p/w300" + imageURL + "&api_key=e257613f461ed40c956dc1464fb16313";

        //ImageView imageView = (ImageView) view;
        ImageView imageView = viewHolder.iconView;
        Picasso.with(context).load(constructedURL).into(imageView);

    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
       // ImageView imageView;

        View view = LayoutInflater.from(context).inflate(R.layout.grid_view_item, parent, false);
        //imageView = new ImageView(context);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        //bindView(imageView, context, cursor);
        return view;
    }

    public static class ViewHolder {
        public final ImageView iconView;


        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.movie_thumbnail);

        }
    }
}
