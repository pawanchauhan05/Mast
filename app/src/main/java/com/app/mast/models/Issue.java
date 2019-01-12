package com.app.mast.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Issue implements Parcelable {
    private User user;
    private int number;
    private String title;
    private PullRequest pull_request;
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PullRequest getPull_request() {
        return pull_request;
    }

    public void setPull_request(PullRequest pull_request) {
        this.pull_request = pull_request;
    }

    public Issue() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.user, flags);
        dest.writeInt(this.number);
        dest.writeString(this.title);
        dest.writeParcelable(this.pull_request, flags);
        dest.writeString(this.state);
    }

    protected Issue(Parcel in) {
        this.user = in.readParcelable(User.class.getClassLoader());
        this.number = in.readInt();
        this.title = in.readString();
        this.pull_request = in.readParcelable(PullRequest.class.getClassLoader());
        this.state = in.readString();
    }

    public static final Creator<Issue> CREATOR = new Creator<Issue>() {
        @Override
        public Issue createFromParcel(Parcel source) {
            return new Issue(source);
        }

        @Override
        public Issue[] newArray(int size) {
            return new Issue[size];
        }
    };
}
