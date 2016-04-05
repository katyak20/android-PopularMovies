package com.intelligentcompute.android.popularmovies.tasks;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.intelligentcompute.android.popularmovies.DetailActivityWithSlidingTabs;
import com.intelligentcompute.android.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by katya on 25/03/2016.
 */
public class FetchReviewsForMovieTask  extends AsyncTask<ArrayList<String>, Void, HashMap<String, ArrayList<HashMap<String, String>>>> {

    private final String LOG_TAG = FetchReviewsForMovieTask.class.getSimpleName();
    private final Context mContext;
    private HashMap<String, ArrayList<HashMap<String, String>>> moviesReviews = new HashMap<>();

    public FetchReviewsForMovieTask(Context context) {
        mContext = context;
    }
    public int addReviews(Vector<ContentValues> reviews) {


        ///TO DOOOOO

        return  1;
    }


    @Override
    protected HashMap<String, ArrayList<HashMap<String, String>>> doInBackground(ArrayList<String>... movieIds) {

        ArrayList<String> moviesIds = movieIds[0];
//            Toast.makeText(getActivity(), movieId[0], Toast.LENGTH_LONG).show();


        for (String id : moviesIds) {
            Log.v(LOG_TAG, "String PARAMETER " + id);
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String reviewsJsonStr = null;


            try {
                String VIDEO_BASE_URL =
                        "http://api.themoviedb.org/3/movie/" + id + "/reviews?";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(VIDEO_BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, "***")
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    // return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    // return null;
                }
                reviewsJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("PopularMoviesFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                // return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PopularMoviesFragment", "Error closing stream", e);
                    }
                }
            }
            try {
                //Log.v(LOG_TAG, "ReviewsString" + reviewsJsonStr);
                moviesReviews.put(id, getMovieReviewsFromJson(reviewsJsonStr));
                // return getMovieReviewsFromJson(reviewsJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }
        return moviesReviews;
    }

    @Override
    protected void onPostExecute(HashMap<String, ArrayList<HashMap<String, String>>> myMoviesReviews) {
        super.onPostExecute(myMoviesReviews);
        //addReviews(myMoviesReviews);

    }

    private ArrayList<HashMap<String, String>> getMovieReviewsFromJson(String reviewsJsonStr)
            throws JSONException {

        final String MOVIE_ID = "movie_id";
        final String REVIEWS_RESULTS = "results";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";
        ArrayList<HashMap<String, String>> reviewsList = new ArrayList<>();


        JSONObject movieReviewsJson = new JSONObject(reviewsJsonStr);
        JSONArray reviewsArray = movieReviewsJson.getJSONArray(REVIEWS_RESULTS);
        int movieId = movieReviewsJson.getInt("id");
        Vector<ContentValues> cReviewsVector = new Vector<ContentValues>(reviewsArray.length());

        for (int i = 0; i < reviewsArray.length(); i++) {
            HashMap<String, String> reviews = new HashMap();

            ContentValues reviewValues = new ContentValues();
            reviews.put(MOVIE_ID, String.valueOf(movieId));
            reviews.put(REVIEW_AUTHOR, reviewsArray.getJSONObject(i).getString(REVIEW_AUTHOR));
            reviews.put(REVIEW_CONTENT, reviewsArray.getJSONObject(i).getString(REVIEW_CONTENT));
            reviewsList.add(reviews);

            reviewValues.put(MOVIE_ID, movieId);
            reviewValues.put(REVIEW_AUTHOR, reviewsArray.getJSONObject(i).getString(REVIEW_AUTHOR));
            reviewValues.put(REVIEW_CONTENT, reviewsArray.getJSONObject(i).getString(REVIEW_CONTENT));
            cReviewsVector.add(reviewValues);
            //  Log.v(LOG_TAG, "ReviewContent" + reviewsJson.getString(REVIEW_CONTENT));
        }

        int inserted = 0;
        // add to database
        if ( cReviewsVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cReviewsVector.size()];
            cReviewsVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(MovieContract.ReviewsEntry.CONTENT_URI, cvArray);
        }

        Log.d(LOG_TAG, "FetchReviewsForMovieTask Complete. " + inserted + " Inserted");

        return reviewsList;
    }

}
