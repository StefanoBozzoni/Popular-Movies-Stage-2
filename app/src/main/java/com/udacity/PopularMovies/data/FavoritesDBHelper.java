package com.udacity.PopularMovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class FavoritesDBHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "Favorites.db";

    static final int DATABASE_VERSION  = 1;

    public FavoritesDBHelper(Context context) {
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_FAVORITES_TABLE= "CREATE TABLE "+ FavoritesDBContract.FavoritesEntry.TABLE_NAME+ "("+
                FavoritesDBContract.FavoritesEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                FavoritesDBContract.FavoritesEntry.COLUMN_MOVIE_ID     +" INTEGER NOT NULL, "+
                FavoritesDBContract.FavoritesEntry.COLUMN_POSTER_PATH  +" TEXT NULL, "+
                FavoritesDBContract.FavoritesEntry.COLUMN_BACKDROP_PATH+" TEXT NULL "+
                //FavoritesDBContract.FavoritesEntry.COLUMN_VOTE_COUNT+" INTEGER NULL ,"+
                //FavoritesDBContract.FavoritesEntry.COLUMN_RELEASE_DATE+" DATE NULL ,"+
                //FavoritesDBContract.FavoritesEntry.COLUMN_VOTE_AVERAGE+" DECIMAL(10,5) NULL "+
                ")";
        db.execSQL(CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ FavoritesDBContract.FavoritesEntry.TABLE_NAME);
        onCreate(db);
    }

}
