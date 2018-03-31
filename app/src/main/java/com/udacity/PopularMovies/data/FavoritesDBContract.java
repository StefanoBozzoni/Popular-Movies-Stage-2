package com.udacity.PopularMovies.data;

import android.provider.BaseColumns;

public class FavoritesDBContract {

    public static final class FavoritesEntry implements BaseColumns {
        public static final String TABLE_NAME            ="tab_favorites";
        public static final String COLUMN_VOTE_COUNT     ="vote_count";
        public static final String COLUMN_VOTE_AVERAGE   ="vote_average";
        public static final String COLUMN_MOVIE_ID       ="id";
        public static final String COLUMN_POSTER_PATH    ="poster_path";
        public static final String COLUMN_BACKTROP_PATH  ="backdrop_path";
        public static final String COLUMN_RELEASE_DATE   ="release_date";
    }

}
