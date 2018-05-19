package com.app.mast.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pawansingh on 27/03/18.
 */

public class Repository implements Parcelable {
    private String name;
    private String html_url;
    private int size;
    private int watchers;
    private int open_issues_count;
    private User owner;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getHtml_url() {
        return html_url;
    }

    public void setHtml_url(String html_url) {
        this.html_url = html_url;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getWatchers() {
        return watchers;
    }

    public void setWatchers(int watchers) {
        this.watchers = watchers;
    }

    public int getOpen_issues_count() {
        return open_issues_count;
    }

    public void setOpen_issues_count(int open_issues_count) {
        this.open_issues_count = open_issues_count;
    }

    @Override
    public String toString() {
        return "Repository [name=" + name + ", description=" + description + "]";
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.html_url);
        dest.writeInt(this.size);
        dest.writeInt(this.watchers);
        dest.writeInt(this.open_issues_count);
        dest.writeParcelable(this.owner, flags);
        dest.writeString(this.description);
    }

    public Repository() {
    }

    protected Repository(Parcel in) {
        this.name = in.readString();
        this.html_url = in.readString();
        this.size = in.readInt();
        this.watchers = in.readInt();
        this.open_issues_count = in.readInt();
        this.owner = in.readParcelable(User.class.getClassLoader());
        this.description = in.readString();
    }

    public static final Parcelable.Creator<Repository> CREATOR = new Parcelable.Creator<Repository>() {
        @Override
        public Repository createFromParcel(Parcel source) {
            return new Repository(source);
        }

        @Override
        public Repository[] newArray(int size) {
            return new Repository[size];
        }
    };
}
