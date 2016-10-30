package com.example.android.popularmovies;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity
        implements MoviePostersFragment.OnFragmentInteractionListener, GetMoviesData.DownloadComplete
{
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    //Reference to async task object
    GetMoviesData mGetMoviesData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Also start the task to download movie poster in the background
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
            return true;
        }
        if (id == R.id.action_refresh) {
            Log.e(LOG_TAG, "Refresh action!");
            getMovieData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //callback for the fragment
    public void onFragmentInteraction(Uri uri)
    {

    }

    //callback for the async task
    public void onPosterPathsAvailable(String[] posterPaths) {
        //TODO Add checks in async task to check network availability (and may be register itself to be notified when
        //TODO network is available.
        //Handle unavailability of path (due to network unavailability)
        if(posterPaths != null) {
            //get the associated fragment
            Fragment associatedFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_MoviePosters);
            ((MoviePostersFragment) associatedFragment).mImageAdapter.setmThumbIds(posterPaths);
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
