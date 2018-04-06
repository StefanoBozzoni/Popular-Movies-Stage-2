package com.udacity.PopularMovies.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.udacity.PopularMovies.data.FavoritesDBContract;
import com.udacity.PopularMovies.data.FavoritesProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MovieItem implements Parcelable {
    public static final String id_Json                ="id";
    public static final String vote_count_Json        ="vote_count";
    public static final String vote_average_Json      ="vote_average";
    public static final String video_Json             ="video";
    public static final String popularity_Json        ="popularity";
    public static final String poster_path_Json       ="poster_path";
    public static final String original_language_Json ="original_language";
    public static final String original_title_Json    ="original_title";
    public static final String genre_ids_Json         ="genre_ids";
    public static final String backdrop_path_Json     ="backdrop_path";
    public static final String adult_Json             ="adult";
    public static final String overview_Json          ="overview";
    public static final String release_date_Json      ="release_date";

    private int vote_count;
    private int id;
    private boolean video;
    private float vote_average;
    private int popularity;
    private String poster_path;
    private String original_language;
    private String original_title;
    private List<Integer> genre_ids;
    private String backdrop_path;
    private boolean adult;
    private String overview;
    private Date release_date;

    public MovieItem() {

    }

    public MovieItem(int id, int vote_count, float vote_average, boolean video, int popularity, String poster_path,
                     String original_language, String original_title, List<Integer> genre_ids,
                     String backdrop_path, boolean adult, String overview, Date release_date) {
        this.id            =id;
        this.vote_count    =vote_count;
        this.vote_average =vote_average;
        this.video         =video;
        this.popularity    =popularity;
        this.poster_path   =poster_path;
        this.original_language=original_language;
        this.original_title=original_title;
        this.genre_ids     =genre_ids;
        this.backdrop_path =backdrop_path;
        this.adult         =adult;
        this.overview      =overview;
        this.release_date  =release_date;
    }

    public int getVote_count() {
        return vote_count;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public float getVote_average() {
        return vote_average;
    }

    public void setVote_average(float vote_average) {
        this.vote_average = vote_average;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public String getOriginal_title() {
        return (original_title == null ? "" : original_title);
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public List<Integer> getGenre_ids() {
        return genre_ids;
    }

    public void setGenre_ids(List<Integer> genre_ids) {
        this.genre_ids = genre_ids;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Date getRelease_date() {
        return release_date;
    }

    public String getRelease_date_printable() {
        SimpleDateFormat sdfr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            return sdfr.format(getRelease_date());
        } catch (Exception ex ){
            ex.printStackTrace();
            return null;
        }

    }

    public void setRelease_date(Date release_date) {
        this.release_date = release_date;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.vote_count);
        dest.writeInt(this.id);
        dest.writeByte(this.video ? (byte) 1 : (byte) 0);
        dest.writeFloat(this.vote_average);
        dest.writeInt(this.popularity);
        dest.writeString(this.poster_path);
        dest.writeString(this.original_language);
        dest.writeString(this.original_title);
        dest.writeList(this.genre_ids);
        dest.writeString(this.backdrop_path);
        dest.writeByte(this.adult ? (byte) 1 : (byte) 0);
        dest.writeString(this.overview);
        dest.writeLong(this.release_date != null ? this.release_date.getTime() : -1);
    }

    protected MovieItem(Parcel in) {
        this.vote_count = in.readInt();
        this.id = in.readInt();
        this.video = in.readByte() != 0;
        this.vote_average = in.readFloat();
        this.popularity = in.readInt();
        this.poster_path = in.readString();
        this.original_language = in.readString();
        this.original_title = in.readString();
        this.genre_ids = new ArrayList<Integer>();
        in.readList(this.genre_ids, Integer.class.getClassLoader());
        this.backdrop_path = in.readString();
        this.adult = in.readByte() != 0;
        this.overview = in.readString();
        long tmpRelease_date = in.readLong();
        this.release_date = tmpRelease_date == -1 ? null : new Date(tmpRelease_date);
    }

    public static final Parcelable.Creator<MovieItem> CREATOR = new Parcelable.Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel source) {
            return new MovieItem(source);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    public boolean ExistInDB(ContentResolver res) {
        Cursor favQry= res.query(
                FavoritesDBContract.CONTENT_URI,
                null,
                FavoritesDBContract.FavoritesEntry.COLUMN_MOVIE_ID+"=?",
                new String[]{Integer.toString(getId())},
                null,
                null
        );
        boolean result=(favQry.getCount()!=0);
        favQry.close();
        return result;
    }

    /*
    to add directly in a db without passing from a provider:
    public boolean AddToDB(SQLiteDatabase mDb) {
        ContentValues cv = new ContentValues();
        cv.put(FavoritesDBContract.FavoritesEntry.COLUMN_MOVIE_ID, getId());
        cv.put(FavoritesDBContract.FavoritesEntry.COLUMN_POSTER_PATH, getPoster_path());
        long rowInserted = mDb.insert(FavoritesDBContract.FavoritesEntry.TABLE_NAME, null, cv);
        return (rowInserted!=0);
    }
    */

    public boolean AddToDB(ContentResolver res) {
        ContentValues cv = new ContentValues();
        cv.put(FavoritesDBContract.FavoritesEntry.COLUMN_MOVIE_ID, getId());
        cv.put(FavoritesDBContract.FavoritesEntry.COLUMN_POSTER_PATH, getPoster_path());
        Uri uri = res.insert(FavoritesDBContract.CONTENT_URI,cv);
        //to insert directly on db -> long rowInserted = mDb.insert(FavoritesDBContract.FavoritesEntry.TABLE_NAME, null, cv);
        return (uri!=null);
    }

    public  boolean deleteFromDB(ContentResolver res) {
        String sMovieId=Integer.toString(getId());
        int rowsaffected= res.delete(FavoritesDBContract.CONTENT_URI, FavoritesDBContract.FavoritesEntry.COLUMN_MOVIE_ID+"="+sMovieId,null);
        //int rowsaffected= mDb.delete(FavoritesDBContract.FavoritesEntry.TABLE_NAME, FavoritesDBContract.FavoritesEntry.COLUMN_MOVIE_ID+"="+sMovieId,null);
        return (rowsaffected!=0);
    }
}
