package com.example.dmajc.cinephile_movieinfo;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dmajc.cinephile_movieinfo.adapters.CreditsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uwetrottmann.tmdb2.Tmdb;
import com.uwetrottmann.tmdb2.entities.Credits;
import com.uwetrottmann.tmdb2.entities.Genre;
import com.uwetrottmann.tmdb2.entities.Movie;
import com.uwetrottmann.tmdb2.services.MoviesService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class MovieDetailActivity extends AppCompatActivity implements CreditsAdapter.ListItemClickListener {
    private TextView mTitleTV;
    private TextView mYearTV;
    private TextView mDescriptionTV;
    private TextView mRatingTV;
    private TextView mGenresTV;
    private ImageView mPosterIV;
    private ImageView mImdbIV;
    private ImageView mFavoriteIV;
    private Movie mMovie;
    private Credits mCreditsList;
    private RecyclerView mCredits;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private HashMap<String, Boolean> favouritedMovies;

    private CreditsAdapter ca;

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

        // firebase init
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        mUser = mAuth.getCurrentUser();

        new FetchMovieInfo().execute();
        new FetchMovieCredits().execute();

        mTitleTV = (TextView) findViewById(R.id.tv_movie_detail_title);
        mYearTV = (TextView) findViewById(R.id.tv_movie_detail_year);
        mPosterIV = (ImageView) findViewById(R.id.iv_movie_detail_poster);
        mDescriptionTV = (TextView) findViewById(R.id.tv_movie_detail_description);
        mRatingTV = (TextView) findViewById(R.id.tv_movie_detail_rating_number);
        mImdbIV = (ImageView) findViewById(R.id.iv_movie_detail_imdb_uri);
        mGenresTV = (TextView) findViewById(R.id.tv_movie_detail_genres);
        mCredits = (RecyclerView) findViewById(R.id.credits);
        mFavoriteIV = (ImageView) findViewById(R.id.iv_movie_detail_favorite);

        if(mUser != null) {
            mDatabaseReference = mDatabase.getReference("users/" + mUser.getUid() + "/fav_movies");
            mFavoriteIV.setVisibility(View.VISIBLE);
            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    favouritedMovies = (HashMap<String, Boolean>) dataSnapshot.getValue();

                    if (favouritedMovies != null && !favouritedMovies.isEmpty()) {
                        if (favouritedMovies.containsKey(Integer.toString(movieID)) && favouritedMovies.get(Integer.toString(movieID))) {
                            mFavoriteIV.setImageResource(R.mipmap.favorite_fill);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(MovieDetailActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                }
            });
        }

        mRatingTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(MovieDetailActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
            }
        });

        mFavoriteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favouritedMovies == null || favouritedMovies.isEmpty()) {
                    favouritedMovies = new HashMap<String, Boolean>();
                }

                if (favouritedMovies.containsKey(Integer.toString(movieID))) {
                    if (favouritedMovies.get(Integer.toString(movieID))) {
                        Log.d("AAA", Integer.toString(movieID) + ": " + favouritedMovies.get(Integer.toString(movieID)) + "a");
                        favouritedMovies.put(Integer.toString(movieID), false);
                        mFavoriteIV.setImageResource(R.mipmap.favorite_empty);
                    } else {
                        Log.d("AAA", Integer.toString(movieID) + ": " + favouritedMovies.get(Integer.toString(movieID)) + "b");
                        favouritedMovies.put(Integer.toString(movieID), true);
                        mFavoriteIV.setImageResource(R.mipmap.favorite_fill);
                    }
                } else {
                    Log.d("AAA", Integer.toString(movieID) + ": " + favouritedMovies.get(Integer.toString(movieID)) + "c");
                    favouritedMovies.put(Integer.toString(movieID), true);
                    mFavoriteIV.setImageResource(R.mipmap.favorite_fill);
                }

                Toast.makeText(MovieDetailActivity.this, "YAY YOU " + movieID + ": " + favouritedMovies.get(Integer.toString(movieID)) + " HAVE FAVORITED THIS", Toast.LENGTH_SHORT).show();

                mDatabaseReference.setValue(favouritedMovies);
            }
        });

        mTitleTV.setText(movieTitle);
        mYearTV.setText(movieYear);
        Glide.with(this).load(moviePosterPath).into(mPosterIV);
        ca = new CreditsAdapter(this, this);
        mCredits.setAdapter(ca);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        mCredits.setLayoutManager(llm);
    }

    @Override
    public void onListItemClick(int clickedItemIndex, String personName, String posterPath, int personID) {
        Intent startDetailActivity = new Intent(MovieDetailActivity.this, PersonActivity.class);
        startDetailActivity.putExtra("PERSON_NAME", personName);
        startDetailActivity.putExtra("POSTER_PATH", posterPath);
        startDetailActivity.putExtra("PERSON_ID", personID);
        startActivity(startDetailActivity);
    }

    public class FetchMovieInfo extends AsyncTask<Void, Void, Movie> {

        // COMPLETED (6) Override the doInBackground method to perform your network requests
        @Override
        protected Movie doInBackground(Void... params) {
            Call<Movie> call = moviesService.summary(movieID, null, null);
            Call<Credits> creditsCall = moviesService.credits(movieID);
            Movie movie = null;
            Credits credits = null;
            try {
                movie = call.execute().body();
                credits = creditsCall.execute().body();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return movie;
        }

        // COMPLETED (7) Override the onPostExecute method to display the results of the network request
        @Override
        protected void onPostExecute(Movie movie) {
            if (movie != null) {
                mMovie = movie;
                List<Genre> genres = mMovie.genres;
                for (Genre genre : genres) {
                    mGenresTV.append(genre.name + "\n");
                }
                mDescriptionTV.setText(mMovie.overview);
                mRatingTV.setText(Double.toString(mMovie.vote_average) + "/10");
                mImdbIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse("http://www.imdb.com/title/" + mMovie.imdb_id));
                        startActivity(intent);
                    }
                });

            }
        }
    }

    public class FetchMovieCredits extends AsyncTask<Void, Void, Credits> {

        // COMPLETED (6) Override the doInBackground method to perform your network requests
        @Override
        protected Credits doInBackground(Void... params) {
            Call<Credits> call = moviesService.credits(movieID);
            Credits credits = null;
            try {
                credits = call.execute().body();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return credits;
        }

        // COMPLETED (7) Override the onPostExecute method to display the results of the network request
        @Override
        protected void onPostExecute(Credits credits) {
            if (credits != null) {
                mCreditsList = credits;
                ca.setCredits(mCreditsList.cast);
            }
        }
    }


}
