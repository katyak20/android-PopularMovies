package com.intelligentcompute.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.intelligentcompute.android.popularmovies.common.view.SlidingTabLayout;
import com.intelligentcompute.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by katya on 05/04/2016.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID_MOVIE_DETAILS_CURSOR = 0;
    private static final int LOADER_ID_REVIEWS_CURSOR = 1;
    private static final int LOADER_ID_VIDEOS_CURSOR = 2;

    static final int ITEMS = 2;

    private Cursor cursor1 = null;
    private Cursor cursor2 = null;
    private Cursor cursor3 = null;

    private MyAdapter mAdapter;
    private ViewPager mPager;
    private SlidingTabLayout mSlidingTabLayout;

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
   // static final String DETAIL_URI = "URI";
    static final String DETAIL_MOVIE_ID = "movie_id";

    private static final String POPULAR_MOVIES_SHARE_HASHTAG = " #PopularMoviesApp";

    private View rootView;
    private ShareActionProvider mShareActionProvider;
    private String mMovieId ="1771";

    private Uri mMovieUri = MovieContract.MovieEntry.buildSelectedMovieUri(mMovieId);
    private Uri mReviewsUri = MovieContract.ReviewsEntry.buildReviewsForMovie(mMovieId);
    private Uri mVideosUri = MovieContract.VideoEntry.buildVideosForMovie(mMovieId);

    private HashMap<String, String> movieDetails = new HashMap<>();
    private ArrayList<HashMap<String, String>> reviewsList = new ArrayList<>();
    private ArrayList<VideoForListFragmentEntry> videoEntries = new ArrayList<VideoForListFragmentEntry>();

    ArrayList<HashMap<String, String>> videosList = new ArrayList<>();

    private final String MOVIE_ID = "movie_id";
    private final String MOVIE_TITLE = "movie_title";
    private final String MOVIE_RELEASE_DATE = "release_date";
    private final String VOTE_AVERAGE = "vote_average";
    private final String MOVIE_DESCRIPTION = "movie_overview";
    private final String POPULARITY ="popularity";
    private final String POSTER_PATH = "poster_path";
    private TextView movieTitle;
    private TextView movieReleaseDate;
    private TextView  movieVoteAverage;
    private TextView moviePlotSynopsis;
    private ImageView posterImage;

    private static final String[] MOVIE_DETAILS_COLUMNS = {
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

    private static final String[] MOVIE_REVIEWS_COLUMNS = {
            MovieContract.ReviewsEntry.TABLE_NAME + "." + MovieContract.ReviewsEntry._ID,
            MovieContract.ReviewsEntry.COLUMN_MOVIE_KEY,
            MovieContract.ReviewsEntry.COLUMN_AUTHOR,
            MovieContract.ReviewsEntry.COLUMN_CONTENT
    };

    public static final int COL_REVIEW_ID = 0;
    public static final int COL_KEY = 1;
    public static final int COL_AUTHOR = 2;
    public static final int COL_CONTENT = 3;

    private static final String[] MOVIE_VIDEOS_COLUMNS = {
            MovieContract.VideoEntry.TABLE_NAME + "." + MovieContract.VideoEntry._ID,
            MovieContract.VideoEntry.COLUMN_MOVIE_KEY,
            MovieContract.VideoEntry.COLUMN_TRAILER_PATH,
            MovieContract.VideoEntry.COLUMN_DESCRIPTION
    };

    public static final int COL_VIDEO_ID = 0;
    public static final int COL_MOVIE_KEY = 1;
    public static final int COL_TRAILER_PATH = 2;
    public static final int COL_DESCRIPTION = 3;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    private CursorLoader getMovieDetailsCursorLoader() {
       // Uri uri = MovieContract.MovieEntry.buildSelectedMovieUri(newMovieId);
        String[] select = null;
        String where = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?";
        String[] whereArgs = new String[]{mMovieId};
        String sortOrder = null;
        return new CursorLoader(getActivity(), mMovieUri, MOVIE_DETAILS_COLUMNS, where, whereArgs, null);

    }

    private CursorLoader getReviewsCursorLoader() {
        //Uri uri = MovieContract.ReviewsEntry.buildReviewsForMovie(newMovieId);
        String[] select = null;
        String where = MovieContract.ReviewsEntry.COLUMN_MOVIE_KEY + " = ?";
        String[] whereArgs = new String[]{mMovieId};
        String sortOrder = null;
        return new CursorLoader(getActivity(), mReviewsUri, MOVIE_REVIEWS_COLUMNS, where, whereArgs, null);

    }

    private CursorLoader getVideosCursorLoader() {
        //Uri uri = MovieContract.VideoEntry.buildVideosForMovie(newMovieId);
        String[] select = null;
        String where = MovieContract.VideoEntry.COLUMN_MOVIE_KEY + " = ?";
        String[] whereArgs = new String[]{mMovieId};
        String sortOrder = null;
        return new CursorLoader(getActivity(), mVideosUri, MOVIE_VIDEOS_COLUMNS, where, whereArgs, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader" + mMovieId);
        //Intent intent = getActivity().getIntent();
       /* if (intent == null || intent.getData() == null) {
            return null;
        }*/
        if ( null != mMovieId ) {
            switch (id) {
                case LOADER_ID_MOVIE_DETAILS_CURSOR:
                    return getMovieDetailsCursorLoader();
                case LOADER_ID_REVIEWS_CURSOR:
                    return getReviewsCursorLoader();
                case LOADER_ID_VIDEOS_CURSOR:
                    return getVideosCursorLoader();
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";

        switch (loader.getId()) {
            case LOADER_ID_MOVIE_DETAILS_CURSOR:
                cursor1 = data;
                while ( cursor1.moveToNext()) {
                    movieDetails.put(MOVIE_ID, cursor1.getString(1));
                    movieDetails.put(MOVIE_TITLE, cursor1.getString(2));
                    movieDetails.put(MOVIE_RELEASE_DATE, cursor1.getString(3));
                    movieDetails.put(VOTE_AVERAGE, cursor1.getString(4));
                    movieDetails.put(POPULARITY, cursor1.getString(5));
                    movieDetails.put(MOVIE_DESCRIPTION, cursor1.getString(7));
                    String posterPathUrl = "http://image.tmdb.org/t/p/w300"
                            + cursor1.getString(6) + "&api_key=e257613f461ed40c956dc1464fb16313";
                    movieDetails.put(POSTER_PATH, posterPathUrl);



                    movieTitle.setText(movieDetails.get(MOVIE_TITLE));
                    ((TextView) rootView.findViewById(R.id.movieReleaseDate)).setText(movieDetails.get(MOVIE_RELEASE_DATE));
                    ((TextView) rootView.findViewById(R.id.movieVoteAverage)).setText(movieDetails.get(VOTE_AVERAGE));
                    ((TextView) rootView.findViewById(R.id.moviePlotSynopsis)).setText(movieDetails.get(MOVIE_DESCRIPTION));
                    posterImage = (ImageView) rootView.findViewById(R.id.moviePoster);

                    Picasso.with(this.getContext()).load(posterPathUrl).into(posterImage);

                }
            case LOADER_ID_REVIEWS_CURSOR:
                cursor2 = data;
                while (cursor2.moveToNext()) {
                    HashMap<String, String> reviews = new HashMap();
                    reviews.put(REVIEW_AUTHOR, cursor2.getString(2));
                    reviews.put(REVIEW_CONTENT, cursor2.getString(3));
                    reviewsList.add(reviews);
                }
                break;
            case LOADER_ID_VIDEOS_CURSOR:
                cursor3 = data;
                while (cursor3.moveToNext()) {

                    videoEntries.add(new VideoForListFragmentEntry(cursor3.getString(3), cursor3.getString(2)));

                }
                break;
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovieId = arguments.getString(DetailFragment.DETAIL_MOVIE_ID);
            mVideosUri = MovieContract.VideoEntry.buildVideosForMovie(mMovieId);
            mReviewsUri = MovieContract.ReviewsEntry.buildReviewsForMovie(mMovieId);
            mMovieUri =MovieContract.MovieEntry.buildSelectedMovieUri(mMovieId);
        }
        Log.v(LOG_TAG," On Attach" + mMovieId);
        LoaderManager lm = getActivity().getSupportLoaderManager();
        lm.restartLoader(LOADER_ID_MOVIE_DETAILS_CURSOR, null, this);
        lm.restartLoader(LOADER_ID_REVIEWS_CURSOR, null, this);
        lm.restartLoader(LOADER_ID_VIDEOS_CURSOR, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_pager, container, false);

        mAdapter = new MyAdapter(getActivity().getSupportFragmentManager());
        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mSlidingTabLayout = (SlidingTabLayout) rootView.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mPager);

        ((TextView) rootView.findViewById(R.id.movieTitle)).setText(String.valueOf(mMovieId));

        movieTitle = (TextView) rootView.findViewById(R.id.movieTitle);
        movieReleaseDate = (TextView) rootView.findViewById(R.id.movieReleaseDate);
        movieVoteAverage = (TextView) rootView.findViewById(R.id.movieVoteAverage);
        moviePlotSynopsis = (TextView) rootView.findViewById(R.id.moviePlotSynopsis);

        posterImage = (ImageView) rootView.findViewById(R.id.moviePoster);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {

            mMovieId = arguments.getString(DetailFragment.DETAIL_MOVIE_ID);
            mVideosUri = MovieContract.VideoEntry.buildVideosForMovie(mMovieId);
            mReviewsUri = MovieContract.ReviewsEntry.buildReviewsForMovie(mMovieId);
            mMovieUri =MovieContract.MovieEntry.buildSelectedMovieUri(mMovieId);
            Log.d(LOG_TAG, "From onActivityCreated" + mMovieId);
        }
        Log.d(LOG_TAG, "From onActivityCreated BEFORE initializing Loaders" + movieDetails.get(MOVIE_TITLE));
        LoaderManager lm = getActivity().getSupportLoaderManager();
        lm.initLoader(LOADER_ID_MOVIE_DETAILS_CURSOR, null, this);
        lm.initLoader(LOADER_ID_REVIEWS_CURSOR, null, this);
        lm.initLoader(LOADER_ID_VIDEOS_CURSOR, null, this);
       // super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG, "From onActivityCreated AFTER initializing Loaders" + movieDetails.get(MOVIE_TITLE));

    }

    public class MyAdapter extends FragmentStatePagerAdapter {
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detail, menu);
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
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
