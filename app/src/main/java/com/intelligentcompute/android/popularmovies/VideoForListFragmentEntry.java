package com.intelligentcompute.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoForListFragmentEntry implements Parcelable{
    private String text;
    private String videoId;

    public VideoForListFragmentEntry(String text, String videoId) {
        this.text = text;

        this.videoId = videoId;
    }

    public VideoForListFragmentEntry(Parcel in) {
        String[] data = new String[2];
        in.readStringArray(data);

        this.text = data[0];
        this.videoId = data[1];
    }
    public String getText() {
        return text;
    }

    public String getVideoId() {
        return videoId;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] { String.valueOf(this.text),String.valueOf(this.videoId)});
    }

    private void readFromParcel(Parcel in) {
        text = in.readString();
        videoId = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public VideoForListFragmentEntry createFromParcel(Parcel in) {
            return new VideoForListFragmentEntry(in);
        }

        @Override
        public VideoForListFragmentEntry[] newArray(int size) {
            return new VideoForListFragmentEntry[size];
        }
    };
}