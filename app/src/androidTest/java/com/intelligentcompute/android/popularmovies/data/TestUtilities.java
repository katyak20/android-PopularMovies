package com.intelligentcompute.android.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.intelligentcompute.android.popularmovies.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

public class TestUtilities extends AndroidTestCase {
    static final String TEST_MOVIE = "206647";
  //  static final long TEST_DATE = 1419033600L;  // December 20th, 2014

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    /*
        Students: Use this to create some default weather values for your database tests.
     */
    static ContentValues createMovieReviewsValues(String movieRowId) {
        ContentValues reviewsValues = new ContentValues();
        reviewsValues.put(MovieContract.ReviewsEntry.COLUMN_MOVIE_KEY, movieRowId);
        reviewsValues.put(MovieContract.ReviewsEntry.COLUMN_AUTHOR, "Nadia Pigott");
        reviewsValues.put(MovieContract.ReviewsEntry.COLUMN_CONTENT, "S 8 Marta, tebya Katya !!!! ");


        return reviewsValues;
    }

    static ContentValues createMovieVideosValues(String movieRowId) {
        ContentValues videoValues = new ContentValues();
        videoValues.put(MovieContract.VideoEntry.COLUMN_MOVIE_KEY, movieRowId);
        videoValues.put(MovieContract.VideoEntry.COLUMN_TRAILER_PATH, "7GqClqvlObY");
        videoValues.put(MovieContract.VideoEntry.COLUMN_DESCRIPTION, "Trailer #2");

        return videoValues;
    }
    /*
        Students: You can uncomment this helper function once you have finished creating the
        LocationEntry part of the WeatherContract.
     */
    static ContentValues createJamesBondMovieValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, 206647);
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, "Spectre" );
        testValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,"2015-10-26");
        testValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 1);
        testValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, 1);
        testValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "hE24GYddaxB9MVZl1CaiI86M3kp.jpg");
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, "A cryptic message from Bond’s past sends him on a trail to uncover a sinister organization.");
        return testValues;
    }

    /*
        Students: You can uncomment this function once you have finished creating the
        MovieEntry part of the MovieContract as well as the MovieDbHelper.
     */
    static long insertJamesBondMovieValues(Context context) {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createJamesBondMovieValues();

        Cursor cursor = db.query(
                MovieContract.MovieEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );
        if(cursor.moveToFirst()) {
          db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);
        }
        long movieRowId;
        movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert James Bond Movie Values", movieRowId != -1);

        return movieRowId;
    }

    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}