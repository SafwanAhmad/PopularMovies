package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity
        implements MoviePostersFragment.OnGridItemClickedListener,
        MovieDetailFragment.OnMovieDetailsUpdatedListener {
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

            //Attach the detail fragment to the blank container if state is not being restored
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment_movie_details,
                        new MovieDetailFragment(),
                        MOVIE_DETAIL_FRAG_TAG).commit();
            }
        } else {

            mTwoPane = false;
        }

        //Inform movie poster fragment about mTwoPane
        Fragment posterFragment = (Fragment) getSupportFragmentManager().findFragmentById(R.id.fragment_MoviePosters);
        ((MoviePostersFragment)posterFragment).setTypeOfDevice(mTwoPane);

        //Also start the task to download movie poster in the background if the
        //current sort order is popular or top rated
        if (!mSortingOrder.equals(getString(R.string.pref_sorting_favorite))) {
            mGetMoviesData = null;
            getMovieData();
        }

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
            startActivity(new Intent(this, SettingActivity.class));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Unregister from async task
        if (mGetMoviesData != null) {
            mGetMoviesData.listener = null;
            mGetMoviesData.cancel(true);
        }
    }

    //callback method, it is called by attached fragment while any item
    // inside is clicked
    public void onMoviePosterClicked(String movieId) {
        //Take out the movie id
        if (mTwoPane == true) {
            //We want to pass the uri also to this new fragment. We will use bundle for this purpose
            Bundle id = new Bundle();
            id.putString(MovieDetailFragment.MOVIE_ID_KEY, movieId);

            MovieDetailFragment detailFragment = new MovieDetailFragment();
            detailFragment.setArguments(id);

            getSupportFragmentManager().beginTransaction().
                    replace(R.id.container_fragment_movie_details, detailFragment, MOVIE_DETAIL_FRAG_TAG).commit();
        }

        //If the device doesn't support two pane launch detail activity
        //through an intent
        else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailActivity.MOVIE_ID_KEY, movieId);
            startActivity(intent);
        }

    }

    //callback for the fragment
    public void onMovieDetailsUpdated() {

        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(MOVIE_DETAIL_FRAG_TAG);

        ft.detach(fragment).attach(fragment).commit();
    }


    // Method to launch downloading task of movie posters:
    // In case the sorting order being popular or top rated we connect to server (The Movie Database)
    // and get the listed movies under that sorting order. First we check if there network is available
    // or not. If the network is available then this method creates an async task and connect to server.
    // Async task will write to the respective table in the database with the help of Content Resolver.
    protected void getMovieData() {

        //Before moving further, check if network is available or not
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            //Register itself as a listener to availability of movies data
            mGetMoviesData = new GetMoviesData();

            if (mGetMoviesData.listener == null) {
                mGetMoviesData.listener = this;
            }

            //The task can be executed only once (an exception will be thrown if a second execution is attempted.)
            //Download the movies data from server.
            mGetMoviesData.execute(null, null, null);
        } else {
            Toast toast = Toast.makeText(this, "Network not available!", Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
