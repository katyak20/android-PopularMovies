package com.intelligentcompute.android.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by katya on 21/03/2016.
 */
public class TestReviewsContract extends AndroidTestCase {

    private static final String TEST_REVIEWS_FOR_MOVIE = "/206647";

    public void testBuildWeatherLocation() {
        Uri movieUri = MovieContract.ReviewsEntry.buildReviewsForMovie(TEST_REVIEWS_FOR_MOVIE);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildWeatherLocation in " +
                        "WeatherContract.",
                movieUri);
        assertEquals("Error: Weather location not properly appended to the end of the Uri",
                TEST_REVIEWS_FOR_MOVIE, movieUri.getLastPathSegment());
        assertEquals("Error: Weather location Uri doesn't match our expected result",
                movieUri.toString(),
                "content://com.intelligentcompute.android.popularmovies/reviews/%2F206647");
    }
}
