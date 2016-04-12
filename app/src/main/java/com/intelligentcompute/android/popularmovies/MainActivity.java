package com.intelligentcompute.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity implements PopularMoviesFragment.Callback {

    private String mSortingOrder;
   // private String firstMovieId;
    private final String POPULARMOVIESFRAGMENT_TAG = "PMFTAG";
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSortingOrder = Utility.getPreferredSortingOrder(this);
        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PopularMoviesFragment(), POPULARMOVIESFRAGMENT_TAG)
                    .commit();*/

        if (findViewById(R.id.popular_movies_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                Bundle arguments = new Bundle();
                arguments.putString(DetailFragment.DETAIL_MOVIE_ID, "11");
                DetailFragment fragment = new DetailFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.popular_movies_detail_container, fragment, DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortingOrder = Utility.getPreferredSortingOrder(this);
        // update the location in our second pane using the fragment manager
        if (sortingOrder != null && !sortingOrder.equals(mSortingOrder)) {
            PopularMoviesFragment ff = (PopularMoviesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_popular_movies);
            if (null != ff) {
                ff.onSortingOrderChanged();
            }
            DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (null != df) {
                //df.onSortingOrderChanged(mSortingOrder);
                //df.onMovieChanged("131634");

            }
            mSortingOrder = sortingOrder;
        }
    }

    @Override
    public void onItemSelected(String movieId) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            //args.putParcelable(DetailFragment.DETAIL_URI, contentUri);
            args.putString(DetailFragment.DETAIL_MOVIE_ID, movieId);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.popular_movies_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class).putExtra(DetailFragment.DETAIL_MOVIE_ID, movieId);
                    //.setData(contentUri);
            startActivity(intent);
        }
    }

}




