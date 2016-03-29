package com.intelligentcompute.android.popularmovies.tasks;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.intelligentcompute.android.popularmovies.ImageAdapter;
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

/**
 * Created by katya on 25/03/2016.
 */
public class FetchPopularMoviesTask extends AsyncTask<String, Void, ArrayList<String>> {

    private final String LOG_TAG = FetchPopularMoviesTask.class.getSimpleName();

    private ImageAdapter mMoviePostersAdapter;
    private final Context mContext;
    private ArrayList<ArrayList<String>> popularMovies= new ArrayList<>();
    private ArrayList<String> movieIDsList = new ArrayList<String>();
    private HashMap<String, ArrayList<HashMap<String, String>>> moviesReviews;  //HashMap<String, ArrayList<HashMap<String, String>>>
    private HashMap<String, ArrayList<VideoForListFragmentEntry>> moviesVideos;


    public FetchPopularMoviesTask(Context context) {
        mContext = context;
    }

    public FetchPopularMoviesTask(Context context, ImageAdapter moviePostersAdapter) {
        mContext = context;
        mMoviePostersAdapter = moviePostersAdapter;
       // moviesReviews = new HashMap<>();
       // moviesVideos = new HashMap<>();
    }
    @Override
    protected ArrayList<String> doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieJsonStr = null;


        // Will contain the raw JSON response as a string.
        String sort_by = "popularity.desc";
        String certification_country = "UK";

        try {
            // Construct the URL for the moviebd query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // URL url = new URL("https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=e257613f461ed40c956dc1464fb16313");

            final String MOVIE_BASE_URL =
                    "https://api.themoviedb.org/3/discover/movie?";
            final String CERTIFICATION_PARAM = "certification_country";
            final String SORTING_PARAM = "sort_by";
            final String API_KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(SORTING_PARAM, params[0])
                            //.appendQueryParameter(CERTIFICATION_PARAM, certification_country)
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
            movieJsonStr = buffer.toString();
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
            return getMovieDataFromJson(movieJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        ;
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
        if (result != null) {
            //mMoviePostersAdapter.clear();
            mMoviePostersAdapter.updateThumbnails(result);
            Log.i(LOG_TAG, " REACHED OnPostExecute" + result.get(0) + result.get(1));
            FetchReviewsForMovieTask reviewsTask = new FetchReviewsForMovieTask(mContext);
            reviewsTask.execute(movieIDsList);

            FetchVideosForMovieTask videosTask = new FetchVideosForMovieTask(mContext);
            videosTask.execute(movieIDsList);
           /* try {
                 moviesReviews = reviewsTask.execute(movieIDsList).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            // moviesReviews = reviewsTask.getMoviesReviews();
             videosTask = new FetchVideosForMovieTask(mContext);
            try {
                moviesVideos = videosTask.execute(movieIDsList).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }*/


        }
        Log.i(LOG_TAG, " REACHED OnPostExecute");
    }

    private ArrayList<String> getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String MOVIE_LIST = "results";
        final String MOVIE_POSTER_PATH = "poster_path";
        final String MOVIE_TITLE = "title";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String MOVIE_POPULARITY = "popularity";
        final String MOVIE_VOTE_AVERAGE = "vote_average";
        final String MOVIE_OVERVIEW = "overview";
        final String MOVIE_ID = "id";

        JSONObject moviesJson = new JSONObject(movieJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(MOVIE_LIST);

        ArrayList<String> resultStrs = new ArrayList<String>();
        for (int i = 0; i < moviesArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String title;
            String overview;
            String releaseDate;
            String posterPath;
            String popularity;
            String voteAverage;
            int movieId;

            // Get the JSON object representing the day

            try{
                JSONObject movie = moviesArray.getJSONObject(i);
                title = movie.getString(MOVIE_TITLE);
                Log.v(LOG_TAG, "Movie Title" + title);
                overview = movie.getString(MOVIE_OVERVIEW);
                releaseDate = movie.getString(MOVIE_RELEASE_DATE);
                posterPath = movie.getString(MOVIE_POSTER_PATH);
                voteAverage = movie.getString(MOVIE_VOTE_AVERAGE);
                popularity = movie.getString(MOVIE_POPULARITY);
                movieId = movie.getInt(MOVIE_ID);

                int myMovieId = addMovie(movieId, title, releaseDate, posterPath, Double.parseDouble(popularity), Double.parseDouble(voteAverage), overview);

                ArrayList<String> movieDetailsList = new ArrayList<>();
                movieDetailsList.add(String.valueOf(movieId));
                movieDetailsList.add(title);
                movieDetailsList.add(posterPath);
                movieDetailsList.add(releaseDate);
                movieDetailsList.add(voteAverage);
                movieDetailsList.add(overview);

                popularMovies.add(movieDetailsList);
                movieIDsList.add(String.valueOf(movieId));

                resultStrs.add("http://image.tmdb.org/t/p/w300/" + posterPath + "&api_key=***");

            }
            catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

        }

        for (String s : resultStrs) {
            Log.v(LOG_TAG, "Movie entry: " + s);
        }
        return resultStrs;

    }

    public ArrayList<ArrayList<String>> getPopularMovies() {
        return popularMovies;
    }

    public int addMovie(int myMovieId, String movieTitle, String releaseDate, String posterPath, double popularity,
                         double voteAverage, String overview) {
        int movieId;

        // First, check if the movie entry exists in the db
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry.COLUMN_MOVIE_ID},
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{String.valueOf(myMovieId)},
                null);

        if (movieCursor.moveToFirst()) {
            int movieIdIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            movieId = movieCursor.getInt(movieIdIndex);
        } else {
            // Now that the content provider is set up, inserting rows of data is pretty simple.
            // First create a ContentValues object to hold the data you want to insert.
            ContentValues movieValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, myMovieId);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movieTitle);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, overview);

            // Finally, insert location data into the database.
            Uri insertedUri = mContext.getContentResolver().insert(
                    MovieContract.MovieEntry.CONTENT_URI,
                    movieValues
            );

            // The resulting URI contains the ID for the row.  Extract the movieId from the Uri.
            movieId = (int) ContentUris.parseId(insertedUri);
        }

        movieCursor.close();
        // Wait, that worked?  Yes!
        return movieId;
    }

}