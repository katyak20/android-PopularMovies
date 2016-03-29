package com.intelligentcompute.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.intelligentcompute.android.popularmovies.tasks.FetchPopularMoviesTask;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by katya on 08/02/2016.
 */
public class PopularMoviesFragment extends Fragment {


    private ImageAdapter mMoviePostersAdapter;
    private ArrayList<ArrayList<String>> popularMovies;
    private ArrayList<String> movieIDsList;
    private HashMap<String, ArrayList<HashMap<String, String>>> moviesReviews;
    private HashMap<String, ArrayList<VideoForListFragmentEntry>> moviesVideos;

    public PopularMoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        popularMovies = new ArrayList<>();
        movieIDsList = new ArrayList<String>();
        moviesReviews = new HashMap<>();
        moviesVideos = new HashMap<>();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.favouritemoviesfragment_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateMoviesList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) view.findViewById(R.id.gridview);

       /* final FetchPopularMoviesTask moviesTask = new FetchPopularMoviesTask(getActivity());
        moviesTask.execute("popularity.desc");*/
        mMoviePostersAdapter = new ImageAdapter(getActivity(), new ArrayList<String>());
        gridView.setAdapter(mMoviePostersAdapter); // uses the view to get the context instead of getActivity().
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                ArrayList<String> movieOverview = PopularMoviesFragment.this.popularMovies.get(position);

//                Toast.makeText(getActivity(), movieOverview.get(6), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DetailActivityWithSlidingTabs.class); //DetailActivity.class); VideoListDemoActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putStringArrayList("movieOverview", movieOverview);

//                mBundle.putParcelableArrayList("reviews", moviesVideos.get(movieOverview.get(0)));

       //         mBundle.putParcelableArrayList("videos", moviesVideos.get(movieOverview.get(0)));
                intent.putExtras(mBundle);
                startActivity(intent);
            }
        });
        return view;
    }

    private void updateMoviesList() {
        FetchPopularMoviesTask moviesTask = new FetchPopularMoviesTask(getActivity(), mMoviePostersAdapter);
        // moviesTask.execute("vote_average.desc");
      //  popularMovies = moviesTask.getPopularMovies();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sorting_order = prefs.getString(getString(R.string.pref_sorting_key),
                getString(R.string.pref_sorting_default));
        sorting_order = "popularity";
        moviesTask.execute(sorting_order + ".desc");

        popularMovies = moviesTask.getPopularMovies();
      //  moviesReviews =moviesTask.getmoviesReviews();
        //moviesVideos = moviesTask.getMoviesVideos();
    }

    @Override
    public void onStart() {
        super.onStart();

        updateMoviesList();
    }

   /* @Override
    public void onResume() {
        super.onResume();
        updateMoviesList();
    }*/
}
