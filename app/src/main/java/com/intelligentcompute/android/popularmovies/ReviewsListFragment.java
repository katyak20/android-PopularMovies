package com.intelligentcompute.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by katya on 03/03/2016.
 */
public class ReviewsListFragment extends Fragment {
    int fragNum;
    String arr[] = { "This is", "a Truiton", "Demo", "App", "For", "Showing",
            "FragmentPagerAdapter", "and ViewPager", "Implementation" };

    static ReviewsListFragment init(int val) {
        ReviewsListFragment truitonList = new ReviewsListFragment();


        // Supply val input as an argument.
        Bundle args = new Bundle();
        args.putInt("val", val);
        truitonList.setArguments(args);

        return truitonList;
    }

    /**
     * Retrieving this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragNum = getArguments() != null ? getArguments().getInt("val") : 1;
    }

    /**
     * The Fragment's UI is a simple text view showing its instance number and
     * an associated list.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_pager_reviews_list,
                container, false);
        Intent intent = getActivity().getIntent();
        Bundle myBundle= intent.getExtras();
        if (intent.hasExtra("reviews")) {

            ArrayList<HashMap<String, String>> myArrList = (ArrayList)myBundle.getSerializable("reviews");
            ListView reviews = (ListView)layoutView.findViewById(R.id.reviews_list);

            SimpleAdapter adapter = new SimpleAdapter(this.getContext(), myArrList, android.R.layout.two_line_list_item,//simple_list_item_2,
                    new String[] {"author", "content"},
                    new int[] {android.R.id.text1, android.R.id.text2});
            reviews.setAdapter(adapter);

        }
        View tv = layoutView.findViewById(R.id.text);
        ((TextView) tv).setText("Truiton Fragment #" + fragNum);
        return layoutView;
    }




}