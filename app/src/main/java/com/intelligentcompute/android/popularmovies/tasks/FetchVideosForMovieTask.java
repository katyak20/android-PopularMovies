package com.intelligentcompute.android.popularmovies.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.intelligentcompute.android.popularmovies.VideoForListFragmentEntry;
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
public class FetchVideosForMovieTask extends AsyncTask<ArrayList<String>, Void, HashMap<String,ArrayList<VideoForListFragmentEntry>>> {

    private final String LOG_TAG = FetchVideosForMovieTask.class.getSimpleName();

    private final Context mContext;
    private HashMap<String, ArrayList<VideoForListFragmentEntry>> moviesVideos = new HashMap<>();

    public FetchVideosForMovieTask(Context context) {
        mContext = context;
    }
    @Override
    protected HashMap<String, ArrayList<VideoForListFragmentEntry>> doInBackground(ArrayList<String>... movieIds) {

        ArrayList<String> moviesIds = movieIds[0];
//            Toast.makeText(getActivity(), movieId[0], Toast.LENGTH_LONG).show();
        for (String id : moviesIds) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String trailersJsonStr = null;

            try {
                String VIDEO_BASE_URL =
                        "http://api.themoviedb.org/3/movie/" + id + "/videos?";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(VIDEO_BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, "****")
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
                    return null;
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
                    return null;
                }
                trailersJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("PopularMoviesFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
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
                moviesVideos.put(id, getOfficialTrailerLinkFromJson(trailersJsonStr));

                //return getOfficialTrailerLinkFromJson(trailersJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }
        return moviesVideos;
    }

    private ArrayList<VideoForListFragmentEntry> getOfficialTrailerLinkFromJson(String movieTrailersJsonStr)
            throws JSONException {

        ArrayList<VideoForListFragmentEntry> videoEntries = new ArrayList<VideoForListFragmentEntry>();

        final String MOVIE_ID = "movie_id";
        final String YOUTUBE_RESULTS = "results";

        final String YOUTUBE_KEY = "key";
        final String YOUTUBE_NAME = "name";
        JSONObject movieTrailersJson = new JSONObject(movieTrailersJsonStr);
        JSONArray youTubeResultsArray = movieTrailersJson.getJSONArray(YOUTUBE_RESULTS);

        int movieId = movieTrailersJson.getInt("id");
        Vector<ContentValues> cVideosVector = new Vector<ContentValues>(youTubeResultsArray.length());

        for (int i = 0; i < youTubeResultsArray.length(); i++) {
            JSONObject officialTrailerJson = youTubeResultsArray.getJSONObject(i);

            videoEntries.add(new VideoForListFragmentEntry(officialTrailerJson.getString(YOUTUBE_NAME), officialTrailerJson.getString(YOUTUBE_KEY)));

            ContentValues videoValues = new ContentValues();
            videoValues.put(MOVIE_ID, movieId);
            videoValues.put("description", officialTrailerJson.getString(YOUTUBE_NAME));
            videoValues.put("video_trailer_path", officialTrailerJson.getString(YOUTUBE_KEY));
            cVideosVector.add(videoValues);

        }

        int inserted = 0;
        // add to database
        if ( cVideosVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVideosVector.size()];
            cVideosVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(MovieContract.VideoEntry.CONTENT_URI, cvArray);
        }

        Log.d(LOG_TAG, "FetchVideosForMovieTask Complete. " + inserted + " Inserted");


        return videoEntries;
    }


}