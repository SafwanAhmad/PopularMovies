package com.example.android.popularmovies.data;

import android.content.ContentResolver;
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
    public static final String SCHEME = "content://";

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_POPULAR = "popular";
    public static final String PATH_TOP_RATED = "topRated";
    public static final String PATH_FAVORITE = "favorite";


    //Create all the required tables for the movie database. They are in the form of inner classes.
    //Each inner class represents a table in the database. For movies app we need three table for
    //popular, top rated, and favorite.

    public static final class Popular implements BaseColumns {
        //Content Uri for popular table (directory)
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_POPULAR).build();

        //Different MIME types used by getType method of content provider
        //We are working with two types of data:
        //1) a directory and 2) a single row of data.
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR;

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
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_RATED).build();

        //Different MIME types used by getType method of content provider
        //We are working with two types of data:
        //1) a directory and 2) a single row of data.
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;

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
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        //Different MIME types used by getType method of content provider
        //We are working with two types of data:
        //1) a directory and 2) a single row of data.
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;

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
