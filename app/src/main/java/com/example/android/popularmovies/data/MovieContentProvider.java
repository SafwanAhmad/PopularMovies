package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * In order to build a content provider of its own the should be a class that
 * extends <code>{@link ContentProvider}</code> and overload all the abstract methods
 * as done below.
 * Created by safwanx on 1/7/17.
 */

public class MovieContentProvider extends ContentProvider {
    //Define constants that are used by the Uri matcher.
    // It's convention to use 100, 200, 300, etc for directories,
    // and related ints (101, 102, ..) for items in that directory.
    public static final int POPULAR = 100;
    public static final int POPULAR_ITEM = 101;
    public static final int TOP_RATED = 200;
    public static final int TOP_RATED_ITEM = 201;
    public static final int FAVORITE = 300;
    public static final int FAVORITE_ITEM = 301;

    //Define a static Uri matcher object
    private static UriMatcher sUriMatcher = buildUriMatcher();

    //Create a member variable for database helper
    SQLiteOpenHelper mDbHelper;


    /**
     * The purpose of this method is to define all URIs that are valid for this content
     * provider. Further it helps to map these URIs to different constants defined in content
     * provider and later allow easy URI matching.
     * For the identification of a single row the _ID field representing a movie id is used.
     * @return UriMatcher object.
     */
    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //Add URI for all rows inside popular table (complete directory)
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_POPULAR, POPULAR);
        //Add URI for a single row inside popular table(single item)
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_POPULAR + "/#", POPULAR_ITEM);

        //Add URI for all rows inside topRated table (complete directory)
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_TOP_RATED, TOP_RATED);
        //Add URI for a single row inside topRated table (single item)
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_TOP_RATED + "/#", TOP_RATED_ITEM);

        //Add URI for all rows inside favorite table(complete directory)
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITE, FAVORITE);
        //Add URI for a single row inside favorite table (single item)
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITE + "/#", FAVORITE_ITEM);

        return uriMatcher;
    }

    /* onCreate() is where you should initialize anything you’ll need to setup
    your underlying data source.
    In this case, you’re working with a SQLite database, so you’ll need to
    initialize a DbHelper to gain access to it.
     */
    @Override
    public boolean onCreate() {
        Context context = getContext();
        //Get the object using our own MovieDbHelper class
        mDbHelper = new MovieDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }
}
