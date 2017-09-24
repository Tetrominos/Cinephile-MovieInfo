package com.example.dmajc.cinephile_movieinfo;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dmajc.cinephile_movieinfo.adapters.CreditsAdapter;
import com.example.dmajc.cinephile_movieinfo.adapters.DrawerItemCustomAdapter;
import com.example.dmajc.cinephile_movieinfo.models.DataModel;
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

    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;

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

        mTitle = mDrawerTitle = getTitle();
        mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.movie_detail_drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.movie_detail_left_drawer);

        DataModel[] drawerItem = new DataModel[4];

        drawerItem[0] = new DataModel(R.drawable.movie_icon, mNavigationDrawerItemTitles[0]);
        drawerItem[1] = new DataModel(R.drawable.favorites, mNavigationDrawerItemTitles[1]);
        drawerItem[2] = new DataModel(R.drawable.about, mNavigationDrawerItemTitles[2]);
        drawerItem[3] = new DataModel(R.drawable.sign_out, mNavigationDrawerItemTitles[3]);

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.list_view_item_row, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new MovieDetailActivity.DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.movie_detail_drawer_layout);
        setupDrawerToggle();
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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

                mDatabaseReference.setValue(favouritedMovies);
            }
        });

        mTitleTV.setText(movieTitle);
        mYearTV.setText(movieYear);
        Glide.with(this).load(moviePosterPath).placeholder(R.drawable.movie_night).into(mPosterIV);
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

        @Override
        protected void onPostExecute(Movie movie) {
            if (movie != null) {
                mMovie = movie;
                List<Genre> genres = mMovie.genres;
                for (Genre genre : genres) {
                    mGenresTV.append(genre.name + "\n");
                }
                mDescriptionTV.setText(mMovie.overview);
                mDescriptionTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showFullDescription(mMovie.overview);
                    }
                });
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

        @Override
        protected void onPostExecute(Credits credits) {
            if (credits != null) {
                mCreditsList = credits;
                ca.setCredits(mCreditsList.cast);
            }
        }
    }

    private void showFullDescription(String fullDescription) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailActivity.this);
        builder.setTitle(getString(R.string.description));
        builder.setMessage(fullDescription);

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

    }

    private void selectItem(int position) {
        switch (position) {
            case 0:
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(MovieDetailActivity.this, MainActivity.class));
                break;
            case 1:
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(MovieDetailActivity.this, FavoriteMoviesActivity.class));
                break;
            case 2:
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(MovieDetailActivity.this, AboutActivity.class));
                break;
            case 3:
                mAuth.signOut();
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(MovieDetailActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    void setupDrawerToggle(){
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();
    }
}
