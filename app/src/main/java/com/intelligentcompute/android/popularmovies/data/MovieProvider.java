package com.intelligentcompute.android.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class MovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIES = 100;
    static final int MOVIES_BY_POPULARITY = 101;
    static final int MOVIES_BY_VOTE_AVERAGE = 102;
    static final int REVIEWS = 200;
    static final int REVIEWS_FOR_MOVIE = 201;
    static final int VIDEOS = 300;
    static final int VIDEOS_FOR_MOVIE = 301;

   /* static final int MOVIE_WITH_REVIEWS = 111;
    static final int MOVIE_WITH_REVIEWS_AND_VIDEOS =112; */

    private static final SQLiteQueryBuilder sReviewsForMovieIdQueryBuilder;

    static {
        sReviewsForMovieIdQueryBuilder = new SQLiteQueryBuilder();
        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sReviewsForMovieIdQueryBuilder.setTables(
                MovieContract.ReviewsEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieEntry.TABLE_NAME +
                        " ON " + MovieContract.ReviewsEntry.TABLE_NAME +
                        "." + MovieContract.ReviewsEntry.COLUMN_MOVIE_KEY +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID);
    }

    private static final SQLiteQueryBuilder sVideosByMovieIdQueryBuilder;

    static {
        sVideosByMovieIdQueryBuilder = new SQLiteQueryBuilder();
    }

    private static final SQLiteQueryBuilder sMoviesQueryBuilder;

    static {
        sMoviesQueryBuilder = new SQLiteQueryBuilder();
    }

    //movies.sort_by.popularity.desc
    private static final String sMoviesByPopularitySelection =
            MovieContract.MovieEntry.TABLE_NAME;

    //movies.sort_by.vote_average.desc

    //reviews.movie_id = ?
    private static final String sReviewsByMovieIdSelection =
            MovieContract.ReviewsEntry.TABLE_NAME+
                    "." + MovieContract.ReviewsEntry.COLUMN_MOVIE_KEY + " = ? ";

    //videos.movie_id = ?
    private static final String sVideosByMovieIdSelection =
            MovieContract.VideoEntry.TABLE_NAME+
                    "." + MovieContract.VideoEntry.COLUMN_MOVIE_KEY + " = ? ";


    private Cursor getReviewsForMovie(Uri uri, String[] projection, String sortOrder) {
        String movieIdFromUri = MovieContract.ReviewsEntry.getMovieIdFromUri(uri);
       // long startDate = WeatherContract.WeatherEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sReviewsByMovieIdSelection;
        selectionArgs = new String[]{movieIdFromUri};



        return sReviewsForMovieIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getVideosForMovie(Uri uri, String[] projection, String sortOrder) {
        String movieIdFromUri = MovieContract.VideoEntry.getMovieIdFromUri(uri);
        // long startDate = WeatherContract.WeatherEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sVideosByMovieIdSelection;
        selectionArgs = new String[]{movieIdFromUri};



        return sVideosByMovieIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMoviesSorted(
            Uri uri, String[] projection, String sortOrder) {

        return sMoviesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the WEATHER, WEATHER_WITH_LOCATION, WEATHER_WITH_LOCATION_AND_DATE,
        and LOCATION integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_VIDEO, VIDEOS);
        matcher.addURI(authority, MovieContract.PATH_VIDEO + "/*", VIDEOS_FOR_MOVIE);

        matcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEWS);
        matcher.addURI(authority, MovieContract.PATH_REVIEW + "/*", REVIEWS_FOR_MOVIE);

        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIES);
        return matcher;
    }

    /*
        Students: We've coded this for you.  We just create a new WeatherDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.
     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
//            case WEATHER_WITH_LOCATION_AND_DATE:
//            case WEATHER_WITH_LOCATION:
            case MOVIES:
                return  MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIES_BY_VOTE_AVERAGE:
                return  MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIES_BY_POPULARITY:
                return  MovieContract.MovieEntry.CONTENT_TYPE;
            case REVIEWS:
                return MovieContract.ReviewsEntry.CONTENT_TYPE;
            case REVIEWS_FOR_MOVIE:
                return MovieContract.ReviewsEntry.CONTENT_TYPE;
            case VIDEOS:
                return MovieContract.VideoEntry.CONTENT_TYPE;
            case VIDEOS_FOR_MOVIE:
                return MovieContract.VideoEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "movies/*"
            case MOVIES_BY_POPULARITY:
            {
                retCursor = getMoviesSorted(uri, projection, sortOrder);
                break;
            }
            // "weather/*"
            case MOVIES_BY_VOTE_AVERAGE: {
                retCursor = getMoviesSorted(uri, projection, sortOrder);
                break;
            }
            case VIDEOS_FOR_MOVIE:
            {
                retCursor = getVideosForMovie(uri, projection, sortOrder);
                break;
            }
            // "reviews/*"
            case REVIEWS_FOR_MOVIE: {
                retCursor = getReviewsForMovie(uri, projection, sortOrder);
                break;
            }
            // "MOVIES"
            case MOVIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "videos"
            case VIDEOS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                    MovieContract.VideoEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );
                break;
            }
            // "reviews"
            case REVIEWS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case REVIEWS: {
                long _id = -1;
                try {
                    _id = db.insertOrThrow(MovieContract.ReviewsEntry.TABLE_NAME, null, values);
                }
                catch (SQLiteConstraintException e) {
                    Log.e("Error inserting reviews", e.toString());
                }
                if ( _id > 0 )
                    returnUri = MovieContract.ReviewsEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case VIDEOS: {
                long _id = db.insert(MovieContract.VideoEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.VideoEntry.buildVideoUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MOVIES: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rows_deleted;

        if (null==selection) selection = "1";
        switch (match) {
            case REVIEWS: {
                rows_deleted = db.delete(MovieContract.ReviewsEntry.TABLE_NAME, selection, selectionArgs);

                break;
            }
            case VIDEOS: {
                rows_deleted = db.delete(MovieContract.VideoEntry.TABLE_NAME, selection, selectionArgs);

                break;
            }
            case MOVIES: {
                rows_deleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
       /* if (rows_deleted!=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }*/
        getContext().getContentResolver().notifyChange(uri, null);
        return rows_deleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rows_updated;

        if (null==selection) selection = "1";
        switch (match) {
            case REVIEWS: {
                rows_updated = db.update(MovieContract.ReviewsEntry.TABLE_NAME, values, selection, selectionArgs);

                break;
            }
            case VIDEOS: {
                rows_updated = db.update(MovieContract.VideoEntry.TABLE_NAME, values, selection, selectionArgs);

                break;
            }
            case MOVIES: {
                rows_updated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rows_updated!=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows_updated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case VIDEOS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = 0;
                        try {
                            _id = db.insertOrThrow(MovieContract.VideoEntry.TABLE_NAME, null, value);
                        }
                        catch (SQLiteConstraintException e) {
                            Log.e("Error inserting reviews", e.toString());
                        }
                       // long _id = db.insert(MovieContract.VideoEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case REVIEWS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = 0;
                        try {
                            _id = db.insertOrThrow(MovieContract.ReviewsEntry.TABLE_NAME, null, value);
                        }
                        catch (SQLiteConstraintException e) {
                            Log.e("Error inserting reviews", e.toString());
                        }
                        //long _id = db.insert(MovieContract.ReviewsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
