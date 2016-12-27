package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
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
public class MovieDetailFragment extends Fragment implements GetMovieDetails.DownloadComplete,
        Button.OnClickListener {

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
    @BindView(R.id.movie_video_separator)
    View mMovieVideoSeparator;
    @BindView(R.id.movie_video_previous)
    Button mMovieVideoPrevious;
    @BindView(R.id.button_separator_first)
    View mButtonSeparatorFirst;
    @BindView(R.id.play_movie_video)
    Button mPlayMovieVideo;
    @BindView(R.id.movie_trailer_number)
    TextView mMovieTrailerNumber;
    @BindView(R.id.movie_video_next)
    Button mMovieVideoNext;
    @BindView(R.id.button_separator_second)
    View mButtonSeparatorSecond;
    @BindView(R.id.movie_review_separator)
    View mMovieReviewSeparator;
    @BindView(R.id.movie_reviews_title)
    TextView mMovieReviewsTitle;
    @BindView(R.id.movie_review)
    TextView mMovieReview;
    @BindView(R.id.movie_reviewer)
    TextView mMovieReviewer;
    @BindView(R.id.review_prev_next_separator)
    Button mReviewPrevNextSeparator;
    @BindView(R.id.review_previous)
    Button mReviewPrevious;
    @BindView(R.id.review_next)
    Button mReviewNext;


    public static final String MOVIE_ID_KEY = "MOVIE_ID";

    //Reference to the async task object
    GetMovieDetails mGetMovieDetails = null;

    //Data structure holding all details of a movie required for the fragment views
    MovieDetails viewData = null;

    //Reference to parent activity
    private OnMovieDetailsUpdatedListener mListener;

    //Used to store the trailer sequence
    private int mTrailerSequenceNumber = -1;

    //Used to store the review sequence
    private int mReviewSequenceNumber = -1;

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

        //Attach click listener for buttons
        setClickListener();

        //if the view data is available them update them
        if (viewData != null) {
            //Update the title
            mTitle.setText(viewData.getmMovieTitle());

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
                    .load(viewData.getmMoviePosterPath())
                    .placeholder(R.drawable.default_preview)
                    .into(mPoster);

            //Update the year of release
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date releaseDate = null;

            try {
                releaseDate = (Date) dateFormat.parse(viewData.getmYearOfRelease());
            } catch (ParseException pEx) {

            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(releaseDate);


            mReleaseDate.setText(String.valueOf(calendar.get(Calendar.YEAR)));

            //Update the running time
            mRunningTime.setText(getActivity().getString(R.string.running_time, viewData.getmRunningTime()));

            //Update the vote average
            mVoteAverage.setText(getActivity().getString(R.string.movie_rating, viewData.getmVoteAverage()));

            //Update the Description
            mMoviePlot.setText(viewData.getmMoviePlot());

            //Set the visibility for the favorite button
            //Also if this movie is not in the favorite list reset color filter
            if(mListener.getFavorites(viewData.getmMovieId()) == null) {
                setColorFilter(null);
            }
            else
            {
                setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY));
            }

            mMarkFavorite.setVisibility(Button.VISIBLE);

            //Set the visibility for the video separator
            mMovieVideoSeparator.setVisibility(View.VISIBLE);

            //Set up views for video section
            setupVideoSection();

            //Set up views for review section
            setUpReviewSection();
        }

        return rootView;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onMovieDetailsUpdated();
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
        mMovieTrailerNumber.clearAnimation();
        mMovieVideoPrevious.clearAnimation();
        mMovieReview.clearAnimation();

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
        public void onMovieDetailsUpdated();
    }


    public void onMovieDetailAvailable(MovieDetails movieDetails) {

        //Update the data for the fragment
        viewData = movieDetails;

        //Forward the update to parent activity
        Activity parentActivity = getActivity();
        ((OnMovieDetailsUpdatedListener) parentActivity).onMovieDetailsUpdated();
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

    //Helper method to set up view(s) required for trailers
    private void setupVideoSection() {

        //If there are no videos for this movie then don't make any view visible
        if(viewData.getNumberOfVideos() != 0) {
            mPlayMovieVideo.setVisibility(Button.VISIBLE);

            mMovieTrailerNumber.setText(getString(R.string.trailer_text) + " " + (++mTrailerSequenceNumber + 1));
            mMovieTrailerNumber.setVisibility(View.VISIBLE);

            //If there are more than one videos then make this visible
            if(viewData.getNumberOfVideos() > 1) {
                mMovieVideoNext.setVisibility(View.VISIBLE);
            }
        }

    }

    //Helper method to set up view(s) required for reviews
    private void setUpReviewSection() {

        //If there are no reviews for the movie then don't make any view visible
        if(viewData.getNumberOfReviews() != 0) {
            mMovieReviewSeparator.setVisibility(View.VISIBLE);
            mMovieReviewsTitle.setVisibility(View.VISIBLE);

            mMovieReview.setText(viewData.getMovieReview(++mReviewSequenceNumber));
            mMovieReview.setVisibility(View.VISIBLE);

            mMovieReviewer.setText(viewData.getMovieReviewAuthor(mReviewSequenceNumber));
            mMovieReviewer.setVisibility(View.VISIBLE);

            //If there are more than one reviews then make this visible
            if(viewData.getNumberOfReviews() > 1) {
                mReviewNext.setVisibility(View.VISIBLE);
            }
        }
    }

    //Helper method to set listener for different buttons
    private void setClickListener() {
        mMovieVideoPrevious.setOnClickListener(this);
        mMovieVideoNext.setOnClickListener(this);
        mPlayMovieVideo.setOnClickListener(this);
        mReviewPrevious.setOnClickListener(this);
        mReviewNext.setOnClickListener(this);
        mMarkFavorite.setOnClickListener(this);
    }

    //Helper method to set the color filter for favorite button drawable
    private void setColorFilter(PorterDuffColorFilter filter) {
        Drawable[] imageDrawable = mMarkFavorite.getCompoundDrawables();

        for(Drawable drawable : imageDrawable)
        {
            if(drawable != null)
            {
                drawable.setColorFilter(filter);
            }
        }
    }

    @Override
    public void onClick(View v) {

        //Get the id of the button to take appropriate actions
        int resourceId = v.getId();

        switch (resourceId) {
            case R.id.movie_video_previous: {
                //Load previous trailer only if mTrailerSequenceNumber is greater than 0
                if (mTrailerSequenceNumber > 0) {
                    //Update the text
                    mMovieTrailerNumber.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_out_right));
                    mMovieTrailerNumber.setText(getString(R.string.trailer_text) + " " + (mTrailerSequenceNumber));
                    mTrailerSequenceNumber--;

                    mMovieVideoNext.setVisibility(View.VISIBLE);
                }

                if (mTrailerSequenceNumber == 0) {
                    mMovieVideoPrevious.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
                    mMovieVideoPrevious.setVisibility(View.INVISIBLE);
                }
            }
            break;

            case R.id.play_movie_video: {
                //TODO Launch an intent to display video on youtube
                Intent videoLaunch = new Intent();
                videoLaunch.setAction(Intent.ACTION_VIEW);
                videoLaunch.setData(Uri.parse(viewData.getMovieVideo(mTrailerSequenceNumber)));

                // Verify that the intent will resolve to an activity
                if (videoLaunch.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(videoLaunch);
                }
            }
            break;

            case R.id.movie_video_next: {
                //Move to next trailer only if there are more trailer left
                if (viewData.getNumberOfVideos() > (mTrailerSequenceNumber + 1)) {
                    //Update the text
                    mMovieTrailerNumber.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left));
                    mMovieTrailerNumber.setText(getString(R.string.trailer_text) + " " + (++mTrailerSequenceNumber + 1));

                    mMovieVideoPrevious.setVisibility(View.VISIBLE);
                }

                if (viewData.getNumberOfVideos() == (mTrailerSequenceNumber + 1)) {
                    mMovieVideoNext.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
                    mMovieVideoNext.setVisibility(View.INVISIBLE);
                }
            }
            break;

            case R.id.review_previous: {
                //Load previous review only if mReviewSequenceNumber is greater than 0
                if (mReviewSequenceNumber > 0) {
                    mReviewSequenceNumber--;

                    //Update the review and reviewer information
                    mMovieReviewer.setText(viewData.getMovieReviewAuthor(mReviewSequenceNumber));
                    mMovieReview.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_out_right));
                    mMovieReview.setText(viewData.getMovieReview(mReviewSequenceNumber));

                }

                //Set the visibility of next to visible
                if (mReviewNext.getVisibility() == View.INVISIBLE) {
                    mReviewNext.setVisibility(View.VISIBLE);
                }

                if (mReviewSequenceNumber == 0) {
                    mReviewPrevious.setVisibility(View.INVISIBLE);
                }
            }
            break;

            case R.id.review_next: {
                //Move to next review only if there are more review left
                if (viewData.getNumberOfReviews() > (mReviewSequenceNumber + 1)) {
                    //Update the review and reviewer information
                    mReviewSequenceNumber++;

                    mMovieReviewer.setText(viewData.getMovieReviewAuthor(mReviewSequenceNumber));
                    mMovieReview.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left));
                    mMovieReview.setText(viewData.getMovieReview(mReviewSequenceNumber));

                }

                //Also make the previous visible
                if (mReviewPrevious.getVisibility() == View.INVISIBLE) {
                    mReviewPrevious.setVisibility(View.VISIBLE);
                }

                if (viewData.getNumberOfReviews() == (mReviewSequenceNumber + 1)) {
                    //Make the next button invisible
                    mReviewNext.setVisibility(View.INVISIBLE);
                }
            }
            break;

            case R.id.mark_favorite:
            {
                //Add to favorite list if not already added
                //TODO viewData has all the data for a movie which can be used
                setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY));
            }
        }
    }
}
