package com.intelligentcompute.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;

import com.intelligentcompute.android.popularmovies.data.MovieContract;
import com.intelligentcompute.android.popularmovies.tasks.FetchPopularMoviesTask;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by katya on 08/02/2016.
 */
public class PopularMoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private PopularMoviesAdapter mMoviePostersAdapter;
    private ArrayList<ArrayList<String>> popularMovies;
    private ArrayList<String> movieIDsList;
    private HashMap<String, ArrayList<HashMap<String, String>>> moviesReviews;
    private HashMap<String, ArrayList<VideoForListFragmentEntry>> moviesVideos;

    private static final int POPULAR_MOVIES_LOADER = 0;
    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] MOVIE_COLUMNS = {

            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW

    };

    // These indices are tied to MOVIE_COLUMNS.
    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_TITLE = 2;
    static final int COL_RELEASE_DATE = 3;
    static final int COL_VOTE_AVERAGE = 4;
    static final int COL_POPYLARITY = 5;
    static final int COL_POSTER_PATH = 6;
    static final int COL_MOVIE_OVERVIEW = 7;

    public PopularMoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        popularMovies = new ArrayList<>();
        movieIDsList = new ArrayList<String>();
        moviesReviews = new HashMap<>();
        moviesVideos = new HashMap<>();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(POPULAR_MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.favouritemoviesfragment_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateMoviesList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) view.findViewById(R.id.gridview);
/*
        String sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        //Uri popularMoviesUri = MovieContract.MovieEntry.buildMovieUri();

        Cursor cursor = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null, null, null, sortOrder);

        //mMoviePostersAdapter = new PopularMoviesAdapter(getActivity(), cursor, 0);*/
        mMoviePostersAdapter = new PopularMoviesAdapter(getActivity(), null, 0);

        gridView.setAdapter(mMoviePostersAdapter); // uses the view to get the context instead of getActivity().


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            // CursorAdapter returns a cursor at the correct position for getItem(), or null
            // if it cannot seek to that position.

                ArrayList<String> movieDetails = new ArrayList<String>();
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    int movieId = cursor.getInt(COL_MOVIE_ID);
                    movieDetails.add(String.valueOf(movieId));
                    movieDetails.add(cursor.getString(COL_MOVIE_TITLE));
                    movieDetails.add(cursor.getString(COL_RELEASE_DATE));
                    movieDetails.add(cursor.getString(COL_VOTE_AVERAGE));
                    movieDetails.add(cursor.getString(COL_POPYLARITY));
                    movieDetails.add(cursor.getString(COL_POSTER_PATH));
                    movieDetails.add(cursor.getString(COL_MOVIE_OVERVIEW));
                    Bundle mBundle = new Bundle();
                    mBundle.putStringArrayList("movieDetails", movieDetails);
                    Intent intent = new Intent(getActivity(), DetailActivityWithSlidingTabs.class);
                    intent.putExtras(mBundle);
                    startActivity(intent);
                }
            }
        });

        return view;
    }
    void onSortingOrderChanged( ) {
        updateMoviesList();
        getLoaderManager().restartLoader(POPULAR_MOVIES_LOADER, null, this);
    }
    private void updateMoviesList() {
        FetchPopularMoviesTask moviesTask = new FetchPopularMoviesTask(getActivity());
        // moviesTask.execute("vote_average.desc");
      //  popularMovies = moviesTask.getPopularMovies();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sorting_order = prefs.getString(getString(R.string.pref_sorting_key),
                getString(R.string.pref_sorting_default));
        //sorting_order = "popularity";
        moviesTask.execute(sorting_order + ".desc");
    }

    @Override
    public void onStart() {
        super.onStart();

        updateMoviesList();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
       // String locationSetting = Utility.getPreferredLocation(getActivity());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sorting_order_column = prefs.getString(getString(R.string.pref_sorting_key),
                getString(R.string.pref_sorting_default));
        String sortOrder = sorting_order_column + " DESC";
        return new CursorLoader(getActivity(),MovieContract.MovieEntry.CONTENT_URI,
                null, null, null, sortOrder);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMoviePostersAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviePostersAdapter.swapCursor(null);
    }

}
