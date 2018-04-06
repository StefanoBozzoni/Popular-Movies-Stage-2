package com.udacity.PopularMovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.udacity.PopularMovies.data.FavoritesDBContract.FavoritesEntry.TABLE_NAME;

/**
 * Created by stefa on 04/04/2018.
 */

public class FavoritesProvider extends ContentProvider {
    static FavoritesDBHelper mFavoritesDbHelper;
    static final int FAVORITES    =100;
    static final int FAVORITES_ID =101;
    static final UriMatcher sUriMatcher=buildUriMatcher();


    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavoritesDBContract.AUTHORITY,FavoritesDBContract.PATH_FAVORITES   ,FAVORITES);
        uriMatcher.addURI(FavoritesDBContract.AUTHORITY,FavoritesDBContract.PATH_FAVORITES_ID,FAVORITES_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mFavoritesDbHelper=new FavoritesDBHelper(context);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db= mFavoritesDbHelper.getReadableDatabase();
        int match= sUriMatcher.match(uri);
        Cursor returnCursor;

        switch (match) {
            case FAVORITES : {
                returnCursor = db.query(
                                        FavoritesDBContract.FavoritesEntry.TABLE_NAME,
                                        projection,
                                        selection,
                                        selectionArgs,
                                        null,
                                        null,
                                        sortOrder);
                break;
            }
            case FAVORITES_ID: {
                String id=uri.getPathSegments().get(0);
                returnCursor=db.query(
                                    FavoritesDBContract.FavoritesEntry.TABLE_NAME,
                                    projection,
                                    FavoritesDBContract.FavoritesEntry.COLUMN_MOVIE_ID+"=?",
                                    new String[] {id},
                                    null,
                                    null,
                                    sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri"+uri);
        }
        //getContext().getContentResolver().notifyChange(uri,null);

        returnCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return  returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("getType not implemented.");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db= mFavoritesDbHelper.getWritableDatabase();
        int match= sUriMatcher.match(uri);
        Uri resultUri;

        switch (match) {
            case FAVORITES : {
                long n=  db.insert(TABLE_NAME,null, values);
                if (n>0) {
                    resultUri= ContentUris.withAppendedId(FavoritesDBContract.CONTENT_URI,n);
                }
                else {
                    throw new SQLException("Failed to insert new row: "+uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri"+uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return  resultUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db= mFavoritesDbHelper.getWritableDatabase();
        int match= sUriMatcher.match(uri);
        Uri resultUri;
        int numRows=0;
        switch (match) {
            case FAVORITES : {
                numRows=  db.delete(TABLE_NAME,selection,selectionArgs);
                break;
            }
            case FAVORITES_ID : {
                String id=uri.getPathSegments().get(0);
                numRows=  db.delete(TABLE_NAME,FavoritesDBContract.FavoritesEntry._ID+"=?",new String[] {id});
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri"+uri);
        }
        if (numRows!=0)
           getContext().getContentResolver().notifyChange(uri,null);
        return  numRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db= mFavoritesDbHelper.getWritableDatabase();
        int match= sUriMatcher.match(uri);
        Uri resultUri;
        int numRows=0;
        switch (match) {
            case FAVORITES_ID : {
                String id=uri.getPathSegments().get(0);
                numRows=  db.update(TABLE_NAME, values,FavoritesDBContract.FavoritesEntry.COLUMN_MOVIE_ID+"=?",new String[] {id});
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri"+uri);
        }
        if ((numRows!=0) && (getContext()!=null) && (getContext().getContentResolver()!=null))
            getContext().getContentResolver().notifyChange(uri,null);

        return  numRows;
    }
}
