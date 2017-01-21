package com.example.android.popularmovies;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Intent.ACTION_SEND;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnMovieDetailsUpdatedListener} interface
 * to handle interaction events.
 */
public class MovieDetailFragment extends Fragment implements GetMovieDetails.DownloadComplete,
        Button.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

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

    //Reference to provider for share intent
    ShareActionProvider mShareActionProvider = null;

    //These value are updated by loader, and can be used to set in database if favorite button is
    //clicked
    private String mMovieID = null;
    private String mTitleCurrent = null;
    private String mPosterPathCurrent = null;
    private String mReleaseDateCurrent = null;
    private String mRunningTimeCurrent = null;
    private String mUserRatingCurrent = null;
    private String mMoviePlotCurrent = null;


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

    //Projection string to be used for cursor loader
    private static final String[] PROJECTION_POPULAR = {
            //To check if this is a favorite movie we need ids
            MovieContract.Popular._ID,
            MovieContract.Popular.COLUMN_MOVIE_TITLE,
            MovieContract.Popular.COLUMN_MOVIE_POSTER_PATH,
            MovieContract.Popular.COLUMN_MOVIE_RELEASE_DATE,
            MovieContract.Popular.COLUMN_MOVIE_RUNNING_TIME,
            MovieContract.Popular.COLUMN_MOVIE_USER_RATING,
            MovieContract.Popular.COLUMN_MOVIE_PLOT
    };

    private static final String[] PROJECTION_TOP_RATED = {
            MovieContract.TopRated._ID,
            MovieContract.TopRated.COLUMN_MOVIE_TITLE,
            MovieContract.TopRated.COLUMN_MOVIE_POSTER_PATH,
            MovieContract.TopRated.COLUMN_MOVIE_RELEASE_DATE,
            MovieContract.TopRated.COLUMN_MOVIE_RUNNING_TIME,
            MovieContract.TopRated.COLUMN_MOVIE_USER_RATING,
            MovieContract.TopRated.COLUMN_MOVIE_PLOT
    };

    private static final String[] PROJECTION_FAVORITE = {
            MovieContract.Favorite._ID,
            MovieContract.Favorite.COLUMN_MOVIE_TITLE,
            MovieContract.Favorite.COLUMN_MOVIE_POSTER_PATH,
            MovieContract.Favorite.COLUMN_MOVIE_RELEASE_DATE,
            MovieContract.Favorite.COLUMN_MOVIE_RUNNING_TIME,
            MovieContract.Favorite.COLUMN_MOVIE_USER_RATING,
            MovieContract.Favorite.COLUMN_MOVIE_PLOT
    };

    //Based on above projections the index for columns. So if the projection changes
    //this too have to be changed. The cursor returned will have columns in this order
    //and follow these indices for columns
    static final int COLUMN_ID = 0;
    static final int COLUMN_MOVIE_TITLE = 1;
    static final int COLUMN_MOVIE_POSTER_PATH = 2;
    static final int COLUMN_MOVIE_RELEASE_DATE = 3;
    static final int COLUMN_MOVIE_RUNNING_TIME = 4;
    static final int COLUMN_MOVIE_USER_RATING = 5;
    static final int COLUMN_MOVIE_PLOT = 6;


    //Create three loaders each for available sorting orders
    private final int loaderPopularId = 3;
    private final int loaderTopRatedId = 4;
    private final int loaderFavoriteId = 5;


    public MovieDetailFragment() {
        // Required empty public constructor
        //This fragment has menu items of its own
        setHasOptionsMenu(true);
    }

    //The initialization of loaders should be done here, so that we are confirmed that parent
    //activity has been created.
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Get the type of loader based on the setting
        String sortingOrder = getSortingOrder();

        //Check if some movie poster is clicked (in case of two pane UI fragment is attached)
        if (mMovieID != null) {
            if (sortingOrder.equals(getString(R.string.pref_sorting_popular))) {
                //Initialize loader
                getLoaderManager().initLoader(loaderPopularId, null, this);
            } else if (sortingOrder.equals(getString(R.string.pref_sorting_top_rated))) {
                getLoaderManager().initLoader(loaderTopRatedId, savedInstanceState, this);
            } else if (sortingOrder.equals(getString(R.string.pref_sorting_favorite))) {
                getLoaderManager().initLoader(loaderFavoriteId, savedInstanceState, this);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Launch the background task to download movie details
        Bundle data = getArguments();
        if (data != null) {
            mMovieID = data.getString(MOVIE_ID_KEY);

            //Launch the async task and pass movie id
            mGetMovieDetails = null;
            getMovieDetail(mMovieID);
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        ButterKnife.bind(this, rootView);

        //Attach click listener for buttons
        setClickListener();

        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detail_fragment, menu);

        //Find the menu item we want to attach
        MenuItem shareItem = (MenuItem) menu.findItem(R.id.action_share);

        //Get a reference to the ShareActionProvider by calling getActionProvider()
        // and passing the share action's MenuItem.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        //Check if the async task is done loading and videos url are available,
        //then we can set the share intent
        if (viewData != null) {
            mShareActionProvider.setShareIntent(createVideoShareIntent());
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
        //Where should the viewData should be made null?
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;

        switch (id) {
            case loaderPopularId: {
                //Build uri corresponding to single item, which will be converted in the appropriate
                //query by our content provider.
                Uri uriItem = MovieContract.Popular.CONTENT_URI.buildUpon().appendPath(mMovieID).build();
                cursorLoader = new CursorLoader(getContext(),
                        uriItem,
                        PROJECTION_POPULAR,
                        null,
                        null,
                        null);
            }
            break;

            case loaderTopRatedId: {
                //Build uri corresponding to single item, which will be converted in the appropriate
                //query by our content provider.
                Uri uriItem = MovieContract.TopRated.CONTENT_URI.buildUpon().appendPath(mMovieID).build();
                cursorLoader = new CursorLoader(getContext(),
                        uriItem,
                        PROJECTION_TOP_RATED,
                        null,
                        null,
                        null);
            }
            break;

            case loaderFavoriteId: {
                //Build uri corresponding to single item, which will be converted in the appropriate
                //query by our content provider.
                Uri uriItem = MovieContract.Favorite.CONTENT_URI.buildUpon().appendPath(mMovieID).build();
                cursorLoader = new CursorLoader(getContext(),
                        uriItem,
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

        if (data != null && data.moveToFirst()) {
            //In this case we don't have any adapter to work for us. So have to update views.
            //Update the title
            mTitleCurrent = data.getString(COLUMN_MOVIE_TITLE);
            mTitle.setText(mTitleCurrent);
            //Also make it visible
            mTitle.setVisibility(Button.VISIBLE);

            //Update the poster
            //Calculate the height(in pixels) for the image for different devices.
            Resources resources = getActivity().getResources();
            float heightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 280, resources.getDisplayMetrics());

            mPoster.setAdjustViewBounds(true);

            mPoster.setMaxHeight((int) heightPx);

            mPoster.setScaleType(ImageView.ScaleType.FIT_XY);

            //Check if network is available
            ConnectivityManager connectivityManager = (ConnectivityManager) getContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            boolean isConnected = networkInfo != null &&
                    networkInfo.isConnectedOrConnecting();

            //Save the poster path so that if user clicks favorite button we can write this
            //path
            mPosterPathCurrent = data.getString(COLUMN_MOVIE_POSTER_PATH);
            if (isConnected) {
                Picasso.with(getActivity())
                        .load(mPosterPathCurrent)
                        .placeholder(R.drawable.default_preview)
                        .into(mPoster);
            } else {
                Picasso.with(getActivity())
                        .load(mPosterPathCurrent)
                        .placeholder(R.drawable.default_preview)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(mPoster);
            }

            //Update the year of release
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date releaseDate = null;

            try {
                mReleaseDateCurrent = data.getString(COLUMN_MOVIE_RELEASE_DATE);
                releaseDate = (Date) dateFormat.parse(mReleaseDateCurrent);
            } catch (ParseException pEx) {

            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(releaseDate);

            mReleaseDate.setText(String.valueOf(calendar.get(Calendar.YEAR)));

            //Update the running time
            mRunningTimeCurrent = data.getString(COLUMN_MOVIE_RUNNING_TIME);
            mRunningTime.setText(getActivity().getString(R.string.running_time, mRunningTimeCurrent));

            //Update the vote average
            mUserRatingCurrent = data.getString(COLUMN_MOVIE_USER_RATING);
            mVoteAverage.setText(getActivity().getString(R.string.movie_rating, mUserRatingCurrent));

            //Update the Description
            mMoviePlotCurrent = data.getString(COLUMN_MOVIE_PLOT);
            mMoviePlot.setText(mMoviePlotCurrent);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e("Hello World!", "Inside Finished");
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


    //This is a callback function, that is called by async task (launched on a separate thread)
    //when it is ready with the results.
    public void onMovieDetailAvailable(MovieDetails movieDetails) {

        //Update the data for the fragment
        viewData = movieDetails;

        //Also if this movie is not in the favorite list reset color filter
        if (isFavorite() == false) {
            setColorFilter(null);
        } else {
            setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY));
        }

        mMarkFavorite.setVisibility(Button.VISIBLE);

        //Set the visibility for the video separator
        mMovieVideoSeparator.setVisibility(View.VISIBLE);

        //Set up views for video section
        setupVideoSection();

        //Set up views for review section
        setUpReviewSection();

        //At this place we check if a share intent provider exists, then we need to update
        //the share intent now
        if(mShareActionProvider != null)
        {
            mShareActionProvider.setShareIntent(createVideoShareIntent());
        }

        //Forward the update to parent activity
        //Activity parentActivity = getActivity();
        //((OnMovieDetailsUpdatedListener) parentActivity).onMovieDetailsUpdated();
    }

    //Helper method to download movie running time, videos and reviews. If there is some network available
    //then it will launch an async task.
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
        if (viewData.getNumberOfVideos() != 0) {
            mPlayMovieVideo.setVisibility(Button.VISIBLE);

            mMovieTrailerNumber.setText(getString(R.string.trailer_text) + " " + (++mTrailerSequenceNumber + 1));
            mMovieTrailerNumber.setVisibility(View.VISIBLE);

            //If there are more than one videos then make this visible
            if (viewData.getNumberOfVideos() > 1) {
                mMovieVideoNext.setVisibility(View.VISIBLE);
            }
            mMovieReviewSeparator.setVisibility(View.VISIBLE);
        }

    }

    //Helper method to set up view(s) required for reviews
    private void setUpReviewSection() {

        //If there are no reviews for the movie then don't make any view visible
        if (viewData.getNumberOfReviews() != 0) {
            mMovieReviewsTitle.setVisibility(View.VISIBLE);

            mMovieReview.setText(viewData.getMovieReview(++mReviewSequenceNumber));
            mMovieReview.setVisibility(View.VISIBLE);

            mMovieReviewer.setText(viewData.getMovieReviewAuthor(mReviewSequenceNumber));
            mMovieReviewer.setVisibility(View.VISIBLE);

            //If there are more than one reviews then make this visible
            if (viewData.getNumberOfReviews() > 1) {
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

        for (Drawable drawable : imageDrawable) {
            if (drawable != null) {
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
                //Launch an intent to display video on youtube
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

            case R.id.mark_favorite: {
                //Check if the movie is in the database or not
                boolean isAdded = isFavorite();

                //If the movie is already added in the database then remove it
                if (isAdded) {
                    Uri itemUri = MovieContract.Favorite.CONTENT_URI.buildUpon().appendPath(mMovieID).build();
                    //Delete this movie from database with the help of content resolver
                    int rowsDeleted = getContext().getContentResolver().delete(itemUri,
                            null,
                            null);

                    if (rowsDeleted != 0) {
                        //Update the favorite button drawable color
                        setColorFilter(null);
                    }
                }
                //Add to favorite list if not already added
                //Find out the movie id
                if (mMovieID != null && !isAdded) {
                    //We need to insert this movie information into favorite table
                    String movieId = mMovieID;
                    String title = mTitleCurrent;
                    String posterPath = mPosterPathCurrent;
                    String releaseDate = mReleaseDateCurrent;
                    String runningTime = mRunningTimeCurrent;
                    String userRating = mUserRatingCurrent;
                    String moviePlot = mMoviePlotCurrent;

                    ContentValues values = new ContentValues();
                    values.put(MovieContract.Favorite._ID, movieId);
                    values.put(MovieContract.Favorite.COLUMN_MOVIE_TITLE, title);
                    values.put(MovieContract.Favorite.COLUMN_MOVIE_POSTER_PATH, posterPath);
                    values.put(MovieContract.Favorite.COLUMN_MOVIE_RUNNING_TIME, runningTime);
                    values.put(MovieContract.Favorite.COLUMN_MOVIE_RELEASE_DATE, releaseDate);
                    values.put(MovieContract.Favorite.COLUMN_MOVIE_USER_RATING, userRating);
                    values.put(MovieContract.Favorite.COLUMN_MOVIE_PLOT, moviePlot);

                    getContext().getContentResolver().insert(
                            MovieContract.Favorite.CONTENT_URI,
                            values
                    );
                    //Change the color of favorite button drawable
                    setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY));
                }
            }
        }
    }

    //Helper method to find out if a movie exists in favorite table or not
    private boolean isFavorite() {
        if (mMovieID != null) {
            //Build the uri to select a row(s).
            Uri itemUri = MovieContract.Favorite.CONTENT_URI.buildUpon().appendPath(mMovieID).build();

            Cursor cursor = getContext().getContentResolver().query(itemUri,
                    new String[]{MovieContract.Favorite._ID},
                    null,
                    null,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                return true;
            }
        }
        return false;
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

    //Helper method to create a share intent for sharing the first video url
    private Intent createVideoShareIntent()
    {
        Intent videoIntent = new Intent(ACTION_SEND);
        videoIntent.setType("text/plain");

        //Some movies do not have any video
        if(viewData.getNumberOfVideos() != 0) {
            videoIntent.putExtra(Intent.EXTRA_TEXT, viewData.getMovieVideo(0));
        }
        else {
            videoIntent.putExtra(Intent.EXTRA_TEXT, "No Videos For this movie!");
        }
        return videoIntent;
    }
}
