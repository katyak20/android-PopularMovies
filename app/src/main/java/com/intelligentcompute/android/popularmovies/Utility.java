package com.intelligentcompute.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Created by katya on 06/04/2016.
 */
public class Utility {

    public static String getPreferredSortingOrder(Context c) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);

        String mSortingOrder = prefs.getString(c.getString(R.string.pref_sorting_key),
                c.getString(R.string.pref_sorting_default));
        return mSortingOrder;
    }
}
