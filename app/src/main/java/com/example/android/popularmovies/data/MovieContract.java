package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * <code>{@link MovieContract}</code> is the contract class used for the movies database.
 * Created by safwanx on 12/29/16.
 */

public class MovieContract {

    /*Add content provider constants to this contact. Clients need to know how to access
    the data. It is our job to provide content URIs for different paths and following data:
     1) Content Authority
     2) Base Content Uri
     3) Path(s) to different directories (tables/multiple rows)
     4) Content URIs for different tables(inner classes).
     */
    private static final String SCHEME = "content://";

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    private static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    private static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    private static final String PATH_POPULAR = "popular";
    private static final String PATH_TOP_RATED = "topRated";
    private static final String PATH_FAVORITE = "favorite";


    //Create all the required tables for the movie database. They are in the form of inner classes.
    //Each inner class represents a table in the database. For movies app we need three table for
    //popular, top rated, and favorite.

    public static final class Popular implements BaseColumns {
        //Content Uri for popular table (directory)
        private static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_POPULAR).build();
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

    public static final class TopRated implements BaseColumns {
        //Content Uri for topRated table(directory)
        private static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_RATED).build();
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

    public static final class Favorite implements BaseColumns {
        //Content Uri for favorite table(directory)
        private static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();
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
