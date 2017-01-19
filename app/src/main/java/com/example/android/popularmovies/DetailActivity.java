package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class DetailActivity extends AppCompatActivity implements MovieDetailFragment.OnMovieDetailsUpdatedListener {

    public static final String MOVIE_ID_KEY = "MOVIE_ID";

    //Tag for movie details fragment
    private static final String MOVIE_DETAIL_FRAG_TAG = "MDFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //This activity is launched via intent when device doesn't
        //support two pane UI
        if (savedInstanceState == null) {
            Bundle id = new Bundle();
            id.putString(MovieDetailFragment.MOVIE_ID_KEY, getIntent().getStringExtra(MOVIE_ID_KEY));

            MovieDetailFragment detailFragment = new MovieDetailFragment();
            detailFragment.setArguments(id);

            getSupportFragmentManager().beginTransaction().
                    add(R.id.container_fragment_movie_details, detailFragment, MOVIE_DETAIL_FRAG_TAG).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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

    @Override
    public void onMovieDetailsUpdated() {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(MOVIE_DETAIL_FRAG_TAG);

        ft.detach(fragment).attach(fragment).commit();
    }
}
