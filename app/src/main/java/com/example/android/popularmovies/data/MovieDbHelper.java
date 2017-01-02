package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by safwanx on 1/1/17.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    //Constant representing the name of the database
    public static final String DATABASE_NAME = "movie.db";
    //Initail version for the database, set it to one
    public static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Build sql query to build all the required tables
        final String SQL_CREATE_POPULAR_TABLE = "CREATE TABLE " +
                MovieContract.Popular.TABLE_NAME + " (" +
                MovieContract.Popular._ID + " TEXT PRIMARY KEY, " +
                MovieContract.Popular.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieContract.Popular.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.Popular.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.Popular.COLUMN_MOVIE_RUNNING_TIME + " TEXT NOT NULL, " +
                MovieContract.Popular.COLUMN_MOVIE_USER_RATING + " TEXT NOT NULL, " +
                MovieContract.Popular.COLUMN_MOVIE_PLOT + " TEXT NOT NULL " +
                ");";

        final String SQL_CREATE_TOP_RATED_TABLE = "CREATE TABLE " +
                MovieContract.TopRated.TABLE_NAME + " (" +
                MovieContract.Popular._ID + " TEXT PRIMARY KEY, " +
                MovieContract.Popular.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieContract.Popular.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.Popular.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.Popular.COLUMN_MOVIE_RUNNING_TIME + " TEXT NOT NULL, " +
                MovieContract.Popular.COLUMN_MOVIE_USER_RATING + " TEXT NOT NULL, " +
                MovieContract.Popular.COLUMN_MOVIE_PLOT + " TEXT NOT NULL " +
                ");";

        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " +
                MovieContract.Favorite.TABLE_NAME + " (" +
                MovieContract.Popular._ID + " TEXT PRIMARY KEY, " +
                MovieContract.Popular.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieContract.Popular.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.Popular.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.Popular.COLUMN_MOVIE_RUNNING_TIME + " TEXT NOT NULL, " +
                MovieContract.Popular.COLUMN_MOVIE_USER_RATING + " TEXT NOT NULL, " +
                MovieContract.Popular.COLUMN_MOVIE_PLOT + " TEXT NOT NULL " +
                ");";

        //Execute these queries
        db.execSQL(SQL_CREATE_POPULAR_TABLE);
        db.execSQL(SQL_CREATE_TOP_RATED_TABLE);
        db.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Right now we simply drop the existing tables in the database and recreate them.
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.Popular.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.TopRated.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.Favorite.TABLE_NAME);
        onCreate(db);
    }
}
