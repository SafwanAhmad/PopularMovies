package com.example.android.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by safwanx on 11/11/16.
 */
public class GetMovieDetails extends AsyncTask<Integer, Void, MovieDetails> {

    public final String LOG_TAG = GetMovieDetails.class.getSimpleName();

    //Reference to the listener for posterPathAvailable
    public DownloadComplete listener = null;

    //Define an interface used as a callback to share the result
    public interface DownloadComplete
    {
        public void onMovieDetailAvailable(MovieDetails movieDetails);
    }


    @Override
    protected MovieDetails doInBackground(Integer... params) {

        //These two are defined outside the try/catch so that
        //they can be closed inside finally block

        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        MovieDetails movieData = null;

        //Take out the movie id from the parameters
        int movieId = params[0];

        //Raw JSON response as a string
        String movieDetailsJson;

        //This is how the URL should look like to get details for a movie
        //https://api.themoviedb.org/3/movie/157336?api_key={api_key}&
        //&append_to_response=videos,reviews

        try
        {
            Uri.Builder builder = new Uri.Builder();

            final String SCHEME = "https";
            final String AUTHORITY = "api.themoviedb.org";
            final String VERSION = "3";
            final String CONTENT_TYPE = "movie";
            final String API_KEY_PARAM = "api_key";
            final String RESPONSE_STRING_KEY = "append_to_response";
            final String[] RESPONSE_STRING = {"videos","reviews"};

            Uri uri = builder.scheme(SCHEME)
                    .authority(AUTHORITY)
                    .appendPath(VERSION)
                    .appendPath(CONTENT_TYPE)
                    .appendPath(String.valueOf(movieId))
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.OPEN_MOVIE_API_KEY)
                    .appendQueryParameter(RESPONSE_STRING_KEY, RESPONSE_STRING[0]+","+RESPONSE_STRING[1])
                    .build();

            URL url = new URL(uri.toString());

            //Create the request to open movie and try to open connection
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            //Read the stream input into a string
            InputStream inputStream = httpURLConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();

            if (inputStream == null) {
                //Nothing to do
                return null;
            }

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging
                stringBuffer.append(line + "\n");
            }

            if (stringBuffer.length() == 0) {
                //Stream was empty no parsing required
                return null;
            }

            //else convert the buffer data to string
            movieDetailsJson = stringBuffer.toString();

            //It's time to parse the JSON
            movieData = getMovieDataFromJson(movieDetailsJson, movieId);

            //Close the data stream
            inputStream.close();

        }
        catch (IOException iExp)
        {
            return null;
        }
        catch (JSONException jEx)
        {
            return null;
        }
        finally {
            //Close the streams
            if(bufferedReader != null)
            {
                try {
                    bufferedReader.close();
                }
                catch (IOException iEx)
                {
                    Log.e(LOG_TAG, "Error closing stream", iEx);
                }
            }

            if(httpURLConnection != null)
            {
                httpURLConnection.disconnect();
            }

        }
        return movieData;
    }


    private MovieDetails getMovieDataFromJson(String moviesInfoJson, int movieId)
            throws JSONException {
        //Define the JSON tags that have to be extracted from the JSON data
        final String ORIGINAL_TITLE = "original_title";
        final String POSTER_PATH = "poster_path";
        final String RELEASE_DATE = "release_date";
        final String RUNNING_TIME = "runtime";
        final String VOTE_AVERAGE = "vote_average";

        final String MOVIE_OVERVIEW = "overview";

        final String MOVIE_VIDEOS = "videos";
        final String MOVIE_REVIEWS = "reviews";

        String basePath = ((Fragment)listener).getActivity().getString(R.string.poster_base_path);

        String[] movieDetails = new String[6];

        //Create a JSON object
        JSONObject jsonObject = new JSONObject(moviesInfoJson);

        //Retrieve title for the movie
        movieDetails[0] = jsonObject.getString(ORIGINAL_TITLE);

        //Retrieve poster path
        movieDetails[1] = basePath + jsonObject.getString(POSTER_PATH);

        //Retrieve release date
        movieDetails[2] = jsonObject.getString(RELEASE_DATE);

        //Retrieve Running time
        movieDetails[3] = jsonObject.getString(RUNNING_TIME);

        //Retrieve vote average
        movieDetails[4] = jsonObject.getString(VOTE_AVERAGE);

        //Retrieve description
        movieDetails[5] = jsonObject.getString(MOVIE_OVERVIEW);

        //Create data structure to save movie information
        //but need to know the size of reviews and videos first
        JSONObject reviews = jsonObject.getJSONObject(MOVIE_REVIEWS);
        JSONArray arrayReviews = reviews.getJSONArray("results");

        JSONObject videos = jsonObject.getJSONObject(MOVIE_VIDEOS);
        JSONArray arrayVideos = videos.getJSONArray("results");

        MovieDetails newMovieDetails = new MovieDetails(movieDetails[0],
                movieDetails[1],
                movieDetails[2],
                movieDetails[3],
                movieDetails[4],
                movieDetails[5],
                arrayVideos.length(),
                arrayReviews.length(),
                String.valueOf(movieId));

        //Start adding videos to list
        String baseVideoUrl = ((Fragment) listener).getActivity().getString(R.string.base_video_url);

        for(int i = 0; i < arrayVideos.length(); i++) {
            //Get the Json object
            JSONObject videoDetails = arrayVideos.getJSONObject(i);
            //Take out the video key
            String videoKey = videoDetails.getString("key");

            Uri.Builder builder = new Uri.Builder();
            builder.encodedPath(baseVideoUrl);
            builder.appendQueryParameter("v",videoKey);

            //Add complete url to list
            newMovieDetails.addVideoToList(builder.toString());
        }

        //Start adding reviews to list
        for (int i = 0; i < arrayReviews.length(); i++) {
            //Get the json object
            JSONObject reviewDetails = arrayReviews.getJSONObject(i);
            //Take out the author and review
            String author = reviewDetails.getString("author");
            String review = reviewDetails.getString("content");

            //Add this to the movie structure
            newMovieDetails.addReviewToList(author, review);
        }

        return newMovieDetails;
    }


    @Override
    protected void onPostExecute(MovieDetails movieData) {
        super.onPostExecute(movieData);

        try {
            //Update the movie information at listener's end
            listener.onMovieDetailAvailable(movieData);
        }
        catch (NullPointerException nEx) {
            Log.w(LOG_TAG, "No listener found!");
        }
    }
}
