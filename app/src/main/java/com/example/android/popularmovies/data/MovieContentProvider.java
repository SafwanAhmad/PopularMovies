package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

    //These are different Selection clauses which are used to perform different
    //types of queries. The ? mark is replaced by the selection args provided
    //in the query.
    static final String sSelectionPopular = MovieContract.Popular._ID + "=?";
    static final String sSelectionTopRated = MovieContract.TopRated._ID + "=?";
    static final String sSelectionFavorite = MovieContract.Favorite._ID + "=?";

    /**
     * The purpose of this method is to define all URIs that are valid for this content
     * provider. Further it helps to map these URIs to different constants defined in content
     * provider and later allow easy URI matching.
     * For the identification of a single row the _ID field representing a movie id is used.
     *
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
        //Use uri matcher to determine the type of the uri
        int code = sUriMatcher.match(uri);

        switch (code) {
            case POPULAR: {
                return MovieContract.Popular.CONTENT_TYPE;
            }

            case POPULAR_ITEM: {
                return MovieContract.Popular.CONTENT_ITEM_TYPE;
            }

            case TOP_RATED: {
                return MovieContract.TopRated.CONTENT_TYPE;
            }

            case TOP_RATED_ITEM: {
                return MovieContract.TopRated.CONTENT_ITEM_TYPE;
            }

            case FAVORITE: {
                return MovieContract.Favorite.CONTENT_TYPE;
            }

            case FAVORITE_ITEM: {
                return MovieContract.Favorite.CONTENT_ITEM_TYPE;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //Right now we are not providing support for update
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        //Get access to writable database
        final SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id;
        Uri returnedUri;

        //Use uri matcher to check the validity of passed uri.
        int code = sUriMatcher.match(uri);

        switch (code) {
            case POPULAR: {
                id = database.insert(MovieContract.Popular.TABLE_NAME,
                        null,
                        values);
            }
            break;
            case TOP_RATED: {
                id = database.insert(MovieContract.TopRated.TABLE_NAME,
                        null,
                        values);
            }
            break;
            case FAVORITE: {
                id = database.insert(MovieContract.Favorite.TABLE_NAME,
                        null,
                        values);
            }
            break;
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        //Check if the values have been successfully inserted
        if (id > 0) {
            returnedUri = ContentUris.withAppendedId(uri, id);

            //Notify the resolver if the uri has been changed, and return the newly inserted URI
            getContext().getContentResolver().notifyChange(uri, null);
        } else {
            throw new android.database.SQLException("Failed to insert row into " + uri);
        }
        return returnedUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //Get the access to a writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Number of rows deleted
        int rowsDeleted;

        //Use UriMatcher to check if the uri is supported
        int code = sUriMatcher.match(uri);

        switch (code) {
            case POPULAR: {
                rowsDeleted = database.delete(
                        //Name of the table containing the data
                        MovieContract.Popular.TABLE_NAME,
                        //Selection clause (where in sql)
                        selection,
                        //Arguments for the selection
                        selectionArgs
                );
            }
            break;

            case POPULAR_ITEM: {
                //Get the movie ID for a specific movie
                String movieId = uri.getPathSegments().get(1);
                //Make the selection clause for this operation
                String[] mSelectionArgs = new String[]{movieId};

                rowsDeleted = database.delete(
                        MovieContract.Popular.TABLE_NAME,
                        sSelectionPopular,
                        mSelectionArgs
                );
            }
            break;

            case TOP_RATED: {
                rowsDeleted = database.delete(
                        //Name of the table containing the data
                        MovieContract.TopRated.TABLE_NAME,
                        //Selection clause (where in sql)
                        selection,
                        //Arguments for the selection
                        selectionArgs
                );
            }
            break;

            case TOP_RATED_ITEM: {
                //Get the movie ID for a specific movie
                String movieId = uri.getPathSegments().get(1);
                //Make the selection clause for this operation
                String[] mSelectionArgs = new String[]{movieId};

                rowsDeleted = database.delete(
                        MovieContract.TopRated.TABLE_NAME,
                        sSelectionTopRated,
                        mSelectionArgs
                );
            }
            break;

            case FAVORITE: {
                rowsDeleted = database.delete(
                        //Name of the table containing the data
                        MovieContract.Favorite.TABLE_NAME,
                        //Selection clause (where in sql)
                        selection,
                        //Arguments for the selection
                        selectionArgs
                );
            }
            break;

            case FAVORITE_ITEM: {
                //Get the movie ID for a specific movie
                String movieId = uri.getPathSegments().get(1);
                //Make the selection clause for this operation
                String[] mSelectionArgs = new String[]{movieId};

                rowsDeleted = database.delete(
                        MovieContract.Favorite.TABLE_NAME,
                        sSelectionFavorite,
                        mSelectionArgs
                );
            }
            break;

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        //Don't forget to notify listener about the data change
        getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Get access to readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        //Cursor to be returned (if query is successful)
        Cursor cursor = null;

        //Find if the uri is valid
        int code = sUriMatcher.match(uri);

        switch (code) {
            case POPULAR: {
                cursor = database.query(MovieContract.Popular.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            }
            break;

            //Based on this type of uri, provider will find the right selection and selection args
            //to be sent in the query.
            case POPULAR_ITEM: {
                //Using Selection and Selection args
                //URI: content://<authority>/<path>/#

                String movieId = uri.getPathSegments().get(1);

                cursor = database.query(MovieContract.Popular.TABLE_NAME,
                        projection,
                        sSelectionPopular,
                        new String[]{movieId},
                        null,
                        null,
                        sortOrder);
            }
            break;

            case TOP_RATED: {
                cursor = database.query(MovieContract.TopRated.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            }
            break;

            case TOP_RATED_ITEM: {
                //Using Selection and Selection args
                //URI: content://<authority>/<path>/#

                String movieId = uri.getPathSegments().get(1);

                cursor = database.query(MovieContract.TopRated.TABLE_NAME,
                        projection,
                        sSelectionTopRated,
                        new String[]{movieId},
                        null,
                        null,
                        sortOrder);
            }
            break;

            case FAVORITE: {
                cursor = database.query(MovieContract.Favorite.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            }
            break;

            case FAVORITE_ITEM: {
                //Using Selection and Selection args
                //URI: content://<authority>/<path>/#

                String movieId = uri.getPathSegments().get(1);

                cursor = database.query(MovieContract.Favorite.TABLE_NAME,
                        projection,
                        sSelectionFavorite,
                        new String[]{movieId},
                        null,
                        null,
                        sortOrder);
            }
            break;

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        //Also register to watch a content URI for changes.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        //Get the access to a writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int code = sUriMatcher.match(uri);

        switch (code) {
            case POPULAR: {
                //See how begin transaction is used effectively
                database.beginTransaction();
                //Number of rows inserted
                int rowsInserted = 0;

                try{
                    for (ContentValues value : values) {
                        long id = database.insert(MovieContract.Popular.TABLE_NAME,
                                null,
                                value);

                        if (id != -1) {
                            rowsInserted++;
                        }
                    }
                    database.setTransactionSuccessful();

                } finally {
                    database.endTransaction();
                }
                //Don't forget to notify listener about the data change
                getContext().getContentResolver().notifyChange(uri, null);
                return rowsInserted;
            }

            case TOP_RATED: {
                //See how begin transaction is used effectively
                database.beginTransaction();
                //Number of rows inserted
                int rowsInserted = 0;

                try{
                    for (ContentValues value : values) {
                        long id = database.insert(MovieContract.TopRated.TABLE_NAME,
                                null,
                                value);

                        if (id != -1) {
                            rowsInserted++;
                        }

                    }
                    database.setTransactionSuccessful();

                } finally {
                    database.endTransaction();
                }
                //Don't forget to notify listener about the data change
                getContext().getContentResolver().notifyChange(uri, null);
                return rowsInserted;
            }

            case FAVORITE: {
                //See how begin transaction is used effectively
                database.beginTransaction();
                //Number of rows inserted
                int rowsInserted = 0;

                try{
                    for (ContentValues value : values) {
                        long id = database.insert(MovieContract.Favorite.TABLE_NAME,
                                null,
                                value);

                        if (id != -1) {
                            rowsInserted++;
                        }
                    }
                    database.setTransactionSuccessful();

                } finally {
                    database.endTransaction();
                }
                //Don't forget to notify listener about the data change
                getContext().getContentResolver().notifyChange(uri, null);
                return rowsInserted;
            }

            default: {
                return super.bulkInsert(uri, values);
            }
        }
    }
}
