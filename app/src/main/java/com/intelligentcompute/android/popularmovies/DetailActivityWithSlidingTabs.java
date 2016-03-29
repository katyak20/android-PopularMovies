package com.intelligentcompute.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.intelligentcompute.android.popularmovies.common.view.SlidingTabLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailActivityWithSlidingTabs extends FragmentActivity {
    static final int ITEMS = 2;
    private MyAdapter mAdapter;
    private ViewPager mPager;
    private SlidingTabLayout mSlidingTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);
        mAdapter = new MyAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mPager);
        ArrayList<String> movieDetails;
        Intent intent = getIntent();
        Bundle myBundle= intent.getExtras();
        if (intent != null && intent.hasExtra("movieOverview") ) {
            movieDetails = myBundle.getStringArrayList("movieOverview");
            ((TextView) findViewById(R.id.movieTitle)).setText(movieDetails.get(1));
            ((TextView) findViewById(R.id.movieReleaseDate)).setText(movieDetails.get(3));
            ((TextView) findViewById(R.id.movieVoteAverage)).setText(movieDetails.get(4));
            ((TextView) findViewById(R.id.moviePlotSynopsis)).setText(movieDetails.get(5));
            //((TextView) rootView.findViewById(R.id.movieUtubeId)).setText(movieDetails.get(6));

            ImageView imageView = (ImageView) findViewById(R.id.moviePoster);
            String posterPathUrl = "http://image.tmdb.org/t/p/w300/"
                    + movieDetails.get(2) + "&api_key=e257613f461ed40c956dc1464fb16313";
            Picasso.with(this).load(posterPathUrl).into(imageView);
        }

     /*   Button button = (Button) findViewById(R.id.first);
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

    public static class MyAdapter extends FragmentPagerAdapter {
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

                //case 0: // Fragment # 1 - This will show image
                   // return VideoListFragment.init(position);
                default:// Fragment # 2-9 - Will show list
                    return ReviewsListFragment.init(position);
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
