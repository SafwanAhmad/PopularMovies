package com.example.android.popularmovies;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity
        implements MoviePostersFragment.OnFragmentInteractionListener, GetMoviesData.DownloadComplete
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Register itself as a listener to availability of movies data
        GetMoviesData getMoviesData = new GetMoviesData();
        getMoviesData.listener = this;

        //Download the movies data from server.
        getMoviesData.execute(null, null, null);
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

        return super.onOptionsItemSelected(item);
    }


    //callback for the fragment
    public void onFragmentInteraction(Uri uri)
    {

    }

    //callback for the async task
    public void onPosterPathsAvailable(String[] posterPaths) {
        //get the associated fragment
        Fragment associatedFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_MoviePosters);
        ((MoviePostersFragment)associatedFragment).mImageAdapter.setmThumbIds(posterPaths);
        ((MoviePostersFragment) associatedFragment).mImageAdapter.notifyDataSetChanged();
    }
}
