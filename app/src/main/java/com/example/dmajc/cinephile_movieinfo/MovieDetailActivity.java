package com.example.dmajc.cinephile_movieinfo;

import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.uwetrottmann.tmdb2.Tmdb;
import com.uwetrottmann.tmdb2.entities.Movie;
import com.uwetrottmann.tmdb2.entities.MovieResultsPage;
import com.uwetrottmann.tmdb2.services.MoviesService;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Call;

public class MovieDetailActivity extends AppCompatActivity {
    private TextView mTitleTV;
    private TextView mYearTV;
    private TextView mDescriptionTV;
    private ImageView mPosterIV;

    int movieID;
    private Tmdb tmdb = new Tmdb("22fae8008755665b5b342cdb43e177af");
    MoviesService moviesService = tmdb.moviesService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Intent intentThatStartedThisActivity = getIntent();
        String movieTitle = intentThatStartedThisActivity.getStringExtra("MOVIE_TITLE");
        String moviePosterPath = intentThatStartedThisActivity.getStringExtra("POSTER_PATH");
        String movieYear = intentThatStartedThisActivity.getStringExtra("MOVIE_YEAR");
        movieID = intentThatStartedThisActivity.getIntExtra("MOVIE_ID", 0);

        new FetchMovieInfo().execute();

        mTitleTV = (TextView) findViewById(R.id.tv_movie_detail_title);
        mYearTV = (TextView) findViewById(R.id.tv_movie_detail_year);
        mPosterIV = (ImageView) findViewById(R.id.iv_movie_detail_poster);
        mDescriptionTV = (TextView) findViewById(R.id.tv_movie_detail_description);

        mTitleTV.setText(movieTitle);
        mYearTV.setText(movieYear);
        Glide.with(this).load(moviePosterPath).into(mPosterIV);
    }

    public class FetchMovieInfo extends AsyncTask<Void, Void, Movie> {

        // COMPLETED (6) Override the doInBackground method to perform your network requests
        @Override
        protected Movie doInBackground(Void... params) {
            Call<Movie> call = moviesService.summary(movieID, null, null);
            Movie movie = null;
            try {
                movie = call.execute().body();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return movie;
        }

        // COMPLETED (7) Override the onPostExecute method to display the results of the network request
        @Override
        protected void onPostExecute(Movie movie) {
            if (movie != null) {
                mDescriptionTV.setText(movie.overview);
            }
        }
    }
}
