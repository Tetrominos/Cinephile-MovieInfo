package com.example.dmajc.cinephile_movieinfo;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.example.dmajc.cinephile_movieinfo.adapters.MovieGridAdapter;
import com.uwetrottmann.tmdb2.Tmdb;
import com.uwetrottmann.tmdb2.entities.MovieResultsPage;
import com.uwetrottmann.tmdb2.services.SearchService;

import java.io.IOException;

import retrofit2.Call;

public class SearchResultActivity extends AppCompatActivity implements MovieGridAdapter.ListItemClickListener{

    public final String TAG = "SearchResultsActivity";
    Tmdb tmdb = new Tmdb("22fae8008755665b5b342cdb43e177af");
//    MoviesService moviesService = tmdb.moviesService();
    SearchService searchService = tmdb.searchService();
    TextView searchResultTV;
    private MovieResultsPage mMovies;

    private MovieGridAdapter mAdapter;
    private RecyclerView mPopularMoviesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Intent intentThatStartedThisActivity = getIntent();
        String query = intentThatStartedThisActivity.getStringExtra("QUERY");
        getSupportActionBar().setTitle("Search for " + query);

        if (null != savedInstanceState) {

            mPopularMoviesList = (RecyclerView) findViewById(R.id.queried_movies_rv);

            GridLayoutManager layoutManager;
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                layoutManager = new GridLayoutManager(this, 2);
            } else {
                layoutManager = new GridLayoutManager(this, 3);
            }
            mPopularMoviesList.setLayoutManager(layoutManager);

            mPopularMoviesList.setHasFixedSize(true);

            mAdapter = new MovieGridAdapter(this, this);

            mPopularMoviesList.setAdapter(mAdapter);

            try {
                mMovies = (MovieResultsPage) getLastCustomNonConfigurationInstance();
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }

            mAdapter.setMovieData(mMovies);

        } else {

            mPopularMoviesList = (RecyclerView) findViewById(R.id.queried_movies_rv);

            GridLayoutManager layoutManager;
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                layoutManager = new GridLayoutManager(this, 2);
            } else {
                layoutManager = new GridLayoutManager(this, 3);
            }
            mPopularMoviesList.setLayoutManager(layoutManager);

            mPopularMoviesList.setHasFixedSize(true);

            mAdapter = new MovieGridAdapter(this, this);

            mPopularMoviesList.setAdapter(mAdapter);

            new FetchMovieInfo().execute(query);
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex, String titleText, String posterPath, String year, int movieID) {
        Intent startDetailActivity = new Intent(SearchResultActivity.this, MovieDetailActivity.class);
        startDetailActivity.putExtra("MOVIE_TITLE", titleText);
        startDetailActivity.putExtra("MOVIE_YEAR", year);
        startDetailActivity.putExtra("POSTER_PATH", posterPath);
        startDetailActivity.putExtra("MOVIE_ID", movieID);
        startActivity(startDetailActivity);
    }

    public class FetchMovieInfo extends AsyncTask<String, Void, MovieResultsPage> {

        // COMPLETED (6) Override the doInBackground method to perform your network requests
        @Override
        protected MovieResultsPage doInBackground(String... queries) {

            String query = queries[0];
            Call<MovieResultsPage> call = searchService.movie(query, null, null, null, null, null, null);
            MovieResultsPage movieResultsPage = null;
            try {
                movieResultsPage = call.execute().body();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Log.d(TAG, movieResultsPage.toString());
            return movieResultsPage;
        }

        // COMPLETED (7) Override the onPostExecute method to display the results of the network request
        @Override
        protected void onPostExecute(MovieResultsPage movieResultsPage) {
            if (movieResultsPage != null) {
                mMovies = movieResultsPage;
                mAdapter.setMovieData(movieResultsPage);
            }
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mMovies;
    }
}
