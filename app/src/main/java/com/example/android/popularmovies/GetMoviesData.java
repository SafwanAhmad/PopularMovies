package com.example.android.popularmovies;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.android.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * In case the sorting order being popular or top rated we connect to server (The Movie Database)
 * and get the listed movies under that sorting order. First we check if there network is available
 * or not. If the network is available then this method creates an async task and connect to server.
 * Async task will write to the respective table in the database with the help of Content Resolver.
 * <p>
 * Created by safwanx on 10/11/16.
 */
public class GetMoviesData extends AsyncTask<Void, Void, Void> {

    public final String LOG_TAG = GetMoviesData.class.getSimpleName();

    //Reference to the listener for posterPathAvailable
    public Context listener = null;


    //TODO Initially we are retrieving the first page. Later we
    //TODO will add support for more pages to be retrieved.
    //TODO Next step will be to provide user the option to
    //TODO navigate back and forth.
    //TODO Finally a infinite scrolling support will be added.
    @Override
    protected Void doInBackground(Void... params) {

        //These two are defined outside the try/catch so that
        //they can be closed inside finally block

        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;

        //Raw JSON response as a string
        String moviesInfoJson;

        try {
            Uri.Builder builder = new Uri.Builder();

            final String SCHEME = "https";
            final String AUTHORITY = "api.themoviedb.org";
            final String API_VERSION = "3";
            final String CONTENT_TYPE = "movie";
            final String API_KEY_PARAM = "api_key";

            //This is set based on user preference
            Context currentContext = (Context) listener;
            SharedPreferences sharedPreferences = (SharedPreferences) PreferenceManager
                    .getDefaultSharedPreferences(currentContext);


            final String MOVIE_ORDER = sharedPreferences.getString(
                    currentContext.getString(R.string.pref_sorting_order_key),
                    currentContext.getString(R.string.pref_sorting_popular)
            );


            Uri uri = builder.scheme(SCHEME)
                    .authority(AUTHORITY)
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

            //It's time to parse the JSON and write this data to database
            getMovieDataFromJson(moviesInfoJson);

            //Close the data stream
            inputStream.close();

        } catch (IOException iExp) {
            return null;
        } catch (JSONException jEx) {
            return null;
        } finally {
            //Close the streams
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException iEx) {
                    Log.e(LOG_TAG, "Error closing stream", iEx);
                }
            }

            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }

        }
        return null;
    }


    private void getMovieDataFromJson(String moviesInfoJson)
            throws JSONException {
        //Define the JSON tags that have to be extracted from the JSON data.
        //At this stage we retrieve movie ids and poster paths only and leave
        //all other information in database to be updated at a later stage.
        final String MOVIE_ID = "id";
        final String TITLE = "title";
        final String POSTER_PATH = "poster_path";
        final String RELEASE_DATE = "release_date";
        final String USER_RATING = "vote_average";
        final String MOVIE_PLOT = "overview";

        final String ROOT_KEY = "results";

        //Create a JSON object
        JSONObject jsonObject = new JSONObject(moviesInfoJson);

        //Retrieve the JSON array of objects containing the movies information
        JSONArray moviesData = jsonObject.getJSONArray(ROOT_KEY);

        //Create a vector to hold all the content values
        Vector<ContentValues> cVVector = new Vector<>(moviesData.length());

        //This is set based on user preference
        Context currentContext = (Context) listener;
        SharedPreferences sharedPreferences = (SharedPreferences) PreferenceManager
                .getDefaultSharedPreferences(currentContext);


        final String MOVIE_ORDER = sharedPreferences.getString(
                currentContext.getString(R.string.pref_sorting_order_key),
                currentContext.getString(R.string.pref_sorting_popular)
        );

        String basePath = ((Activity) listener).getString(R.string.poster_base_path);

        //Loop over this JSON array to retrieve the each movie information
        for (int i = 0; i < moviesData.length(); i++) {

            //Retrieve the following:
            // 1) movie id
            // 2) title
            // 3) poster path
            // 4) release date
            // 5) user rating
            // 6) movie plot
            JSONObject currentMovieData = moviesData.getJSONObject(i);

            String movieId = String.valueOf(currentMovieData.getInt(MOVIE_ID));
            String title = currentMovieData.getString(TITLE);
            String posterPath = currentMovieData.getString(POSTER_PATH);
            String releaseDate = currentMovieData.getString(RELEASE_DATE);
            String userRating = String.valueOf(currentMovieData.getDouble(USER_RATING));
            String moviePlot = currentMovieData.getString(MOVIE_PLOT);


            ContentValues values = new ContentValues();

            //As api does not return running time so we set it to unknown
            final String UNKNOWN = "Unknown";

            if (MOVIE_ORDER.equals(currentContext.getString(R.string.pref_sorting_popular))) {
                //Add the data to content value
                values.put(MovieContract.Popular._ID, movieId);
                values.put(MovieContract.Popular.COLUMN_MOVIE_TITLE, title);
                values.put(MovieContract.Popular.COLUMN_MOVIE_POSTER_PATH, basePath + posterPath);
                values.put(MovieContract.Popular.COLUMN_MOVIE_RELEASE_DATE, releaseDate);
                values.put(MovieContract.Popular.COLUMN_MOVIE_USER_RATING, userRating);
                values.put(MovieContract.Popular.COLUMN_MOVIE_PLOT, moviePlot);
                //Add the unknown value
                values.put(MovieContract.Popular.COLUMN_MOVIE_RUNNING_TIME, UNKNOWN);

            } else if (MOVIE_ORDER.equals(currentContext.getString(R.string.pref_sorting_top_rated))) {
                //Add the data to content value
                values.put(MovieContract.TopRated._ID, movieId);
                values.put(MovieContract.TopRated.COLUMN_MOVIE_TITLE, title);
                values.put(MovieContract.TopRated.COLUMN_MOVIE_POSTER_PATH, basePath + posterPath);
                values.put(MovieContract.TopRated.COLUMN_MOVIE_RELEASE_DATE, releaseDate);
                values.put(MovieContract.TopRated.COLUMN_MOVIE_USER_RATING, userRating);
                values.put(MovieContract.TopRated.COLUMN_MOVIE_PLOT, moviePlot);

                //Add the unknown value
                values.put(MovieContract.TopRated.COLUMN_MOVIE_RUNNING_TIME, UNKNOWN);
            }
            cVVector.add(values);
        }

        Uri uri = null;

        if (MOVIE_ORDER.equals(currentContext.getString(R.string.pref_sorting_popular))) {
            uri = MovieContract.Popular.CONTENT_URI;
        } else if (MOVIE_ORDER.equals(currentContext.getString(R.string.pref_sorting_top_rated))) {
            uri = MovieContract.TopRated.CONTENT_URI;
        }
        //Update the database
        ContentValues[] contentValues = new ContentValues[cVVector.size()];
        cVVector.toArray(contentValues);

        //If there is old data then delete it
        currentContext.getContentResolver().delete(uri,
                null,
                null);
        currentContext.getContentResolver().bulkInsert(uri, contentValues);
    }


    @Override
    protected void onPostExecute(Void value) {
        super.onPostExecute(value);
    }
}
