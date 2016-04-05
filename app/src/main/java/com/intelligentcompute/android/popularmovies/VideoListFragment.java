package com.intelligentcompute.android.popularmovies;

import android.support.v4.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoListFragment extends ListFragment {   // public static final class VideoListFragment extends ListFragment {

    private static ArrayList<VideoForListFragmentEntry> videoList;

    static VideoListFragment init(int val) {
        VideoListFragment truitonFrag = new VideoListFragment();


        Bundle args = new Bundle();
        args.putInt("val", val);
        truitonFrag.setArguments(args);
        return truitonFrag;
    }

    static VideoListFragment init(ArrayList<VideoForListFragmentEntry> listOfVideos) {
        VideoListFragment videoListFragment = new VideoListFragment();
        videoList =listOfVideos;

        return videoListFragment;
    }
    private PageAdapter adapter;
    private View videoBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            Intent intent = this.getActivity().getIntent();
            Bundle myBundle= intent.getExtras();
            if (intent.hasExtra("videos")) {
                videoList = myBundle.getParcelableArrayList("videos");

            }

        adapter = new PageAdapter(getActivity(), videoList);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String videoId = videoList.get(position).getVideoId();

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+videoId)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        adapter.releaseLoaders();
    }


/**
 * Adapter for the video list. Manages a set of YouTubeThumbnailViews, including initializing each
 * of them only once and keeping track of the loader of each one. When the ListFragment gets
 * destroyed it releases all the loaders.
 */
private static final class PageAdapter extends BaseAdapter {

    private final List<VideoForListFragmentEntry> entries;
    private final List<View> entryViews;
    private final Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap;
    private final LayoutInflater inflater;
    private final ThumbnailListener thumbnailListener;

    private boolean labelsVisible;

    public PageAdapter(Context context, List<VideoForListFragmentEntry> entries) {
        this.entries = entries;

        entryViews = new ArrayList<View>();
        thumbnailViewToLoaderMap = new HashMap<YouTubeThumbnailView, YouTubeThumbnailLoader>();
        inflater = LayoutInflater.from(context);
        thumbnailListener = new ThumbnailListener();

        labelsVisible = true;
    }

    public void releaseLoaders() {
        for (YouTubeThumbnailLoader loader : thumbnailViewToLoaderMap.values()) {
            loader.release();
        }
    }


    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public VideoForListFragmentEntry getItem(int position) {
        return entries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        VideoForListFragmentEntry entry = entries.get(position);

        // There are three cases here
        if (view == null) {
            // 1) The view has not yet been created - we need to initialize the YouTubeThumbnailView.
            view = inflater.inflate(R.layout.video_list_item, parent, false);
            YouTubeThumbnailView thumbnail = (YouTubeThumbnailView) view.findViewById(R.id.thumbnail);
            thumbnail.setTag(entry.getVideoId());
            thumbnail.initialize(DeveloperKey.DEVELOPER_KEY, thumbnailListener);
        } else {
            YouTubeThumbnailView thumbnail = (YouTubeThumbnailView) view.findViewById(R.id.thumbnail);
            YouTubeThumbnailLoader loader = thumbnailViewToLoaderMap.get(thumbnail);
            if (loader == null) {
                // 2) The view is already created, and is currently being initialized. We store the
                //    current videoId in the tag.
                thumbnail.setTag(entry.getVideoId());
            } else {
                // 3) The view is already created and already initialized. Simply set the right videoId
                //    on the loader.
                thumbnail.setImageResource(R.drawable.loading_thumbnail);
                loader.setVideo(entry.getVideoId());
            }
        }
        TextView label = ((TextView) view.findViewById(R.id.text));
        label.setText(entry.getText());
        //label.setVisibility(labelsVisible ? View.VISIBLE : View.GONE);
        return view;
    }

    private final class ThumbnailListener implements
            YouTubeThumbnailView.OnInitializedListener,
            YouTubeThumbnailLoader.OnThumbnailLoadedListener {

        @Override
        public void onInitializationSuccess(
                YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
            loader.setOnThumbnailLoadedListener(this);
            thumbnailViewToLoaderMap.put(view, loader);
            view.setImageResource(R.drawable.loading_thumbnail);
            String videoId = (String) view.getTag();
            loader.setVideo(videoId);
        }

        @Override
        public void onInitializationFailure(
                YouTubeThumbnailView view, YouTubeInitializationResult loader) {
            view.setImageResource(R.drawable.no_thumbnail);
        }

        @Override
        public void onThumbnailLoaded(YouTubeThumbnailView view, String videoId) {
        }

        @Override
        public void onThumbnailError(YouTubeThumbnailView view, YouTubeThumbnailLoader.ErrorReason errorReason) {
            view.setImageResource(R.drawable.no_thumbnail);
        }
    }

}

    // Utility methods for layouting.

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private static void setLayoutSize(View view, int width, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }

    private static void setLayoutSizeAndGravity(View view, int width, int height, int gravity) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        params.gravity = gravity;
        view.setLayoutParams(params);
    }
}