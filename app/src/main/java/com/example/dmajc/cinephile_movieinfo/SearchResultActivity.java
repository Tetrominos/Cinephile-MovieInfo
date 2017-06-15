package com.example.dmajc.cinephile_movieinfo;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dmajc.cinephile_movieinfo.utilities.NetworkUtils;
import com.example.dmajc.cinephile_movieinfo.utilities.QueryResult;
import com.example.dmajc.cinephile_movieinfo.utilities.TmdbPopularMoviesJsonUtils;
import com.uwetrottmann.tmdb2.Tmdb;
import com.uwetrottmann.tmdb2.entities.Movie;
import com.uwetrottmann.tmdb2.entities.MovieResultsPage;
import com.uwetrottmann.tmdb2.services.MoviesService;
import com.uwetrottmann.tmdb2.services.SearchService;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Call;

public class SearchResultActivity extends AppCompatActivity {

    public final String TAG = "SearchResultsActivity";
    Tmdb tmdb = new Tmdb("22fae8008755665b5b342cdb43e177af");
//    MoviesService moviesService = tmdb.moviesService();
    SearchService searchService = tmdb.searchService();
    TextView searchResultTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        Intent intentThatStartedThisActivity = getIntent();
        searchResultTV = (TextView) findViewById(R.id.searchResultTV);
        String query = intentThatStartedThisActivity.getStringExtra("QUERY");
        getSupportActionBar().setTitle("Search for " + query);
        new FetchMovieInfo().execute(query);
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
            return movieResultsPage;
        }

        // COMPLETED (7) Override the onPostExecute method to display the results of the network request
        @Override
        protected void onPostExecute(MovieResultsPage movieResultsPage) {
            if (movieResultsPage != null) {
                List<Movie> movies = movieResultsPage.results;
                Calendar calendar = new GregorianCalendar();
                for(Movie movie : movies) {
                    calendar.setTime(movie.release_date);
                    searchResultTV.append(movie.title + " " + calendar.get(Calendar.YEAR) + "\n");
                }
            }
        }
    }
}
