package com.intelligentcompute.android.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.intelligentcompute.android.popularmovies.data.MovieContract.MovieEntry;
import com.intelligentcompute.android.popularmovies.data.MovieContract.ReviewsEntry;
import com.intelligentcompute.android.popularmovies.data.MovieContract.VideoEntry;
/*
    Note: This is not a complete set of tests of the Sunshine ContentProvider, but it does test
    that at least the basic functionality has been implemented correctly.

    Students: Uncomment the tests in this class as you implement the functionality in your
    ContentProvider to make sure that you've implemented things reasonably correctly.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.

       Students: Replace the calls to deleteAllRecordsFromDB with this one after you have written
       the delete functionality in the ContentProvider.
     */
   /* public void deleteAllRecordsFromDB() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(ReviewsEntry.TABLE_NAME, null, null);
        db.delete(VideoForListFragmentEntry.TABLE_NAME, null, null);
        db.delete(MovieEntry.TABLE_NAME, null, null);
        db.close();
    }*/

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                ReviewsEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                VideoEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                MovieEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                ReviewsEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Reviews table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                VideoEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Videos table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movie table during delete", 0, cursor.getCount());
        cursor.close();
    }

    /*
        Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
        you have implemented delete functionality there.
     */
    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /*
        This test checks to make sure that the content provider is registered correctly.
        Students: Uncomment this test to make sure you've correctly registered the WeatherProvider.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // WeatherProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: WeatherProvider registered with authority: " + providerInfo.authority +
                    " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: WeatherProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /*
            This test doesn't touch the database.  It verifies that the ContentProvider returns
            the correct type for each type of URI that it can handle.
            Students: Uncomment this test to verify that your implementation of GetType is
            functioning correctly.
         */
    public void testGetType() {
        String type;
        // content://com.intelligentcompute.android.popularmovies/reviews/
        String reviewsType = mContext.getContentResolver().getType(ReviewsEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.intelligentcompute.android.popularmovies/reviews
        assertEquals("Error: the ReviewsEntry CONTENT_URI should return ReviewsEntry.CONTENT_TYPE",
                ReviewsEntry.CONTENT_TYPE, reviewsType);

        String videoType = mContext.getContentResolver().getType(VideoEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.intelligentcompute.android.popularmovies/video
        assertEquals("Error: the VideosEntry CONTENT_URI should return VideosEntry.CONTENT_TYPE",
                VideoEntry.CONTENT_TYPE, videoType);

        String testMovie = "206647";

        // vnd.android.cursor.dir/com.intelligentcompute.android.popularmovies/reviews/206647
        reviewsType = mContext.getContentResolver().getType(
                ReviewsEntry.buildReviewsForMovie(testMovie));
        // vnd.android.cursor.dir/com.intelligentcompute.android.popularmovies/reviews
        assertEquals("Error: the ReviewsEntry CONTENT_URI FOR THE MOVIE should return ReviewsEntry.CONTENT_TYPE",
                ReviewsEntry.CONTENT_TYPE, reviewsType);

        // vnd.android.cursor.dir/com.intelligentcompute.android.popularmovies/videos/206647
        videoType = mContext.getContentResolver().getType(
                VideoEntry.buildVideosForMovie(testMovie));

        // vnd.android.cursor.dir/com.intelligentcompute.android.popularmovies/videos
        assertEquals("Error: the VideoForListFragmentEntry CONTENT_URI FOR THE MOVIE should return VideoForListFragmentEntry.CONTENT_TYPE",
                VideoEntry.CONTENT_TYPE, videoType);

        // vnd.android.cursor.dir/com.intelligentcompute.android.popularmovies/movies
        type = mContext.getContentResolver().getType(MovieEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.intelligentcompute.android.popularmovies/movies
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieEntry.CONTENT_TYPE, type);
    }


    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this test to see if the basic weather query functionality
        given in the ContentProvider is working correctly.
     */
    public void testBasicLMovieQueries() {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createJamesBondMovieValues();
        long movieRowId = TestUtilities.insertJamesBondMovieValues(mContext);

        // Test the basic content provider query
        Cursor locationCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicMovieQueries, movie query", locationCursor, testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: Location Query did not properly set NotificationUri",
                    locationCursor.getNotificationUri(), MovieEntry.CONTENT_URI);
        }
    }
    public void testBasicReviewsQuery() {
        String movieId = "206647";
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createJamesBondMovieValues();
        long movieRowId = TestUtilities.insertJamesBondMovieValues(mContext);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues reviewsValues = TestUtilities.createMovieReviewsValues(movieId);

        long weatherRowId = db.insert(ReviewsEntry.TABLE_NAME, null, reviewsValues);
        assertTrue("Unable to Insert WeatherEntry into the Database", weatherRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor reviewsCursor = mContext.getContentResolver().query(
                ReviewsEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicReviewsQuery", reviewsCursor, reviewsValues);
    }

    public void testBasicVideosQuery() {
        String movieId = "206647";
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createJamesBondMovieValues();
        long movieRowId = TestUtilities.insertJamesBondMovieValues(mContext);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues videoValues = TestUtilities.createMovieVideosValues(movieId);

        long videoRowId = db.insert(VideoEntry.TABLE_NAME, null, videoValues);
        assertTrue("Unable to Insert VideoForListFragmentEntry into the Database", movieRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor videoCursor = mContext.getContentResolver().query(
                VideoEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicVideoQuery", videoCursor, videoValues);
    }


    /*
        This test uses the provider to insert and then update the data. Uncomment this test to
        see if your update location is functioning correctly.
     */
    public void testUpdateMovie() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createJamesBondMovieValues();

        Uri movieUri = mContext.getContentResolver().
                insert(MovieEntry.CONTENT_URI, values);
        long movieId = ContentUris.parseId(movieUri);

        // Verify we got a row back.
        assertTrue(movieId != -1);
        Log.d(LOG_TAG, "New row id: " + movieId);

        ContentValues updatedValues = new ContentValues(values);
       // updatedValues.put(MovieEntry.COLUMN_MOVIE_ID, movieId);
        updatedValues.put(MovieEntry.COLUMN_MOVIE_TITLE, "Santa's Village");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor movieCursor = mContext.getContentResolver().query(MovieEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        movieCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                MovieEntry.CONTENT_URI, updatedValues, MovieEntry.COLUMN_MOVIE_ID + "= ?", new String[]{"206647"});
                //new String[] { Long.toString(movieId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        movieCursor.unregisterContentObserver(tco);
        movieCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,   // projection
                MovieEntry.COLUMN_MOVIE_ID + " = " + "206647",
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateLocation.  Error validating MOVIE entry update.",
                cursor, updatedValues);

        cursor.close();
    }


    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the insert functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createJamesBondMovieValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, tco);
        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, testValues);

        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long movieRowId = ContentUris.parseId(movieUri);

        // Verify we got a row back.
        assertTrue(movieRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating MovieEntry.",
                cursor, testValues);

        // Fantastic.  Now that we have a movie, add some reviews!
        ContentValues reviewsValues = TestUtilities.createMovieReviewsValues("206647");
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(ReviewsEntry.CONTENT_URI, true, tco);

        Uri reviewsInsertUri = mContext.getContentResolver()
                .insert(ReviewsEntry.CONTENT_URI, reviewsValues);
        assertTrue(reviewsInsertUri != null);

        // Did our content observer get called?  Students:  If this fails, your insert weather
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor reviewsCursor = mContext.getContentResolver().query(
                ReviewsEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating ReviewsEntry insert.",
                reviewsCursor, reviewsValues);

        // Add the movie values in with the reviews data so that we can make
        // sure that the join worked and we actually get all the values back
        reviewsValues.putAll(testValues);

        // Get the joined Reviews and Movie data
        reviewsCursor = mContext.getContentResolver().query(
                ReviewsEntry.buildReviewsForMovie(TestUtilities.TEST_MOVIE),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        int numberOfREviews=reviewsCursor.getCount();
;        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Reviews and Movies Data.",
                reviewsCursor, reviewsValues);

    }
    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the delete functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testDeleteRecords() {
        //testInsertReadProvider();

        // Register a content observer for our movie delete.
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, movieObserver);

        // Register a content observer for our reviews delete.
        TestUtilities.TestContentObserver reviewsObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ReviewsEntry.CONTENT_URI, true, reviewsObserver);

        // Register a content observer for our videos delete.
        TestUtilities.TestContentObserver videosObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(VideoEntry.CONTENT_URI, true, videosObserver);

        deleteAllRecordsFromProvider();

        // Students: If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
//        movieObserver.waitForNotificationOrFail();
        reviewsObserver.waitForNotificationOrFail();
        videosObserver.waitForNotificationOrFail();
        movieObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(movieObserver);
        mContext.getContentResolver().unregisterContentObserver(reviewsObserver);
        mContext.getContentResolver().unregisterContentObserver(videosObserver);
    }



    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertReviewsValues(long movieRowId) {

        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues reviewValues = new ContentValues();

            reviewValues.put(MovieContract.ReviewsEntry.COLUMN_MOVIE_KEY, movieRowId);
            reviewValues.put(MovieContract.ReviewsEntry.COLUMN_AUTHOR, "Nadia Pigott");
            reviewValues.put(MovieContract.ReviewsEntry.COLUMN_CONTENT, "S 8 Marta, tebya Katya !!!! ");

            returnContentValues[i] = reviewValues;
        }
        return returnContentValues;
    }

    // Student: Uncomment this test after you have completed writing the BulkInsert functionality
    // in your provider.  Note that this test will work with the built-in (default) provider
    // implementation, which just inserts records one-at-a-time, so really do implement the
    // BulkInsert ContentProvider function.
    public void testBulkInsert() {
        // first, let's create a location value
        ContentValues testValues = TestUtilities.createJamesBondMovieValues() ;
        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, testValues);
        long movieRowId = ContentUris.parseId(movieUri);

        // Verify we got a row back.
        assertTrue(movieRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testBulkInsert. Error validating MovieEntry.",
                cursor, testValues);

        // Now we can bulkInsert some reviews.  In fact, we only implement BulkInsert for reviews
        // entries.  With ContentProviders, you really only have to implement the features you
        // use, after all.
        ContentValues[] bulkInsertContentValues = createBulkInsertReviewsValues(movieRowId);

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver reviewsObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ReviewsEntry.CONTENT_URI, true, reviewsObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(ReviewsEntry.CONTENT_URI, bulkInsertContentValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        reviewsObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(reviewsObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        cursor = mContext.getContentResolver().query(
                ReviewsEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null //ReviewsEntry.COLUMN_DATE + " ASC"  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating  ReviewEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }
}
