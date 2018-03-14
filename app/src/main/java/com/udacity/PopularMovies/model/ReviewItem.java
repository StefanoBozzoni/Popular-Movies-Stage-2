package com.udacity.PopularMovies.model;


import android.os.Parcel;
import android.os.Parcelable;

public class ReviewItem implements Parcelable {

    public static final String id_Json       ="id";
    public static final String author_Json   ="author";
    public static final String content_Json  ="content";
    public static final String url_Json      ="url";

    private String id;
    private String author;
    private String content;
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.author);
        dest.writeString(this.content);
        dest.writeString(this.url);
    }

    public ReviewItem() {
    }

    public ReviewItem(String id, String author, String content, String url) {
        this.id=id;this.author=author;this.content=content; this.url=url;
    }


    protected ReviewItem(Parcel in) {
        this.id = in.readString();
        this.author = in.readString();
        this.content = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<ReviewItem> CREATOR = new Parcelable.Creator<ReviewItem>() {
        @Override
        public ReviewItem createFromParcel(Parcel source) {
            return new ReviewItem(source);
        }

        @Override
        public ReviewItem[] newArray(int size) {
            return new ReviewItem[size];
        }
    };
}