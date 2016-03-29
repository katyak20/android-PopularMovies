package com.intelligentcompute.android.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by katya on 21/03/2016.
 */
public class TestVideoContract extends AndroidTestCase {

    private static final String TEST_VIDEOS_FOR_MOVIE = "/206647";

    public void testBuildWeatherLocation() {
        Uri movieUri = MovieContract.VideoEntry.buildVideosForMovie(TEST_VIDEOS_FOR_MOVIE);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildVideosForMovie in " +
                        "VideoContract.",
                movieUri);
        assertEquals("Error: Movie id not properly appended to the end of the Uri",
                TEST_VIDEOS_FOR_MOVIE, movieUri.getLastPathSegment());
        assertEquals("Error: videos for movie Uri doesn't match our expected result",
                movieUri.toString(),
                "content://com.intelligentcompute.android.popularmovies/videos/%2F206647");
    }
}
