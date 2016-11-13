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
 * Created by safwanx on 10/11/16.
 */
public class GetMoviesData extends AsyncTask<Void, Void, GetMoviesData.MovieData> {

    public final String LOG_TAG = GetMoviesData.class.getSimpleName();

    //Reference to the listener for posterPathAvailable
    public DownloadComplete listener = null;

    //Define an interface used as a callback to share the result
    public interface DownloadComplete
    {
        public void onPosterPathsAvailable();
    }


    //TODO Initially we are retriving the first page. Later we
    //TODO will add support for more pages to be retrieved.
    //TODO Next step will be to provide user the option to
    //TODO navigate back and forth.
    //TODO Finally a infinite scrolling support will be added.
    @Override
    protected MovieData doInBackground(Void... params) {

        //These two are defined outside the try/catch so that
        //they can be closed inside finally block

        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        MovieData movieData = null;

        //Raw JSON response as a string
        String moviesInfoJson;

        try
        {
            final String MOVIE_BASE_URL = "https://api.themoviedb.org";
            final String API_VERSION = "3";
            final String CONTENT_TYPE = "movie";

            //This is set based on user preference
            Context currentContext = (Context)listener;
            SharedPreferences sharedPreferences = (SharedPreferences) PreferenceManager
                    .getDefaultSharedPreferences(currentContext);


            final String MOVIE_ORDER = sharedPreferences.getString(
                    currentContext.getString(R.string.pref_sorting_order_key),
                            currentContext.getString(R.string.pref_sorting_popular)
            );

            final String API_KEY_PARAM = "api_key";

            Uri uri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(API_VERSION)
                    .appendPath(CONTENT_TYPE)
                    .appendPath(MOVIE_ORDER)
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


    private MovieData getMovieDataFromJson(String moviesInfoJson)
            throws JSONException {
        //Define the JSON tags that have to be extracted from the JSON data
        final String POSTER_PATH = "poster_path";
        final String MOVIE_ID = "id";
        final String ROOT_KEY = "results";

        //Create a JSON object
        JSONObject jsonObject = new JSONObject(moviesInfoJson);

        //Retrieve the JSON array of objects containing the movies information
        JSONArray moviesData = jsonObject.getJSONArray(ROOT_KEY);

        //Create array for poster path and movie ids
        String[] posterPaths = new String[moviesData.length()];
        int[] movieIds = new int[moviesData.length()];

        String basePath = "http://image.tmdb.org/t/p/w185/";

        //Loop over this JSON array to retrieve the each movie information
        for (int i = 0; i < moviesData.length(); i++) {

            //Retrieve the poster path and id
            JSONObject currentMovieData = moviesData.getJSONObject(i);
            String posterPath = currentMovieData.getString(POSTER_PATH);
            int movieId = currentMovieData.getInt(MOVIE_ID);

            //Add poster path and id to array
            posterPaths[i] = basePath + posterPath;
            movieIds[i] = movieId;
        }

        MovieData movieData = new MovieData(posterPaths, movieIds);

        return movieData;
    }


    @Override
    protected void onPostExecute(MovieData movieData) {
        super.onPostExecute(movieData);

        try {
            //Update the movie information at listener's end
            ((MainActivity) listener).mPosterPaths = movieData.mPosterUrl;
            ((MainActivity) listener).mMovieIds = movieData.mMovieIds;

            listener.onPosterPathsAvailable();
        }
        catch (NullPointerException nEx) {
            Log.w(LOG_TAG, "No listener found!");
        }
    }

    // Data Structure used to pass movie data between back doInBackground method and
    // onPostExecute method.
    class MovieData
    {
        private String[] mPosterUrl;
        private int[] mMovieIds;

        public MovieData(String[] posterUrl, int[] movieIds) {
            mPosterUrl = posterUrl;
            mMovieIds = movieIds;
        }
    }
}
