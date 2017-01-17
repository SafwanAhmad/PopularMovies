package com.example.android.popularmovies;

/**
 * The object of <code>MovieDetails</code> class represents data structure storing all the required
 * details for any movie.
 * Created by safwanx on 12/15/16.
 */

public class MovieDetails {

    private int mReviewsIndex;
    private int mVideosIndex;


    //Data structures to hold movie reviews and videos. Hash map contains
    //<Author,Review> pair.
    private MovieReviews[] mMovieReviews;
    private String[] mVideos;

    //Note: Values numberOfVideos and numberOfReviews can be zero, hence
    //No data will be there but a zero size array would be there.
    public MovieDetails(int numberOfVideos, int numberOfReviews)
    {
        mReviewsIndex = mVideosIndex = -1;
        mMovieReviews = new MovieReviews[numberOfReviews];
        mVideos = new String[numberOfVideos];
    }

    //Method to append review to map
    public void addReviewToList(String author, String review) {
        mMovieReviews[++mReviewsIndex] = new MovieReviews(author, review);
    }

    //Method to append video to list
    public void addVideoToList(String url) {
        mVideos[++mVideosIndex] = url;
    }

    //Method to retrieve review Author information
    public String getMovieReviewAuthor(int position)
    {
        return (mMovieReviews.length == 0 ? null : mMovieReviews[position].getmReviewAuthor());
    }

    //Method to retrieve review information
    public String getMovieReview(int position) {
        return (mMovieReviews.length == 0 ? null : mMovieReviews[position].getmReview());
    }

    //Method to retrieve video information
    public String getMovieVideo(int position) {
        return (mVideos.length == 0 ? null : mVideos[position]);
    }

    //Method to retrieve list of videos
    public String[] getMovieVideos() {
        return mVideos;
    }

    //Method to retrieve review infomation
    public MovieReviews[] getmMovieReviews() {
        return mMovieReviews;
    }

    //Method to retrieve number of videos available
    public int getNumberOfVideos()
    {
        return mVideos.length;
    }

    //Method to retrieve number of reviews available
    public int getNumberOfReviews() {
        return mMovieReviews.length;
    }

    //Special data structure to hold movie reviews
    private class MovieReviews
    {
        private String mReviewAuthor;
        private String mReview;

        public String getmReviewAuthor() {
            return mReviewAuthor;
        }

        public String getmReview() {
            return mReview;
        }

        public MovieReviews(String author, String review) {
            mReviewAuthor = author;
            mReview = review;
        }
    }

}
