package com.example.android.popularmovies.data;

import android.provider.BaseColumns;

/**
 * <code>{@link MovieContract}</code> is the contract class used for the movies database.
 * Created by safwanx on 12/29/16.
 */

public class MovieContract {

    //Create all the required tables for the movie database. They are in the form of inner classes.
    //Each inner class represents a table in the database. For movies app we need three table for
    //popular, top rated, and favorite.

    public static final class Popular implements BaseColumns
    {
        //Name of the table
        public static final String TABLE_NAME = "popular";
        //Names of different columns in this table
        public static final String COLUMN_MOVIE_TITLE = "movieTitle";
        public static final String COLUMN_MOVIE_POSTER_PATH = "moviePosterPath";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movieReleaseDate";
        public static final String COLUMN_MOVIE_RUNNING_TIME = "movieRunningTime";
        public static final String COLUMN_MOVIE_USER_RATING = "movieUserRating";
        public static final String COLUMN_MOVIE_PLOT = "moviePlot";
    }

    public static final class TopRated implements BaseColumns
    {
        //Name of the table
        public static final String TABLE_NAME = "topRated";
        //Names of different columns in this table
        public static final String COLUMN_MOVIE_TITLE = "movieTitle";
        public static final String COLUMN_MOVIE_POSTER_PATH = "moviePosterPath";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movieReleaseDate";
        public static final String COLUMN_MOVIE_RUNNING_TIME = "movieRunningTime";
        public static final String COLUMN_MOVIE_USER_RATING = "movieUserRating";
        public static final String COLUMN_MOVIE_PLOT = "moviePlot";
    }

    public static final class Favorite implements BaseColumns
    {
        //Name of the table
        public static final String TABLE_NAME = "favorite";
        //Names of different columns in this table
        public static final String COLUMN_MOVIE_TITLE = "movieTitle";
        public static final String COLUMN_MOVIE_POSTER_PATH = "moviePosterPath";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movieReleaseDate";
        public static final String COLUMN_MOVIE_RUNNING_TIME = "movieRunningTime";
        public static final String COLUMN_MOVIE_USER_RATING = "movieUserRating";
        public static final String COLUMN_MOVIE_PLOT = "moviePlot";
    }
}
