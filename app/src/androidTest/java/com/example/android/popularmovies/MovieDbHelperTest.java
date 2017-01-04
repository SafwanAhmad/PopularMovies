package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.MovieDbHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by safwanx on 1/2/17.
 */
@RunWith(AndroidJUnit4.class)
public class MovieDbHelperTest {
    /* Context used to perform operations on the database and create MovieDbHelper*/
    private final Context mContext = InstrumentationRegistry.getTargetContext();
    /* Class reference to help load the constructor on runtime */
    private final Class mMovieDbHelper = MovieDbHelper.class;


    /**
     * Because we annotate this method with the @Before annotation, this method will be called
     * before every single method with an @Test annotation. We want to start each test clean, so we
     * delete the database to do so.
     */
    @Before
    public void setUp() {
        deleteTheDatabase();
    }

    void deleteTheDatabase() {

        try {
            //Use reflection to get the database name from db helper class
            Field field = mMovieDbHelper.getDeclaredField("DATABASE_NAME");
            field.setAccessible(true);
            mContext.deleteDatabase((String) field.get(null));
        } catch (NoSuchFieldException ex) {
            fail("Make sure there is a member called DATABASE_NAME in MovieDbHelper.");
        } catch (IllegalAccessException ex) {
            fail(ex.getMessage());
        }
    }

    @After
    public void tearDown() {
        deleteTheDatabase();
    }

    /**
     * This method tests that our database contains all of the tables that we think it should
     * contain.
     *
     * @throws Exception in case the constructor hasn't been implemented yet
     */
    @Test
    public void create_database_test() throws Exception {

        /* Use reflection to try to run the correct constructor */
        SQLiteOpenHelper dbHelper = (SQLiteOpenHelper) mMovieDbHelper.getConstructor(Context.class).newInstance(mContext);

        /* Use MovieDbHelper to get access to a writable database */
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        /*We think the database is open, let's verify that here */
        String databaseIsNotOpen = "The database should be open and isn't";
        assertEquals(databaseIsNotOpen, true, database.isOpen());

        /* This cursor will contain name of each table in our database */
        Cursor tableNameCursor = database.rawQuery(
                "SELECT name FROM sqlite_master WHERE type ='table' AND name='"
                        + MovieContract.Popular.TABLE_NAME + "'"
                        + " OR name='" + MovieContract.Favorite.TABLE_NAME + "'"
                        + " OR name='" + MovieContract.TopRated.TABLE_NAME
                        + "'", null);

        /*
         * If tableNameCursor.moveToFirst returns false from this query, it means the database
         * wasn't created properly. In actuality, it means that your database contains no tables.
         */
        String errorInCreatingDatabase =
                "Error: This means that the database has not been created correctly";
        assertTrue(errorInCreatingDatabase,
                tableNameCursor.moveToFirst());

        /* If this fails, it means that your database doesn't contain the expected table(s) */
        do {
            String tableName = tableNameCursor.getString(0);
            assertTrue(tableName, (tableName != "popular" || tableName != "favorite" || tableName != "topRated"));
        } while (tableNameCursor.moveToNext());

        /* Always close a cursor when you are done with it */
        tableNameCursor.close();
    }

    /**
     * This method tests inserting a single record into an empty table from a brand new database.
     * The purpose is to test that the database is working as expected
     *
     * @throws Exception in case the constructor hasn't been implemented yet
     */
    @Test
    public void insert_single_record_into_popular_test() throws Exception {

        //Use reflection to try to run the correct constructor
        SQLiteOpenHelper dbHelper = (SQLiteOpenHelper) mMovieDbHelper.getConstructor(Context.class).newInstance(mContext);

        //Use MovieDbHelper to get access to writable database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        /*We think the database is open, let's verify that here */
        String databaseIsNotOpen = "The database should be open and isn't";
        assertEquals(databaseIsNotOpen, true, database.isOpen());

        //Wrap values inside ContentValues to store them in the database
        ContentValues popularValues = new ContentValues();
        popularValues.put(MovieContract.Popular.COLUMN_MOVIE_TITLE, "Mr. Beans");
        popularValues.put(MovieContract.Popular.COLUMN_MOVIE_POSTER_PATH, "www.tmdb.com/1");
        popularValues.put(MovieContract.Popular.COLUMN_MOVIE_RELEASE_DATE, "04/01/2017");
        popularValues.put(MovieContract.Popular.COLUMN_MOVIE_RUNNING_TIME, "120min");
        popularValues.put(MovieContract.Popular.COLUMN_MOVIE_USER_RATING, "4.2/5");
        popularValues.put(MovieContract.Popular.COLUMN_MOVIE_PLOT, "A nice comedy by Atkinson.");
        popularValues.put(MovieContract.Popular._ID, "1");

        /* Insert ContentValues into database and get first row ID back */
        long firstRowId = database.insert(MovieContract.Popular.TABLE_NAME,
                null,
                popularValues);

        /* If the insert fails, database.insert returns -1 */
        assertNotEquals("Unable to insert into the database", -1, firstRowId);

        /*
         * Query the database and receive a Cursor. A Cursor is the primary way to interact with
         * a database in Android.
         */
        Cursor wCursor = database.query(
                /* Name of table on which to perform the query */
                MovieContract.Popular.TABLE_NAME,
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                null,
                /* Values for "where" clause */
                null,
                /* Columns to group by */
                null,
                /* Columns to filter by row groups */
                null,
                /* Sort order to return in Cursor */
                null);

        /* Cursor.moveToFirst will return false if there are no records returned from your query */
        String emptyQueryError = "Error: No Records returned from waitlist query";
        assertTrue(emptyQueryError,
                wCursor.moveToFirst());

        /* Close cursor and database */
        wCursor.close();
        dbHelper.close();
    }

    /**
     * Tests that onUpgrade works by inserting 2 rows then calling onUpgrade and verifies that the
     * database has been successfully dropped and recreated by checking that the database is there
     * but empty
     * @throws Exception in case the constructor hasn't been implemented yet
     */
    @Test
    public void updrade_database_test() throws Exception
    {
         /* Use reflection to try to run the correct constructor whenever implemented */
        SQLiteOpenHelper dbHelper =
                (SQLiteOpenHelper) mMovieDbHelper.getConstructor(Context.class).newInstance(mContext);

        /* Use WaitlistDbHelper to get access to a writable database */
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        /*We think the database is open, let's verify that here */
        String databaseIsNotOpen = "The database should be open and isn't";
        assertEquals(databaseIsNotOpen, true, database.isOpen());

        /* Insert 2 rows before we upgrade to check that we dropped the database correctly */
        //Wrap values inside ContentValues to store them in the database
        ContentValues popularValues1 = new ContentValues();
        popularValues1.put(MovieContract.Popular.COLUMN_MOVIE_TITLE, "Mr. Beans");
        popularValues1.put(MovieContract.Popular.COLUMN_MOVIE_POSTER_PATH, "www.tmdb.com/1");
        popularValues1.put(MovieContract.Popular.COLUMN_MOVIE_RELEASE_DATE, "04/01/2017");
        popularValues1.put(MovieContract.Popular.COLUMN_MOVIE_RUNNING_TIME, "120min");
        popularValues1.put(MovieContract.Popular.COLUMN_MOVIE_USER_RATING, "4.2/5");
        popularValues1.put(MovieContract.Popular.COLUMN_MOVIE_PLOT, "A nice comedy by Atkinson.");
        popularValues1.put(MovieContract.Popular._ID, "1");

        //Wrap values inside ContentValues to store them in the database
        ContentValues popularValues2 = new ContentValues();
        popularValues2.put(MovieContract.Popular.COLUMN_MOVIE_TITLE, "Mr. Beans");
        popularValues2.put(MovieContract.Popular.COLUMN_MOVIE_POSTER_PATH, "www.tmdb.com/1");
        popularValues2.put(MovieContract.Popular.COLUMN_MOVIE_RELEASE_DATE, "04/01/2017");
        popularValues2.put(MovieContract.Popular.COLUMN_MOVIE_RUNNING_TIME, "120min");
        popularValues2.put(MovieContract.Popular.COLUMN_MOVIE_USER_RATING, "4.2/5");
        popularValues2.put(MovieContract.Popular.COLUMN_MOVIE_PLOT, "A nice comedy by Atkinson.");
        popularValues2.put(MovieContract.Popular._ID, "2");

        /* Insert ContentValues into database and get first row ID back */
        long firstRowId = database.insert(MovieContract.Popular.TABLE_NAME,
                null,
                popularValues1);

        /* Insert ContentValues into database and get first row ID back */
        long secondRowId = database.insert(MovieContract.Popular.TABLE_NAME,
                null,
                popularValues2);

        dbHelper.onUpgrade(database, 0, 1);
        database = dbHelper.getReadableDatabase();

        /* This cursor will contain name of each table in our database */
        Cursor tableNameCursor = database.rawQuery(
                "SELECT name FROM sqlite_master WHERE type ='table' AND name='"
                        + MovieContract.Popular.TABLE_NAME + "'"
                        + " OR name='" + MovieContract.Favorite.TABLE_NAME + "'"
                        + " OR name='" + MovieContract.TopRated.TABLE_NAME
                        + "'", null);

        assertTrue(tableNameCursor.getCount() == 3);

        /*
         * Query the database and receive a Cursor. A Cursor is the primary way to interact with
         * a database in Android.
         */
        Cursor wCursor = database.query(
                /* Name of table on which to perform the query */
                MovieContract.Popular.TABLE_NAME,
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                null,
                /* Values for "where" clause */
                null,
                /* Columns to group by */
                null,
                /* Columns to filter by row groups */
                null,
                /* Sort order to return in Cursor */
                null);

        /* Cursor.moveToFirst will return false if there are no records returned from your query */

        assertFalse("Database doesn't seem to have been dropped successfully when upgrading",
                wCursor.moveToFirst());

        tableNameCursor.close();
        database.close();
    }
}