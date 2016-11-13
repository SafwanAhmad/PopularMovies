package com.example.android.popularmovies;

import android.app.Activity;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieDetailFragment.OnDetailFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class MovieDetailFragment extends Fragment implements GetMovieDetails.DownloadComplete{

    public static final String MOVIE_ID_KEY =  "MOVIE_ID";

    //Reference to the async task object
    GetMovieDetails mGetMovieDetails = null;

    //Data for the fragment views
    String[] viewData = null;


    private OnDetailFragmentInteractionListener mListener;

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

        //if the view data is available them update them
        if(viewData != null) {
            //Update the title
            TextView title = (TextView) rootView.findViewById(R.id.movie_title);
            title.setText(viewData[0]);

            //Also make it visible
            title.setVisibility(Button.VISIBLE);

            //Update the poster
            ImageView poster = (ImageView)rootView.findViewById(R.id.movie_poster);

            //Calculate the height(in pixels) for the image for different devices.
            Resources resources = getActivity().getResources();
            float heightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 280, resources.getDisplayMetrics());

            poster.setAdjustViewBounds(true);

            poster.setMaxHeight((int)heightPx);

            poster.setScaleType(ImageView.ScaleType.FIT_XY);

            Picasso.with(getActivity())
                    .load(viewData[1])
                    .placeholder(R.drawable.default_preview)
                    .into(poster);

            //Update the year of release
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date releaseDate = null;

            try{
                releaseDate = (Date)dateFormat.parse(viewData[2]);
            }
            catch (ParseException pEx)
            {

            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(releaseDate);


            ((TextView) rootView.findViewById(R.id.release_date))
                    .setText(String.valueOf(calendar.get(Calendar.YEAR)));

            //Update the running time
            ((TextView)rootView.findViewById(R.id.running_time))
                    .setText(getActivity().getString(R.string.running_time,viewData[3]));

            //Update the vote average
            ((TextView)rootView.findViewById(R.id.vote_average))
                    .setText(getActivity().getString(R.string.movie_rating,viewData[4]));

            //Update the Description
            ((TextView)rootView.findViewById(R.id.movie_plot))
                    .setText(viewData[5]);

            //Set the visibility for the favorite button
            ((Button)rootView.findViewById(R.id.mark_favorite))
            .setVisibility(Button.VISIBLE);
        }
        return rootView;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String[] movieDetails) {
        if (mListener != null) {
            mListener.onDetailFragmentInteraction(movieDetails);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnDetailFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
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
        if(mGetMovieDetails != null)
        {
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
    public interface OnDetailFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onDetailFragmentInteraction(String[] movieDetails);
    }


    public void onMovieDetailAvailable(String[] movieDetails)
    {
        viewData = movieDetails;
        //Forward the update to parent activity
        Activity parentActivity = getActivity();
        ((OnDetailFragmentInteractionListener)parentActivity ).onDetailFragmentInteraction(movieDetails);
    }

    protected void getMovieDetail(String movieId) {

        mGetMovieDetails = new GetMovieDetails();

        //Register itself as a listener to availability of movie details
        if(mGetMovieDetails.listener == null) {
            mGetMovieDetails.listener = this;
        }


        //The task can be executed only once (an exception will be thrown if a second execution is attempted.)
        //Download the movies data from server.
        mGetMovieDetails.execute(Integer.parseInt(movieId));
    }

}
