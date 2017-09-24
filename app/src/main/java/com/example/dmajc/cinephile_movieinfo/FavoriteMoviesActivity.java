package com.example.dmajc.cinephile_movieinfo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dmajc.cinephile_movieinfo.adapters.DrawerItemCustomAdapter;
import com.example.dmajc.cinephile_movieinfo.adapters.MovieGridAdapter;
import com.example.dmajc.cinephile_movieinfo.models.DataModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uwetrottmann.tmdb2.Tmdb;
import com.uwetrottmann.tmdb2.entities.Movie;
import com.uwetrottmann.tmdb2.entities.MovieResultsPage;
import com.uwetrottmann.tmdb2.services.MoviesService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;

public class FavoriteMoviesActivity extends AppCompatActivity implements MovieGridAdapter.ListItemClickListener{

    private static final String TAG = "FavoriteMoviesActivity";
    private MovieGridAdapter mAdapter;
    private RecyclerView mFavoriteMoviesList;

    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    private View mProgressView;

    private TextView mNotLoggedIn;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;

    private HashMap<String, Boolean> favouritedMovies;


    private Tmdb tmdb = new Tmdb("22fae8008755665b5b342cdb43e177af");
    MoviesService moviesService = tmdb.moviesService();
    MovieResultsPage mMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_movies);

        mAuth = FirebaseAuth.getInstance();

        mTitle = mDrawerTitle = getTitle();
        mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.fav_movie_drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.fav_movie_left_drawer);
        mProgressView = findViewById(R.id.login_progress_fav_movies);

        DataModel[] drawerItem = new DataModel[4];

        drawerItem[0] = new DataModel(R.drawable.movie_icon, mNavigationDrawerItemTitles[0]);
        drawerItem[1] = new DataModel(R.drawable.favorites, mNavigationDrawerItemTitles[1]);
        drawerItem[2] = new DataModel(R.drawable.about, mNavigationDrawerItemTitles[2]);
        drawerItem[3] = new DataModel(R.drawable.sign_out, mNavigationDrawerItemTitles[3]);

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.list_view_item_row, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new FavoriteMoviesActivity.DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.fav_movie_drawer_layout);
        setupDrawerToggle();
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDatabase = FirebaseDatabase.getInstance();

        mUser = mAuth.getCurrentUser();

        showProgress(true);

        if(mUser != null) {
            mDatabaseReference = mDatabase.getReference("users/" + mUser.getUid() + "/fav_movies");
            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    favouritedMovies = (HashMap<String, Boolean>) dataSnapshot.getValue();
                    queryDatabase();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(FavoriteMoviesActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                }
            });

            if(null != savedInstanceState){
                mFavoriteMoviesList = (RecyclerView) findViewById(R.id.fav_movies_rv);

                //different grid size according to orientation
                //http://stackoverflow.com/questions/29579811/changing-number-of-columns-with-gridlayoutmanager-and-recyclerview
                GridLayoutManager layoutManager;
                if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                    layoutManager = new GridLayoutManager(this, 2);
                }
                else{
                    layoutManager = new GridLayoutManager(this, 3);
                }
                mFavoriteMoviesList.setLayoutManager(layoutManager);
                mFavoriteMoviesList.setHasFixedSize(true);
                mAdapter = new MovieGridAdapter(this, this);
                mFavoriteMoviesList.setAdapter(mAdapter);

                try {
                    mMovies = (MovieResultsPage) getLastCustomNonConfigurationInstance();
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }

                mAdapter.setMovieData(mMovies);
            } else {
                mFavoriteMoviesList = (RecyclerView) findViewById(R.id.fav_movies_rv);

                GridLayoutManager layoutManager;
                if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                    layoutManager = new GridLayoutManager(this, 2);
                }
                else{
                    layoutManager = new GridLayoutManager(this, 3);
                }
                mFavoriteMoviesList.setLayoutManager(layoutManager);

                mFavoriteMoviesList.setHasFixedSize(true);

                mAdapter = new MovieGridAdapter(this, this);

                mFavoriteMoviesList.setAdapter(mAdapter);
            }
        } else {
            mNotLoggedIn = (TextView) findViewById(R.id.tv_not_logged_in);
            mNotLoggedIn.setText(R.string.not_logged_in);
            showProgress(false);
        }

    }

    private void queryDatabase() {
        new FavoriteMoviesActivity.FetchMovieInfo().execute();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }


    @Override
    public void onListItemClick(int clickedItemIndex, String titleText, String posterPath, String year, int id) {
        Intent startDetailActivity = new Intent(FavoriteMoviesActivity.this, MovieDetailActivity.class);
        startDetailActivity.putExtra("MOVIE_TITLE", titleText);
        startDetailActivity.putExtra("MOVIE_YEAR", year);
        startDetailActivity.putExtra("POSTER_PATH", posterPath);
        startDetailActivity.putExtra("MOVIE_ID", id);
        startActivity(startDetailActivity);
    }

    public class FetchMovieInfo extends AsyncTask<Void, Void, MovieResultsPage> {

        @Override
        protected MovieResultsPage doInBackground(Void... Params) {

            Call<com.uwetrottmann.tmdb2.entities.Movie> call = null;
            com.uwetrottmann.tmdb2.entities.MovieResultsPage movies = new MovieResultsPage();
            com.uwetrottmann.tmdb2.entities.Movie currentMovie = null;
            movies.results = new ArrayList<Movie>();
            try {
                if (favouritedMovies == null)
                    return null;
                if (favouritedMovies.isEmpty())
                    return null;
                for (String idOfMovie : favouritedMovies.keySet()) {
                    if (favouritedMovies.get(idOfMovie)) {
                        call = moviesService.summary(Integer.parseInt(idOfMovie), null, null);
                        currentMovie = call.execute().body();
                        movies.results.add(currentMovie);
                        movies.page = 1;
                        movies.total_pages = 1;
                        movies.total_results = movies.results.size();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return movies;
        }

        @Override
        protected void onPostExecute(MovieResultsPage popularMovies) {
            if (popularMovies != null) {
                mMovies = popularMovies;
                for (com.uwetrottmann.tmdb2.entities.Movie movie : popularMovies.results) {
                    Log.d(TAG, movie.title + " " + movie.poster_path);
                }
                mAdapter.setMovieData(popularMovies);
                showProgress(false);
            }
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mMovies;
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
                startActivity(new Intent(FavoriteMoviesActivity.this, MainActivity.class));
                break;
            case 1:
                mDrawerLayout.closeDrawers();
                break;
            case 2:
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(FavoriteMoviesActivity.this, AboutActivity.class));
                break;
            case 3:
                mAuth.signOut();
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(FavoriteMoviesActivity.this, LoginActivity.class));
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    void setupDrawerToggle(){
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name);
        mDrawerToggle.syncState();
    }

}
