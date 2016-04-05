package com.intelligentcompute.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;

import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.intelligentcompute.android.popularmovies.common.view.SlidingTabLayout;
import com.intelligentcompute.android.popularmovies.data.MovieContract;
import com.intelligentcompute.android.popularmovies.data.MovieContract.ReviewsEntry;
import com.intelligentcompute.android.popularmovies.data.MovieContract.VideoEntry;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class DetailActivityWithSlidingTabs extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int LOADER_ID_REVIEWS_CURSOR = 0;
    private static final int LOADER_ID_VIDEOS_CURSOR = 1;

    static final int ITEMS = 2;

    private Cursor cursor1 = null;
    private Cursor cursor2 = null;

    private MyAdapter mAdapter;
    private ViewPager mPager;
    private SlidingTabLayout mSlidingTabLayout;

    private static final String LOG_TAG = DetailActivityWithSlidingTabs.class.getSimpleName();

    private static final String POPULAR_MOVIES_SHARE_HASHTAG = " #PopularMoviesApp";

    private ShareActionProvider mShareActionProvider;
    private String mMovieId;
    private Uri mUri;
    private ArrayList<HashMap<String, String>> reviewsList = new ArrayList<>();
    private ArrayList<VideoForListFragmentEntry> videoEntries = new ArrayList<VideoForListFragmentEntry>();

    ArrayList<HashMap<String, String>> videosList = new ArrayList<>();

    private static final String[] MOVIE_REVIEWS_COLUMNS = {
            ReviewsEntry.TABLE_NAME + "." + ReviewsEntry._ID,
            ReviewsEntry.COLUMN_MOVIE_KEY,
            ReviewsEntry.COLUMN_AUTHOR,
            ReviewsEntry.COLUMN_CONTENT
    };

    public static final int COL_REVIEW_ID = 0;
    public static final int COL_KEY = 1;
    public static final int COL_AUTHOR = 2;
    public static final int COL_CONTENT = 3;

    private static final String[] MOVIE_VIDEOS_COLUMNS = {
            VideoEntry.TABLE_NAME + "." + VideoEntry._ID,
            VideoEntry.COLUMN_MOVIE_KEY,
            VideoEntry.COLUMN_TRAILER_PATH,
            VideoEntry.COLUMN_DESCRIPTION
    };

    public static final int COL_VIDEO_ID = 0;
    public static final int COL_MOVIE_KEY = 1;
    public static final int COL_TRAILER_PATH = 2;
    public static final int COL_DESCRIPTION = 3;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private CursorLoader getReviewsCursorLoader() {
        Uri uri = ReviewsEntry.buildReviewsForMovie(mMovieId);
        String[] select = null;
        String where = ReviewsEntry.COLUMN_MOVIE_KEY + " = ?";
        String[] whereArgs = new String[]{mMovieId};
        String sortOrder = null;
        return new CursorLoader(this, uri, MOVIE_REVIEWS_COLUMNS, where, whereArgs, null);

    }

    private CursorLoader getVideosCursorLoader() {
        Uri uri = VideoEntry.buildVideosForMovie(mMovieId);
        String[] select = null;
        String where = VideoEntry.COLUMN_MOVIE_KEY + " = ?";
        String[] whereArgs = new String[]{mMovieId};
        String sortOrder = null;
        return new CursorLoader(this, uri, MOVIE_VIDEOS_COLUMNS, where, whereArgs, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID_REVIEWS_CURSOR:
                return getReviewsCursorLoader();
            case LOADER_ID_VIDEOS_CURSOR:
                return getVideosCursorLoader();
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";
        final String VIDEO_TRAILER_PATH = "trailer_path";
        final String VIDEO_DESCRIPTION = "description";
        switch (loader.getId()) {
            case LOADER_ID_REVIEWS_CURSOR:
                cursor1 = data;
                while (cursor1.moveToNext()) {
                    HashMap<String, String> reviews = new HashMap();
                    reviews.put(REVIEW_AUTHOR, cursor1.getString(2));
                    reviews.put(REVIEW_CONTENT, cursor1.getString(3));
                    reviewsList.add(reviews);
                }
                break;
            case LOADER_ID_VIDEOS_CURSOR:
                cursor2 = data;
                while (cursor2.moveToNext()) {

                    videoEntries.add(new VideoForListFragmentEntry(cursor2.getString(3), cursor2.getString(2)));

                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);
        mAdapter = new MyAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mPager);
        String mMovieStr;
        ArrayList<String> movieDetails;
        Intent intent = getIntent();
        Bundle myBundle = intent.getExtras();
        if (intent != null && intent.hasExtra("movieDetails")) {
            movieDetails = myBundle.getStringArrayList("movieDetails");
            mMovieId = movieDetails.get(0);
            ((TextView) findViewById(R.id.movieTitle)).setText(movieDetails.get(1));
            ((TextView) findViewById(R.id.movieReleaseDate)).setText(movieDetails.get(2));
            ((TextView) findViewById(R.id.movieVoteAverage)).setText(movieDetails.get(3));
            ((TextView) findViewById(R.id.moviePopularity)).setText(movieDetails.get(4));
            ((TextView) findViewById(R.id.moviePlotSynopsis)).setText(movieDetails.get(6));

            ImageView imageView = (ImageView) findViewById(R.id.moviePoster);
            String posterPathUrl = "http://image.tmdb.org/t/p/w300/"
                    + movieDetails.get(5) + "&api_key=e257613f461ed40c956dc1464fb16313";
            Picasso.with(this).load(posterPathUrl).into(imageView);
        }


        LoaderManager lm = this.getSupportLoaderManager();
        lm.initLoader(LOADER_ID_REVIEWS_CURSOR, null, this);
        lm.initLoader(LOADER_ID_VIDEOS_CURSOR, null, this);

       /* Button button = (Button) findViewById(R.id.first);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mPager.setCurrentItem(0);
            }
        });
        button = (Button) findViewById(R.id.last);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mPager.setCurrentItem(ITEMS - 1);
            }
        });*/
    }

    public class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {

                case 0: // Fragment # 1 - This will show image
                    return VideoListFragment.init(videoEntries);
                default:// Fragment # 2-9 - Will show list
                    return ReviewsListFragment.init(reviewsList);
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "VIDEO TRAILERS";
                default:
                    return "FILM REVIEWS";
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if (mShareActionProvider != null) {
            // If onLoadFinished happens before this, we can go ahead and set the share intent now.
            if (mMovieId != null) {
                mShareActionProvider.setShareIntent(createShareMovieIntent());
            } else {
                Log.d(LOG_TAG, "Share Action Provider is null?");
            }
        }
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
        } else if (id == R.id.action_share) {
            startActivity(createShareMovieIntent());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mMovieId + POPULAR_MOVIES_SHARE_HASHTAG);
        return shareIntent;
    }
}
