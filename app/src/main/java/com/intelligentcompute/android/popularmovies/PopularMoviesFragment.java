package com.intelligentcompute.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.ListView;

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

    private GridView mGridView;
    private int mPosition = GridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";

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

    public interface OnFirstMovieIdPass {
        public void onFirstMovieIdPass(String movieId);
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(String movieId);
    }

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

        mMoviePostersAdapter = new PopularMoviesAdapter(getActivity(), null, 0);

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mGridView = (GridView) view.findViewById(R.id.gridview);
        mGridView.setAdapter(mMoviePostersAdapter); // uses the view to get the context instead of getActivity().


        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.

                ArrayList<String> movieDetails = new ArrayList<String>();
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String sortingOrderSetting = Utility.getPreferredSortingOrder(getActivity());
                    int movieId = cursor.getInt(COL_MOVIE_ID);
                    movieDetails.add(String.valueOf(movieId));
                    ((Callback) getActivity()).onItemSelected(String.valueOf(movieId));

                }
                mPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return view;
    }

    void onSortingOrderChanged( ) {
        updateMoviesList();
        getLoaderManager().restartLoader(POPULAR_MOVIES_LOADER, null, this);
    }

    private void updateMoviesList() {
        FetchPopularMoviesTask moviesTask = new FetchPopularMoviesTask(getActivity());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sorting_order = prefs.getString(getString(R.string.pref_sorting_key),
                getString(R.string.pref_sorting_default));
        //sorting_order = "popularity";
        moviesTask.execute(sorting_order + ".desc");
       // getActivity()setFirstMovieId(getfirstMovieId());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();

        updateMoviesList();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        /*SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sorting_order_column = prefs.getString(getString(R.string.pref_sorting_key),
                getString(R.string.pref_sorting_default));*/
        String sorting_order_column = Utility.getPreferredSortingOrder(getActivity());
        String sortOrder = sorting_order_column + " DESC";
        return new CursorLoader(getActivity(),MovieContract.MovieEntry.CONTENT_URI,
                null, null, null, sortOrder);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMoviePostersAdapter.swapCursor(data);
        if (mPosition != GridView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviePostersAdapter.swapCursor(null);
    }



}
