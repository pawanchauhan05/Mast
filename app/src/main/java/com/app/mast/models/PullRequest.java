package com.app.mast.models;

import android.os.Parcel;
import android.os.Parcelable;

public class PullRequest implements Parcelable {
    private String patch_url;

    public String getPatch_url() {
        return patch_url;
    }

    public void setPatch_url(String patch_url) {
        this.patch_url = patch_url;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.patch_url);
    }

    public PullRequest() {
    }

    protected PullRequest(Parcel in) {
        this.patch_url = in.readString();
    }

    public static final Parcelable.Creator<PullRequest> CREATOR = new Parcelable.Creator<PullRequest>() {
        @Override
        public PullRequest createFromParcel(Parcel source) {
            return new PullRequest(source);
        }

        @Override
        public PullRequest[] newArray(int size) {
            return new PullRequest[size];
        }
    };
}
