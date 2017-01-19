package com.example.android.popularmovies;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnGridItemClickedListener} interface
 * to handle interaction events.
 */
public class MoviePostersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    //The reference to the adapter connected to the grid view
    ImageAdapter mImageAdapter = null;

    //Parent activity to which this fragment is attached
    private OnGridItemClickedListener mListener;

    //Projection string to be used for cursor loader
    private static final String[] PROJECTION_POPULAR = {
            MovieContract.Popular._ID,
            MovieContract.Popular.COLUMN_MOVIE_POSTER_PATH
    };

    private static final String[] PROJECTION_TOP_RATED = {
            MovieContract.TopRated._ID,
            MovieContract.TopRated.COLUMN_MOVIE_POSTER_PATH
    };

    private static final String[] PROJECTION_FAVORITE = {
            MovieContract.Favorite._ID,
            MovieContract.Favorite.COLUMN_MOVIE_POSTER_PATH
    };

    //Based on above projections the index for columns. So if the projection changes
    //this too have to be changed. The cursor returned will have columns in this order
    //and follow these indices for columns
    static final int COLUMN_ID = 0;
    static final int COLUMN_MOVIE_POSTER_PATH = 1;


    //Create three loaders each for available sorting orders
    private final int loaderPopularId = 0;
    private final int loaderTopRatedId = 1;
    private final int loaderFavoriteId = 2;

    public MoviePostersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Get the type of loader based on the setting
        String sortingOrder = getSortingOrder();

        if (sortingOrder.equals(getString(R.string.pref_sorting_popular))) {
            //Initialize loader
            getLoaderManager().initLoader(loaderPopularId, savedInstanceState, this);
        } else if (sortingOrder.equals(getString(R.string.pref_sorting_top_rated))) {
            getLoaderManager().initLoader(loaderTopRatedId, savedInstanceState, this);
        } else if (sortingOrder.equals(getString(R.string.pref_sorting_favorite))) {
            getLoaderManager().initLoader(loaderFavoriteId, savedInstanceState, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentMoviePosters = inflater.inflate(R.layout.fragment_movie_posters, container, false);

        //Create the adapter without the cursor
        mImageAdapter = new ImageAdapter(getActivity(), null, 0);

        //Accessing the grid view inside the this fragment
        GridView gridView = (GridView) fragmentMoviePosters.findViewById(R.id.main_grid_view);

        //Attach a adapter with this grid view
        gridView.setAdapter(mImageAdapter);

        //Add the click listener for grid view (anonymous class)
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //For debugging purpose
                Toast.makeText(getActivity(), "Item[" + position + "] clicked!", Toast.LENGTH_SHORT).show();
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String movieId = cursor.getString(COLUMN_ID);
                onPosterClicked(movieId);
            }
        });

        return fragmentMoviePosters;
    }

    public void onPosterClicked(String movieId) {
        if (mListener != null) {
            mListener.onMoviePosterClicked(movieId);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnGridItemClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnGridItemClickedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        CursorLoader cursorLoader = null;

        switch (id) {
            case loaderPopularId: {
                cursorLoader = new CursorLoader(getContext(),
                        MovieContract.Popular.CONTENT_URI,
                        PROJECTION_POPULAR,
                        null,
                        null,
                        null);
            }
            break;

            case loaderTopRatedId: {
                cursorLoader = new CursorLoader(getContext(),
                        MovieContract.TopRated.CONTENT_URI,
                        PROJECTION_TOP_RATED,
                        null,
                        null,
                        null);
            }
            break;

            case loaderFavoriteId: {
                cursorLoader = new CursorLoader(getContext(),
                        MovieContract.Favorite.CONTENT_URI,
                        PROJECTION_FAVORITE,
                        null,
                        null,
                        null);
            }
            break;

            default: {
                throw new IllegalArgumentException("Loader id does not match");
            }
        }


        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mImageAdapter.swapCursor((Cursor) data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mImageAdapter.swapCursor(null);
    }

    //Helper method to find the current sorting order
    private String getSortingOrder() {
        SharedPreferences preferences = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(getContext());

        String sortingOrder = preferences.getString(
                getString(R.string.pref_sorting_order_key),
                getString(R.string.pref_sorting_popular)
        );

        return sortingOrder;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnGridItemClickedListener {
        public void onMoviePosterClicked(String movieId);
    }

}
