package com.udacity.PopularMovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoritesDBContract {

    public static final String AUTHORITY           = "com.udacity.PopularMovies";
    public static final Uri    BASE_CONTENT_URI    = Uri.parse("content://"+AUTHORITY);
    public static final String PATH_FAVORITES      = "favorites";
    public static final String PATH_FAVORITES_ID   = "favorites/#";

    public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

    public static final class FavoritesEntry implements BaseColumns {
        public static final String TABLE_NAME            ="tab_favorites";
        public static final String COLUMN_MOVIE_ID       ="id";
        public static final String COLUMN_POSTER_PATH    ="poster_path";
        public static final String COLUMN_BACKDROP_PATH  ="backdrop_path";
    }

}
