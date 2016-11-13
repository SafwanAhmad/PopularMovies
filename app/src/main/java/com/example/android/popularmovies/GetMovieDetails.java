package com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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
public class GetMovieDetails extends AsyncTask<Integer, Void, String[]> {

    public final String LOG_TAG = GetMovieDetails.class.getSimpleName();

    //Reference to the listener for posterPathAvailable
    public DownloadComplete listener = null;

    //Define an interface used as a callback to share the result
    public interface DownloadComplete
    {
        public void onMovieDetailAvailable(String[] movieDetails);
    }


    @Override
    protected String[] doInBackground(Integer... params) {

        //These two are defined outside the try/catch so that
        //they can be closed inside finally block

        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        String[] movieData = null;

        //Take out the movie id from the parameters
        int movieId = params[0];

        //Raw JSON response as a string
        String moviesInfoJson;

        //This is how the URL should look like to get details for a movie
        //https://api.themoviedb.org/3/movie/157336?api_key={api_key}

        try
        {
            final String MOVIE_BASE_URL = "https://api.themoviedb.org";
            final String API_VERSION = "3";
            final String CONTENT_TYPE = "movie";


            final String API_KEY_PARAM = "api_key";

            Uri uri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(API_VERSION)
                    .appendPath(CONTENT_TYPE)
                    .appendPath(String.valueOf(movieId))
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.OPEN_MOVIE_API_KEY)
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
            moviesInfoJson = stringBuffer.toString();

            //It's time to parse the JSON
            movieData = getMovieDataFromJson(moviesInfoJson);

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


    private String[] getMovieDataFromJson(String moviesInfoJson)
            throws JSONException {
        //Define the JSON tags that have to be extracted from the JSON data
        final String ORIGINAL_TITLE = "original_title";
        final String POSTER_PATH = "poster_path";
        final String RELEASE_DATE = "release_date";
        final String RUNNING_TIME = "runtime";
        final String VOTE_AVERAGE = "vote_average";

        final String MOVIE_OVERVIEW = "overview";
        String basePath = "http://image.tmdb.org/t/p/w185/";

        //Create array for movie information
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

        return movieDetails;
    }


    @Override
    protected void onPostExecute(String[] movieData) {
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
