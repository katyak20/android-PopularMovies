package com.intelligentcompute.android.popularmovies.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/*
    Uncomment this class when you are ready to test your UriMatcher.  Note that this class utilizes
    constants that are declared with package protection inside of the UriMatcher, which is why
    the test must be in the same data package as the Android app code.  Doing the test this way is
    a nice compromise between data hiding and testability.
 */
public class TestUriMatcher extends AndroidTestCase {
    private static final String MOVIE_QUERY = "206647";

    // content://com.intelligentcompute.android.popularmovies/reviews"
    private static final Uri TEST_REVIEWS_DIR = MovieContract.ReviewsEntry.CONTENT_URI;
    private static final Uri TEST_REVIEWS_FOR_MOVIE_DIR = MovieContract.ReviewsEntry.buildReviewsForMovie(MOVIE_QUERY);
   // content://com.intelligentcompute.android.popularmovies/videos"
    private static final Uri TEST_VIDEOS_DIR = MovieContract.VideoEntry.CONTENT_URI;
    private static final Uri TEST_VIDEOS_FOR_MOVIE_DIR = MovieContract.VideoEntry.buildVideosForMovie(MOVIE_QUERY);
    // content://com.intelligentcompute.android.popularmovies/movies"
    private static final Uri TEST_MOVIES_DIR = MovieContract.MovieEntry.CONTENT_URI;

    /*
        Students: This function tests that your UriMatcher returns the correct integer value
        for each of the Uri types that our ContentProvider can handle.  Uncomment this when you are
        ready to test your UriMatcher.
     */
    public void testUriMatcher() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();

        assertEquals("Error: The REVIEWS URI was matched incorrectly.",
                testMatcher.match(TEST_REVIEWS_DIR), MovieProvider.REVIEWS);
        assertEquals("Error: The REVIEWS FOR MOVIE URI was matched incorrectly.",
                testMatcher.match(TEST_REVIEWS_FOR_MOVIE_DIR), MovieProvider.REVIEWS_FOR_MOVIE);
        assertEquals("Error: The VIDEOS URI was matched incorrectly.",
                testMatcher.match(TEST_VIDEOS_DIR), MovieProvider.VIDEOS);
        assertEquals("Error: The VIDEOS FOR MOVIE URI was matched incorrectly.",
                testMatcher.match(TEST_VIDEOS_FOR_MOVIE_DIR), MovieProvider.VIDEOS_FOR_MOVIE);
        assertEquals("Error: The MOVIES URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIES_DIR), MovieProvider.MOVIES);
    }
}
