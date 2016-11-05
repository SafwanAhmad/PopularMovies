package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity
        implements MoviePostersFragment.OnFragmentInteractionListener, GetMoviesData.DownloadComplete,
    MovieDetailFragment.OnDetailFragmentInteractionListener
{
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    //Store the value for preferences so that a change can be monitored
    private String mSortingOrder;

    //Reference to async task object
    GetMoviesData mGetMoviesData = null;

    //flag to find if device supports two panes
    private boolean mTwoPane;

    //Tag for movie details fragment
    private static final String MOVIE_DETAIL_FRAG_TAG = "MDFTAG";

    //Information for the movies
    protected String[] mPosterPaths = null;
    protected int[] mMovieIds = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get the current value of preferences
        SharedPreferences sharedPreferences = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(this);
        mSortingOrder = sharedPreferences.getString(
                getString(R.string.pref_sorting_order_key),
                getString(R.string.pref_sorting_popular));

        //Find out if the device is sw-600dp or not? Try to find out the container
        //defined for sw-600dp devices.
        if (findViewById(R.id.container_fragment_movie_details) != null) {
            mTwoPane = true;

            //Attach the detail fragment to the blank container
            getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment_movie_details,
                    new MovieDetailFragment(),
                    MOVIE_DETAIL_FRAG_TAG).commit();
        } else {

            mTwoPane = false;
        }

        //Also start the task to download movie poster in the background
        mGetMoviesData = null;
        getMovieData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingActivity.class));
            return true;
        }
        if (id == R.id.action_refresh) {
            Log.e(LOG_TAG, "Refresh action!");
            getMovieData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Check if some preference value is changed
        SharedPreferences sharedPreferences = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(this);
        String newSortingOrder = sharedPreferences.getString(
                getString(R.string.pref_sorting_order_key),
                getString(R.string.pref_sorting_popular)
        );

        if (!newSortingOrder.equals(mSortingOrder)) {
            //Execute the async task again
            getMovieData();

            //Update the old value
            mSortingOrder = newSortingOrder;
        }

    }

    //callback for the fragment
    public void onFragmentInteraction(Uri uri)
    {

    }

    //callback for the fragment
    public void onDetailFragmentInteraction(Uri uri)
    {

    }

    //callback for the async task
    public void onPosterPathsAvailable(boolean downloadComplete) {
        //TODO Add checks in async task to check network availability (and may be register itself to be notified when
        //TODO network is available.
        //Handle unavailability of path (due to network unavailability)
        if(downloadComplete != false) {
            //get the associated fragment
            Fragment associatedFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_MoviePosters);
            ((MoviePostersFragment) associatedFragment).mImageAdapter.setmThumbIds(mPosterPaths);
            ((MoviePostersFragment) associatedFragment).mImageAdapter.notifyDataSetChanged();
        }
        else {
            Toast toast = Toast.makeText(this, "Nothing to display!\nCheck network!", Toast.LENGTH_LONG);
            toast.show();
        }

    }


    //Method to launch downloading task of movie posters
    protected void getMovieData()
    {
        //Register itself as a listener to availability of movies data
        mGetMoviesData = new GetMoviesData();

        if(mGetMoviesData.listener == null) {
            mGetMoviesData.listener = this;
        }

        //The task can be executed only once (an exception will be thrown if a second execution is attempted.)
        //Download the movies data from server.
        mGetMoviesData.execute(null, null, null);
    }
}
