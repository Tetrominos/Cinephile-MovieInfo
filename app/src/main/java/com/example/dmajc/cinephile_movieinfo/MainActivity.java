package com.example.dmajc.cinephile_movieinfo;

import android.app.DownloadManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dmajc.cinephile_movieinfo.adapters.PopularMovieAdapter;
import com.example.dmajc.cinephile_movieinfo.utilities.GetPosterFromUrl;
import com.example.dmajc.cinephile_movieinfo.utilities.NetworkUtils;
import com.example.dmajc.cinephile_movieinfo.utilities.QueryResult;
import com.example.dmajc.cinephile_movieinfo.utilities.TmdbPopularMoviesJsonUtils;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PopularMovieAdapter.ListItemClickListener{

    private static final String TAG = "Main Activity";
    private static final String BUNDLE_KEY = "json_query_result";
    private static final int NUM_LIST_ITEMS = 100;
    private PopularMovieAdapter mAdapter;
    private RecyclerView mPopularMoviesList;

    private TextView mQueryResultAsJsonTV;
    private ImageView mTestImageView;
    private String jsonMovieResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(null != savedInstanceState){
            /*mQueryResultAsJsonTV = (TextView) findViewById(R.id.query_result_as_json_tv);*/
            mPopularMoviesList = (RecyclerView) findViewById(R.id.popular_movies_rv);

            //different grid size according to orientation
            //http://stackoverflow.com/questions/29579811/changing-number-of-columns-with-gridlayoutmanager-and-recyclerview
            GridLayoutManager layoutManager;
            if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                layoutManager = new GridLayoutManager(this, 2);
            }
            else{
                layoutManager = new GridLayoutManager(this, 3);
            }
            mPopularMoviesList.setLayoutManager(layoutManager);
            mPopularMoviesList.setHasFixedSize(true);
            mAdapter = new PopularMovieAdapter(this, this);
            mPopularMoviesList.setAdapter(mAdapter);
            try {
                jsonMovieResponse = savedInstanceState.getString(BUNDLE_KEY);
                ArrayList<QueryResult> jsonMovieResponseArray = TmdbPopularMoviesJsonUtils.getMovieInfoFromJson(getApplicationContext(), jsonMovieResponse);
                mAdapter.setMovieData(jsonMovieResponseArray);
                Log.v(TAG, "movie data was set after recreating view");
            } catch (JSONException e) {
                e.printStackTrace();
                queryDatabase();
            }
        } else {
            /*mQueryResultAsJsonTV = (TextView) findViewById(R.id.query_result_as_json_tv);*/
            mPopularMoviesList = (RecyclerView) findViewById(R.id.popular_movies_rv);

            GridLayoutManager layoutManager;
            if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                layoutManager = new GridLayoutManager(this, 2);
            }
            else{
                layoutManager = new GridLayoutManager(this, 3);
            }
            mPopularMoviesList.setLayoutManager(layoutManager);

            mPopularMoviesList.setHasFixedSize(true);

            mAdapter = new PopularMovieAdapter(this, this);

            mPopularMoviesList.setAdapter(mAdapter);
            queryDatabase();


        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();

        //fix for query hint not showing up
        //http://stackoverflow.com/questions/20082535/hint-in-search-widget-within-action-bar-is-not-showing-up
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.search_hint));


        return true;
    }

    private void queryDatabase() {
        new FetchMovieInfo().execute();
    }

    @Override
    public void onListItemClick(int clickedItemIndex, String titleText, String posterPath, String year) {
        Intent startDetailActivity = new Intent(MainActivity.this, MovieDetailActivity.class);
        startDetailActivity.putExtra("MOVIE_TITLE", titleText);
        startDetailActivity.putExtra("MOVIE_YEAR", year);
        startDetailActivity.putExtra("POSTER_PATH", posterPath);
        startActivity(startDetailActivity);
    }

    public class FetchMovieInfo extends AsyncTask<Void, Void, ArrayList<QueryResult>> {

        // COMPLETED (6) Override the doInBackground method to perform your network requests
        @Override
        protected ArrayList<QueryResult> doInBackground(Void... Params) {

            URL queryUrl = NetworkUtils.buildUrl();

            try {
                jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(queryUrl);
                Log.v(TAG, jsonMovieResponse);

                ArrayList<QueryResult> jsonMovieResponseArray = TmdbPopularMoviesJsonUtils.getMovieInfoFromJson(getApplicationContext(), jsonMovieResponse);

                return jsonMovieResponseArray;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        // COMPLETED (7) Override the onPostExecute method to display the results of the network request
        @Override
        protected void onPostExecute(ArrayList<QueryResult> movieSearchData) {
            if (movieSearchData != null) {
                /*
                 * Iterate through the array and append the Strings to the TextView. The reason why we add
                 * the "\n\n\n" after the String is to give visual separation between each String in the
                 * TextView. Later, we'll learn about a better way to display lists of data.
                 */
                mAdapter.setMovieData(movieSearchData);
                Log.v(TAG, "movie data was set");
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_KEY, jsonMovieResponse);
    }
}

