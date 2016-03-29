
package com.intelligentcompute.android.popularmovies;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.intelligentcompute.android.popularmovies.data.MovieContract;
import com.intelligentcompute.android.popularmovies.tasks.FetchPopularMoviesTask;

public class TestFetchPopularMoviesTask extends AndroidTestCase {
    static final int ADD_MOVIE_ID = 209112;
    static final String ADD_MOVIE_TITLE = "Batman v Superman: Dawn of Justice";
    static final String ADD_MOVIE_RELEASE_DATE = "2016-03-23";
    static final double ADD_MOVIE_POPULARITY = 91.377529;
    static final double ADD_MOVIE_VOTE_AVERAGE = 5.95;
    static final String ADD_MOVIE_POSTER_PATH = "\\/6bCplVkhowCjTHXWv49UjRPn0eK.jpg";
    static final String ADD_MOVIE_OVERVIEW = "Fearing the actions of a god-like Super Hero left unchecked, Gotham City’s own formidable, forceful vigilante takes on Metropolis’s most revered, modern-day savior, while the world wrestles with what sort of hero it really needs. And with Batman and Superman at war with one another, a new threat quickly arises";


    @TargetApi(11)
    public void testAddMovie() {
        // start from a clean state
        getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{Integer.toString(ADD_MOVIE_ID)});

        FetchPopularMoviesTask fwt = new FetchPopularMoviesTask(getContext());
        int movieId = fwt.addMovie(ADD_MOVIE_ID, ADD_MOVIE_TITLE, ADD_MOVIE_RELEASE_DATE, ADD_MOVIE_POSTER_PATH,
                ADD_MOVIE_POPULARITY, ADD_MOVIE_VOTE_AVERAGE, ADD_MOVIE_OVERVIEW);

        // does addMovie return a valid record ID?
        assertFalse("Error: addMovie returned an invalid ID on insert",
                movieId == -1);

        // test all this twice
        for ( int i = 0; i < 2; i++ ) {

            // does the ID point to our location?
            Cursor movieCursor = getContext().getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    new String[]{
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
                            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                            MovieContract.MovieEntry.COLUMN_POPULARITY,
                            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
                            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
                            MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW
                    },
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[]{String.valueOf(movieId)},
                    null);

          //  String title =movieCursor.getString(1);
            // these match the indices of the projection
            if (movieCursor.moveToFirst()) {
                assertEquals("Error: the queried value of movieId does not match the returned value" +
                        "from addMovie", movieCursor.getInt(0), movieId);
                assertEquals("Error: the queried value of title is incorrect",
                        movieCursor.getString(1), ADD_MOVIE_TITLE);
                assertEquals("Error: the queried value of release date is incorrect",
                        movieCursor.getString(2), ADD_MOVIE_RELEASE_DATE);
                assertEquals("Error: the queried value of popularity value is incorrect",
                        movieCursor.getDouble(3), ADD_MOVIE_POPULARITY);
                assertEquals("Error: the queried value of vote average is incorrect",
                        movieCursor.getDouble(4), ADD_MOVIE_VOTE_AVERAGE);
                assertEquals("Error: the queried value of poster path is incorrect",
                        movieCursor.getString(5), ADD_MOVIE_POSTER_PATH);
                assertEquals("Error: the queried value of movie overview is incorrect",
                        movieCursor.getString(6), ADD_MOVIE_OVERVIEW);
            } else {
                fail("Error: the id you used to query returned an empty cursor");
            }

            // there should be no more records
            assertFalse("Error: there should be only one record returned from a movie query",
                    movieCursor.moveToNext());

            // add the movie again
            int newMovieId = fwt.addMovie(ADD_MOVIE_ID, ADD_MOVIE_TITLE, ADD_MOVIE_RELEASE_DATE, ADD_MOVIE_POSTER_PATH,
                    ADD_MOVIE_POPULARITY, ADD_MOVIE_VOTE_AVERAGE, ADD_MOVIE_OVERVIEW);

            assertEquals("Error: inserting a location again should return the same ID",
                    movieId, newMovieId);
        }
        // reset our state back to normal
        getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{String.valueOf(ADD_MOVIE_ID)});

        // clean up the test so that other tests can use the content provider
        getContext().getContentResolver().
                acquireContentProviderClient(MovieContract.MovieEntry.CONTENT_URI).
                getLocalContentProvider().shutdown();
    }
}
