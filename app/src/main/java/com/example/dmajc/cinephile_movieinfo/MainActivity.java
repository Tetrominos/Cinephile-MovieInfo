package com.example.dmajc.cinephile_movieinfo;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dmajc.cinephile_movieinfo.utilities.NetworkUtils;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mQueryResultAsJsonTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQueryResultAsJsonTV = (TextView) findViewById(R.id.query_result_as_json);
        mQueryResultAsJsonTV.setText("text");

        queryDatabase();
    }

    private void queryDatabase() {
        String query = "Fargo";
        new FetchMovieInfo().execute(query);
    }

    public class FetchMovieInfo extends AsyncTask<String, Void, String> {

        // COMPLETED (6) Override the doInBackground method to perform your network requests
        @Override
        protected String doInBackground(String... params) {

            /* If there's no zip code, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            String query = params[0];
            URL queryUrl = NetworkUtils.buildUrl(query);

            try {
                String jsonWeatherResponse = NetworkUtils
                        .getResponseFromHttpUrl(queryUrl);

                return jsonWeatherResponse;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        // COMPLETED (7) Override the onPostExecute method to display the results of the network request
        @Override
        protected void onPostExecute(String movieSearchData) {
            if (movieSearchData != null) {
                /*
                 * Iterate through the array and append the Strings to the TextView. The reason why we add
                 * the "\n\n\n" after the String is to give visual separation between each String in the
                 * TextView. Later, we'll learn about a better way to display lists of data.
                 */
                mQueryResultAsJsonTV.setText(movieSearchData);
            }
        }
    }
}

