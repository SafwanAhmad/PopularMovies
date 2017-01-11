package com.example.android.popularmovies;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.popularmovies.data.MovieContentProvider;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.MovieDbHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Created by safwanx on 1/8/17.
 */

@RunWith(AndroidJUnit4.class)
public class MovieContentProviderTest {

    /*Context used to perform operations on the database and create MovieDbHelper*/
    private final Context mContext = InstrumentationRegistry.getTargetContext();

    /**
     * Because we annotate this method with the @Before annotation, this method will be called
     * before every single method with an @Test annotation. We want to start each test clean, so we
     * delete all entries in the tasks directory to do so.
     */
    @Before
    public void setUp() {
        /* Use MovieDbHelper to get access to a writable database */
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        database.delete(MovieContract.Popular.TABLE_NAME, null, null);
        database.delete(MovieContract.TopRated.TABLE_NAME, null, null);
        database.delete(MovieContract.Favorite.TABLE_NAME, null, null);
    }

    //================================================================================
    // Test ContentProvider Registration
    //================================================================================


    /**
     * This test checks to make sure that the content provider is registered correctly in the
     * AndroidManifest file. If it fails, you should check the AndroidManifest to see if you've
     * added a <provider/> tag and that you've properly specified the android:authorities attribute.
     */
    @Test
    public void test_content_provider_registry() {
        /*
         * A ComponentName is an identifier for a specific application component, such as an
         * Activity, ContentProvider, BroadcastReceiver, or a Service.
         *
         * Two pieces of information are required to identify a component: the package (a String)
         * it exists in, and the class (a String) name inside of that package.
         *
         * We will use the ComponentName for our ContentProvider class to ask the system
         * information about the ContentProvider, specifically, the authority under which it is
         * registered.
         */
        String packageName = mContext.getPackageName();
        String movieProviderClassName = MovieContentProvider.class.getName();
        ComponentName componentName = new ComponentName(packageName, movieProviderClassName);

        try {
            /*
             * Get a reference to the package manager. The package manager allows us to access
             * information about packages installed on a particular device. In this case, we're
             * going to use it to get some information about our ContentProvider under test.
             */
            PackageManager packageManager = mContext.getPackageManager();

            /* The ProviderInfo will contain the authority, which is what we want to test */
            ProviderInfo providerInfo = packageManager.getProviderInfo(componentName, 0);
            String actualAuthority = providerInfo.authority;
            String expectedAuthority = packageName;

            /* Make sure that the registered authority matches the authority from the Contract */
            String incorrectAuthority =
                    "Error: TaskContentProvider registered with authority: " + actualAuthority +
                            " instead of expected authority: " + expectedAuthority;
            assertEquals(incorrectAuthority,
                    actualAuthority,
                    expectedAuthority);

        } catch (PackageManager.NameNotFoundException ex) {
            String providerNotRegisteredAtAll =
                    "Error: TaskContentProvider not registered at " + mContext.getPackageName();
            /*
             * This exception is thrown if the ContentProvider hasn't been registered with the
             * manifest at all. If this is the case, you need to double check your
             * AndroidManifest file
             */
            fail(providerNotRegisteredAtAll);
        }
    }


    //================================================================================
    // Test UriMatcher
    //================================================================================

    //Different Uri to be tested for the functionality of UriMatcher
    private static final Uri TEST_POPULAR = MovieContract.Popular.CONTENT_URI;
    private static final Uri TEST_POPULAR_ITEM = TEST_POPULAR.buildUpon().appendPath("1").build();

    private static final Uri TEST_TOP_RATED = MovieContract.TopRated.CONTENT_URI;
    private static final Uri TEST_TOP_RATED_ITEM = TEST_TOP_RATED.buildUpon().appendPath("1").build();

    private static final Uri TEST_FAVORITE = MovieContract.Favorite.CONTENT_URI;
    private static final Uri TEST_FAVORITE_ITEM = TEST_FAVORITE.buildUpon().appendPath("1").build();


    /**
     * This function tests that the UriMatcher returns the correct integer value for
     * each of the Uri types that the ContentProvider can handle. Uncomment this when you are
     * ready to test your UriMatcher.
     */
    @Test
    public void test_uri_matcher() {
        //Get the reference to the UriMatcher
        UriMatcher uriMatcher = MovieContentProvider.buildUriMatcher();

        //Test if the returned code for each of the directories is correct
        int expectedPopularCode = MovieContentProvider.POPULAR;
        int expectedTopRatedCode = MovieContentProvider.TOP_RATED;
        int expectedFavoriteCode = MovieContentProvider.FAVORITE;

        int actualPopularCode = uriMatcher.match(TEST_POPULAR);
        int actualTopRatedCode = uriMatcher.match(TEST_TOP_RATED);
        int actualFavoriteCode = uriMatcher.match(TEST_FAVORITE);

        assertEquals("Popular Uri was matched incorrectly!", expectedPopularCode, actualPopularCode);
        assertEquals("Top rated Uri was mathced incorrectly!", expectedTopRatedCode, actualTopRatedCode);
        assertEquals("Favorite Uri was matched incorrectly!", expectedFavoriteCode, actualFavoriteCode);

        //Test if the returned code for each of the row(item) is correct
        //Test if the returned code for each of the directories is correct
        int expectedPopularItemCode = MovieContentProvider.POPULAR_ITEM;
        int expectedTopRatedItemCode = MovieContentProvider.TOP_RATED_ITEM;
        int expectedFavoriteItemCode = MovieContentProvider.FAVORITE_ITEM;

        int actualPopularItemCode = uriMatcher.match(TEST_POPULAR_ITEM);
        int actualTopRatedItemCode = uriMatcher.match(TEST_TOP_RATED_ITEM);
        int actualFavoriteItemCode = uriMatcher.match(TEST_FAVORITE_ITEM);

        assertEquals("Popular item Uri was matched incorrectly!", expectedPopularItemCode, actualPopularItemCode);
        assertEquals("Top rated item Uri was mathced incorrectly!", expectedTopRatedItemCode, actualTopRatedItemCode);
        assertEquals("Favorite item Uri was matched incorrectly!", expectedFavoriteItemCode, actualFavoriteItemCode);

    }

    //================================================================================
    // Test Insert
    //================================================================================


    /**
     * Tests inserting a single row of data via a ContentResolver
     */
    @Test
    public void testInsert() {
        //Create values to insert
        ContentValues values = new ContentValues();
        values.put(MovieContract.Popular._ID, 1);
        values.put(MovieContract.Popular.COLUMN_MOVIE_TITLE, "Mr. Beans");
        values.put(MovieContract.Popular.COLUMN_MOVIE_POSTER_PATH, "www.tmdb.org");
        values.put(MovieContract.Popular.COLUMN_MOVIE_RELEASE_DATE, "10/01/2017");
        values.put(MovieContract.Popular.COLUMN_MOVIE_USER_RATING, "4.5");
        values.put(MovieContract.Popular.COLUMN_MOVIE_RUNNING_TIME, "120");
        values.put(MovieContract.Popular.COLUMN_MOVIE_PLOT, "Comedy by Atkinson");

        /* TestContentObserver allows us to test if notifyChange was called appropriately */
        TestUtilities.TestContentObserver testContentObserver = TestUtilities.getTestContentObserver();

        /*Get the content resolver*/
        ContentResolver resolver = mContext.getContentResolver();

        /* Register a content observer to be notified of changes to data at a given URI (popular) */
        resolver.registerContentObserver(
                /* URI that we would like to observe changes to */
                MovieContract.Popular.CONTENT_URI,
                /* Whether or not to notify us if descendants of this URI change */
                true,
                /* The observer to register (that will receive notifyChange callbacks) */
                testContentObserver);

        Uri uri = resolver.insert(MovieContract.Popular.CONTENT_URI, values);

        Uri expectedUri = ContentUris.withAppendedId(MovieContract.Popular.CONTENT_URI, 1);

        String insertProviderFailed = "Unable to insert item through Provider";
        assertEquals(insertProviderFailed, uri, expectedUri);

        /*
         * If this fails, it's likely you didn't call notifyChange in your insert method from
         * your ContentProvider.
         */
        testContentObserver.waitForNotificationOrFail();

         /*
         * waitForNotificationOrFail is synchronous, so after that call, we are done observing
         * changes to content and should therefore unregister this observer.
         */
        resolver.unregisterContentObserver(testContentObserver);

    }

    //================================================================================
    // Test Query (for directory/ all rows and single row)
    //================================================================================

    /**
     * Inserts data, then tests if a query for the tasks directory returns that data as a Cursor
     */
    @Test
    public void testQuery() {
        //Get access to writable database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        //Create values to insert
        ContentValues values1 = new ContentValues();
        values1.put(MovieContract.Popular._ID, 1);
        values1.put(MovieContract.Popular.COLUMN_MOVIE_TITLE, "Mr. Beans");
        values1.put(MovieContract.Popular.COLUMN_MOVIE_POSTER_PATH, "www.tmdb.org");
        values1.put(MovieContract.Popular.COLUMN_MOVIE_RELEASE_DATE, "10/01/2017");
        values1.put(MovieContract.Popular.COLUMN_MOVIE_USER_RATING, "4.5");
        values1.put(MovieContract.Popular.COLUMN_MOVIE_RUNNING_TIME, "120");
        values1.put(MovieContract.Popular.COLUMN_MOVIE_PLOT, "Comedy by Atkinson");

        ContentValues values2 = new ContentValues();
        values2.put(MovieContract.Popular._ID, 2);
        values2.put(MovieContract.Popular.COLUMN_MOVIE_TITLE, "Mr. Beans");
        values2.put(MovieContract.Popular.COLUMN_MOVIE_POSTER_PATH, "www.tmdb.org");
        values2.put(MovieContract.Popular.COLUMN_MOVIE_RELEASE_DATE, "10/01/2017");
        values2.put(MovieContract.Popular.COLUMN_MOVIE_USER_RATING, "4.5");
        values2.put(MovieContract.Popular.COLUMN_MOVIE_RUNNING_TIME, "120");
        values2.put(MovieContract.Popular.COLUMN_MOVIE_PLOT, "Comedy by Atkinson");

        /* Insert ContentValues into database and get a row ID back */
        long rowId1 = database.insert(
                //Table to insert into
                MovieContract.Popular.TABLE_NAME,
                null,
                //Values to be inserted
                values1
        );

        long rowId2 = database.insert(
                //Table to insert into
                MovieContract.Popular.TABLE_NAME,
                null,
                //Values to be inserted
                values2
        );

        String insertFailed = "Unable to insert directly into the database";
        assertTrue(insertFailed, (rowId1 != -1) && (rowId2 != -1));

        /* We are done with the database, close it now. */
        database.close();

        /* Perform the ContentProvider query */
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.Popular.CONTENT_URI,
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                null,
                /* Values for "where" clause */
                null,
                /* Sort order to return in Cursor */
                null);

        String queryFailed = "Query failed to return a valid Cursor";
        assertTrue(queryFailed, cursor != null);

        String countInvalid = "Not all rows returned back";
        assertTrue(countInvalid, cursor.getCount() == 2);

        cursor.close();

        cursor = mContext.getContentResolver().query(
                MovieContract.Popular.CONTENT_URI.buildUpon().appendPath("1").build(),
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                null,
                /* Values for "where" clause */
                null,
                /* Sort order to return in Cursor */
                null);

        assertTrue(queryFailed, cursor != null);
        assertTrue(countInvalid, cursor.getCount() == 1);

        if (cursor.moveToFirst()) {
            assertTrue("Movie Id does not match", "1".equals(cursor.getString(cursor.getColumnIndex(MovieContract.Popular._ID))));
        }

        /* We are done with the cursor, close it now. */
        cursor.close();
    }
}
