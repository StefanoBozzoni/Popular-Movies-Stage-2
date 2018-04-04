package com.udacity.PopularMovies.data;

import android.provider.BaseColumns;

public class FavoritesDBContract {

    public static final class FavoritesEntry implements BaseColumns {
        public static final String TABLE_NAME            ="tab_favorites";
        public static final String COLUMN_MOVIE_ID       ="id";
        public static final String COLUMN_POSTER_PATH    ="poster_path";
        public static final String COLUMN_BACKDROP_PATH  ="backdrop_path";
    }

}
