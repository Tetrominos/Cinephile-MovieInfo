package com.example.dmajc.cinephile_movieinfo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dmajc.cinephile_movieinfo.adapters.PopularMovieAdapter;
import com.example.dmajc.cinephile_movieinfo.utilities.GetPosterFromUrl;
import com.example.dmajc.cinephile_movieinfo.utilities.NetworkUtils;
import com.example.dmajc.cinephile_movieinfo.utilities.TmdbPopularMoviesJsonUtils;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Activity";
    private static final int NUM_LIST_ITEMS = 100;
    private PopularMovieAdapter mAdapter;
    private RecyclerView mPopularMoviesList;

    private TextView mQueryResultAsJsonTV;
    private ImageView mTestImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*mQueryResultAsJsonTV = (TextView) findViewById(R.id.query_result_as_json_tv);*/
        mPopularMoviesList = (RecyclerView) findViewById(R.id.popular_movies_rv);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mPopularMoviesList.setLayoutManager(layoutManager);

        mPopularMoviesList.setHasFixedSize(true);

        mAdapter = new PopularMovieAdapter(this);

        mPopularMoviesList.setAdapter(mAdapter);
        queryDatabase();

    }

    private void queryDatabase() {
        new FetchMovieInfo().execute();
    }

    public class FetchMovieInfo extends AsyncTask<Void, Void, String[]> {

        // COMPLETED (6) Override the doInBackground method to perform your network requests
        @Override
        protected String[] doInBackground(Void... Params) {

            URL queryUrl = NetworkUtils.buildUrl();

            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(queryUrl);
                Log.v(TAG, jsonMovieResponse);

                String[] jsonMovieResponseArray = TmdbPopularMoviesJsonUtils.getMovieInfoFromJson(getApplicationContext(), jsonMovieResponse);

                return jsonMovieResponseArray;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        // COMPLETED (7) Override the onPostExecute method to display the results of the network request
        @Override
        protected void onPostExecute(String[] movieSearchData) {
            if (movieSearchData != null) {
                /*
                 * Iterate through the array and append the Strings to the TextView. The reason why we add
                 * the "\n\n\n" after the String is to give visual separation between each String in the
                 * TextView. Later, we'll learn about a better way to display lists of data.
                 */
                mAdapter.setMovieData(movieSearchData);
                Log.v(TAG, "movie data was set");
                /*for (String movie : movieSearchData) {
                    mQueryResultAsJsonTV.append(movie);
                }*/
                /*try {
                    Drawable poster = new GetPosterFromUrl(movieSearchData[0]).getPosterAsDrawable();
                    mTestImageView.setImageDrawable(poster);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }*/
            }
        }
    }
}

