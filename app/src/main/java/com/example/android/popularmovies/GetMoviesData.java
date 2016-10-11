package com.example.android.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by safwanx on 10/11/16.
 */
public class GetMoviesData extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {

        //These two are defined outside the try/catch so that
        //they can be closed inside finally block

        HttpURLConnection httpURLConnection;
        BufferedReader bufferedReader;

        //Raw JSON response as a string
        String moviesInfoJson;

        try
        {
            final String MOVIE_BASE_URL = "https://api.themoviedb.org";
            final String API_VERSION = "3";
            final String CONTENT_TYPE = "movie";

            //This is set based on user preference
            final String MOVIE_ORDER = "popular";

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
            getMovieDataFromJson(moviesInfoJson);

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

        }
        return null;
    }


    private void getMovieDataFromJson(String moviesInfoJson)
            throws JSONException
    {

    }
}
