package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnMovieDetailsUpdatedListener} interface
 * to handle interaction events.
 */
public class MovieDetailFragment extends Fragment implements GetMovieDetails.DownloadComplete {

    //Jake Wharton's Butterknife library can make binding views a lot easier!
    //Take a look - http://jakewharton.github.io/butterknife/
    @BindView(R.id.movie_title)
    TextView mTitle;
    @BindView(R.id.movie_poster)
    ImageView mPoster;
    @BindView(R.id.release_date)
    TextView mReleaseDate;
    @BindView(R.id.running_time)
    TextView mRunningTime;
    @BindView(R.id.vote_average)
    TextView mVoteAverage;
    @BindView(R.id.movie_plot)
    TextView mMoviePlot;
    @BindView(R.id.mark_favorite)
    Button mMarkFavorite;

    public static final String MOVIE_ID_KEY = "MOVIE_ID";

    //Reference to the async task object
    GetMovieDetails mGetMovieDetails = null;

    //Data for the fragment views
    String[] viewData = null;


    private OnMovieDetailsUpdatedListener mListener;

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Launch the background task to download movie details
        Bundle data = getArguments();
        if (data != null) {
            String movieId = data.getString(MOVIE_ID_KEY);

            //Launch the async task and pass movie id
            mGetMovieDetails = null;
            getMovieDetail(movieId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        //if the view data is available them update them
        if (viewData != null) {
            //Update the title
            mTitle.setText(viewData[0]);

            //Also make it visible
            mTitle.setVisibility(Button.VISIBLE);

            //Update the poster

            //Calculate the height(in pixels) for the image for different devices.
            Resources resources = getActivity().getResources();
            float heightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 280, resources.getDisplayMetrics());

            mPoster.setAdjustViewBounds(true);

            mPoster.setMaxHeight((int) heightPx);

            mPoster.setScaleType(ImageView.ScaleType.FIT_XY);

            Picasso.with(getActivity())
                    .load(viewData[1])
                    .placeholder(R.drawable.default_preview)
                    .into(mPoster);

            //Update the year of release
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date releaseDate = null;

            try {
                releaseDate = (Date) dateFormat.parse(viewData[2]);
            } catch (ParseException pEx) {

            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(releaseDate);


            mReleaseDate.setText(String.valueOf(calendar.get(Calendar.YEAR)));

            //Update the running time
            mRunningTime.setText(getActivity().getString(R.string.running_time, viewData[3]));

            //Update the vote average
            mVoteAverage.setText(getActivity().getString(R.string.movie_rating, viewData[4]));

            //Update the Description
            mMoviePlot.setText(viewData[5]);

            //Set the visibility for the favorite button
            mMarkFavorite.setVisibility(Button.VISIBLE);
        }
        return rootView;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String[] movieDetails) {
        if (mListener != null) {
            mListener.onMovieDetailsUpdated(movieDetails);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMovieDetailsUpdatedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnGridItemClickedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

        //TODO Where should the viewData should be made null?
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Unregister from async task
        if (mGetMovieDetails != null) {
            mGetMovieDetails.listener = null;
            mGetMovieDetails.cancel(true);
        }
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
    public interface OnMovieDetailsUpdatedListener {
        public void onMovieDetailsUpdated(String[] movieDetails);
    }


    public void onMovieDetailAvailable(String[] movieDetails) {
        viewData = movieDetails;
        //Forward the update to parent activity
        Activity parentActivity = getActivity();
        ((OnMovieDetailsUpdatedListener) parentActivity).onMovieDetailsUpdated(movieDetails);
    }

    protected void getMovieDetail(String movieId) {

        //Before moving further, check if network is available or not
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            mGetMovieDetails = new GetMovieDetails();

            //Register itself as a listener to availability of movie details
            if (mGetMovieDetails.listener == null) {
                mGetMovieDetails.listener = this;
            }


            //The task can be executed only once (an exception will be thrown if a second execution is attempted.)
            //Download the movies data from server.
            mGetMovieDetails.execute(Integer.parseInt(movieId));
        } else {
            Toast toast = Toast.makeText(getActivity(), "Network not available!", Toast.LENGTH_LONG);
            toast.show();
        }

    }

}
