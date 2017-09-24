package com.example.dmajc.cinephile_movieinfo;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dmajc.cinephile_movieinfo.adapters.DrawerItemCustomAdapter;
import com.example.dmajc.cinephile_movieinfo.adapters.MovieGridAdapter;
import com.example.dmajc.cinephile_movieinfo.models.DataModel;
import com.google.firebase.auth.FirebaseAuth;
import com.uwetrottmann.tmdb2.Tmdb;
import com.uwetrottmann.tmdb2.entities.MovieResultsPage;
import com.uwetrottmann.tmdb2.services.MoviesService;

import java.io.IOException;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity implements MovieGridAdapter.ListItemClickListener {

    private static final String TAG = "Main Activity";
    private static final String BUNDLE_KEY = "json_query_result";
    private static final int NUM_LIST_ITEMS = 100;
    private MovieGridAdapter mAdapter;
    private RecyclerView mPopularMoviesList;

    private TextView mQueryResultAsJsonTV;
    private ImageView mTestImageView;
    private String jsonMovieResponse;

    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    Toolbar toolbar;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;

    private FirebaseAuth mAuth;


    private Tmdb tmdb = new Tmdb("22fae8008755665b5b342cdb43e177af");
    MoviesService moviesService = tmdb.moviesService();
    MovieResultsPage mMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mTitle = mDrawerTitle = getTitle();
        mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        DataModel[] drawerItem = new DataModel[4];

        drawerItem[0] = new DataModel(R.drawable.movie_icon, mNavigationDrawerItemTitles[0]);
        drawerItem[1] = new DataModel(R.drawable.favorites, mNavigationDrawerItemTitles[1]);
        drawerItem[2] = new DataModel(R.drawable.about, mNavigationDrawerItemTitles[2]);
        drawerItem[3] = new DataModel(R.drawable.sign_out, mNavigationDrawerItemTitles[3]);

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.list_view_item_row, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        setupDrawerToggle();
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if(null != savedInstanceState){
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
            mAdapter = new MovieGridAdapter(this, this);
            mPopularMoviesList.setAdapter(mAdapter);

            try {
                mMovies = (MovieResultsPage) getLastCustomNonConfigurationInstance();
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }

            if (mMovies == null) {
                queryDatabase();
            }

            mAdapter.setMovieData(mMovies);
        } else {
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

            mAdapter = new MovieGridAdapter(this, this);

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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String query) {
                return false;
            }

            public boolean onQueryTextSubmit(String query) {
                Intent startSearchResultActivity = new Intent(MainActivity.this, SearchResultActivity.class);
                startSearchResultActivity.putExtra("QUERY", query);
                startActivity(startSearchResultActivity);
                return true;
            }
        });


        return true;
    }

    private void queryDatabase() {
        new FetchMovieInfo().execute();
    }

    @Override
    public void onListItemClick(int clickedItemIndex, String titleText, String posterPath, String year, int id) {
        Intent startDetailActivity = new Intent(MainActivity.this, MovieDetailActivity.class);
        startDetailActivity.putExtra("MOVIE_TITLE", titleText);
        startDetailActivity.putExtra("MOVIE_YEAR", year);
        startDetailActivity.putExtra("POSTER_PATH", posterPath);
        startDetailActivity.putExtra("MOVIE_ID", id);
        startActivity(startDetailActivity);
    }

    public class FetchMovieInfo extends AsyncTask<Void, Void, MovieResultsPage> {

        @Override
        protected MovieResultsPage doInBackground(Void... Params) {
            Call<com.uwetrottmann.tmdb2.entities.MovieResultsPage> call = moviesService.popular(null, null);
            com.uwetrottmann.tmdb2.entities.MovieResultsPage movies = null;
            try {
                movies = call.execute().body();
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
                break;
            case 1:
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(MainActivity.this, FavoriteMoviesActivity.class));
                break;
            case 2:
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            case 3:
                mAuth.signOut();
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
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
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();
    }
}

