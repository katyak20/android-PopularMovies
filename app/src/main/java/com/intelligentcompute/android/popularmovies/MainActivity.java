package com.intelligentcompute.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

    private String mSortingOrder;
    private final String POPULARMOVIESFRAGMENT_TAG = "PMFTAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PopularMoviesFragment(), POPULARMOVIESFRAGMENT_TAG)
                    .commit();
        }

        SharedPreferences  prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSortingOrder = getPreferredSortingOrder(this);
    }

    private String getPreferredSortingOrder(Context c) {
        SharedPreferences  prefs = PreferenceManager.getDefaultSharedPreferences(c);
        String mSortingOrder = prefs.getString(getString(R.string.pref_sorting_key),
                getString(R.string.pref_sorting_default));
        return mSortingOrder;
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
        String sortingOrder = getPreferredSortingOrder(this);
        // update the location in our second pane using the fragment manager
        if (sortingOrder != null && !sortingOrder.equals(mSortingOrder)) {
            PopularMoviesFragment ff = (PopularMoviesFragment)getSupportFragmentManager().findFragmentByTag(POPULARMOVIESFRAGMENT_TAG);
            if ( null != ff ) {
                ff.onSortingOrderChanged();
            }
            mSortingOrder = sortingOrder;
        }
    }


}

