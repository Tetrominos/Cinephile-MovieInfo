package com.example.dmajc.cinephile_movieinfo.utilities;

/**
 * Created by dmajc on 20.3.2017..
 */

import android.content.ContentValues;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Utility functions to handle OpenWeatherMap JSON data.
 */
public final class TmdbPopularMoviesJsonUtils {

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the weather over various days from the forecast.
     * <p/>
     * Later on, we'll be parsing the JSON into structured data within the
     * getFullWeatherDataFromJson function, leveraging the data we have stored in the JSON. For
     * now, we just convert the JSON into human-readable strings.
     *
     * @param popularMoviesJsonStr JSON response from server
     *
     * @return Array of Strings describing weather data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static String[] getMovieInfoFromJson(Context context, String popularMoviesJsonStr)
            throws JSONException {

        /* Movie information. Each day's movie info is an element of the "results" array */
        final String PM_RESULTS = "results";

        final String PM_POSTER_PATH = "poster_path";

        final String PM_GENRE_IDS = "genre_ids";

        final String PM_ID = "id";

        final String PM_TITLE = "title";
        final String PM_VOTE_AVERAGE = "vote_average";

        final String PM_RELEASE_DATE = "release_date";

        final String PM_STATUS_CODE = "status_code";

        /* String array to hold each movies's info String */
        String[] parsedMovieData = null;

        JSONObject movieJson = new JSONObject(popularMoviesJsonStr);

        /* Is there an error? */
        if (movieJson.has(PM_STATUS_CODE)) {
            int errorCode = movieJson.getInt(PM_STATUS_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray movieArray = movieJson.getJSONArray(PM_RESULTS);

        parsedMovieData = new String[movieArray.length()];

        /*long localDate = System.currentTimeMillis();
        long utcDate = SunshineDateUtils.getUTCDateFromLocal(localDate);
        long startDay = SunshineDateUtils.normalizeDate(utcDate);*/

        for (int i = 0; i < movieArray.length(); i++) {
            String posterPath;
            String title;
            String releaseDate;
            int id;
            double voteAverage;

            /* TODO: add genre id extraction funcionality */
            //int[] genreIds;

            JSONObject movie = movieArray.getJSONObject(i);

            posterPath = movie.getString(PM_POSTER_PATH);
            title = movie.getString(PM_TITLE);
            releaseDate = movie.getString(PM_RELEASE_DATE);
            id = movie.getInt(PM_ID);
            voteAverage = movie.getDouble(PM_VOTE_AVERAGE);

            parsedMovieData[i] = posterPath + " - "
                    + title + " - "
                    + releaseDate + " - "
                    + id + " - "
                    + voteAverage + "\n";
        }

        return parsedMovieData;
    }

    /**
     * Parse the JSON and convert it into ContentValues that can be inserted into our database.
     *
     * @param context         An application context, such as a service or activity context.
     * @param forecastJsonStr The JSON to parse into ContentValues.
     *
     * @return An array of ContentValues parsed from the JSON.
     */
    public static ContentValues[] getFullWeatherDataFromJson(Context context, String forecastJsonStr) {
        /** This will be implemented in a future lesson **/
        return null;
    }
}