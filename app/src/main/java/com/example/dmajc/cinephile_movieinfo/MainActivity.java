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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dmajc.cinephile_movieinfo.adapters.DrawerItemCustomAdapter;
import com.example.dmajc.cinephile_movieinfo.adapters.PopularMovieAdapter;
import com.example.dmajc.cinephile_movieinfo.models.DataModel;
import com.uwetrottmann.tmdb2.Tmdb;
import com.uwetrottmann.tmdb2.entities.MovieResultsPage;
import com.uwetrottmann.tmdb2.services.MoviesService;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity implements PopularMovieAdapter.ListItemClickListener {

    private static final String TAG = "Main Activity";
    private static final String BUNDLE_KEY = "json_query_result";
    private static final int NUM_LIST_ITEMS = 100;
    private PopularMovieAdapter mAdapter;
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


    private Tmdb tmdb = new Tmdb("22fae8008755665b5b342cdb43e177af");
    MoviesService moviesService = tmdb.moviesService();
    MovieResultsPage mMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();
        mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        DataModel[] drawerItem = new DataModel[3];

        drawerItem[0] = new DataModel(R.drawable.common_google_signin_btn_icon_light, mNavigationDrawerItemTitles[0]);
        drawerItem[1] = new DataModel(R.drawable.common_google_signin_btn_icon_light, mNavigationDrawerItemTitles[1]);
        drawerItem[2] = new DataModel(R.drawable.common_google_signin_btn_icon_dark, mNavigationDrawerItemTitles[2]);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.list_view_item_row, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        setupDrawerToggle();

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
//            try {
//                jsonMovieResponse = savedInstanceState.getString(BUNDLE_KEY);
//                ArrayList<QueryResult> jsonMovieResponseArray = TmdbPopularMoviesJsonUtils.getMovieInfoFromJson(getApplicationContext(), jsonMovieResponse);
//                mAdapter.setMovieData(jsonMovieResponseArray);
//                Log.v(TAG, "movie data was set after recreating view");
//            } catch (JSONException e) {
//                e.printStackTrace();
//                queryDatabase();
//            }

            try {
                mMovies = (MovieResultsPage) getLastCustomNonConfigurationInstance();
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }

            mAdapter.setMovieData(mMovies);
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

        // COMPLETED (6) Override the doInBackground method to perform your network requests
        @Override
        protected MovieResultsPage doInBackground(Void... Params) {

//            URL queryUrl = NetworkUtils.buildUrl();
//
//            try {
//                jsonMovieResponse = NetworkUtils
//                        .getResponseFromHttpUrl(queryUrl);
//                Log.v(TAG, jsonMovieResponse);
//
//                ArrayList<QueryResult> jsonMovieResponseArray = TmdbPopularMoviesJsonUtils.getMovieInfoFromJson(getApplicationContext(), jsonMovieResponse);
//
//                return jsonMovieResponseArray;
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
            Call<com.uwetrottmann.tmdb2.entities.MovieResultsPage> call = moviesService.popular(null, null);
            com.uwetrottmann.tmdb2.entities.MovieResultsPage movies = null;
            try {
                movies = call.execute().body();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return movies;
        }

        // COMPLETED (7) Override the onPostExecute method to display the results of the network request
        @Override
        protected void onPostExecute(MovieResultsPage popularMovies) {
            if (popularMovies != null) {
//                /*
//                 * Iterate through the array and append the Strings to the TextView. The reason why we add
//                 * the "\n\n\n" after the String is to give visual separation between each String in the
//                 * TextView. Later, we'll learn about a better way to display lists of data.
//                 */
//                mAdapter.setMovieData(movieSearchData);
//                Log.v(TAG, "movie data was set");
                mMovies = popularMovies;
                for (com.uwetrottmann.tmdb2.entities.Movie movie : popularMovies.results) {
                    Log.d(TAG, movie.title + " " + movie.poster_path);
                }
                mAdapter.setMovieData(popularMovies);
            }
        }
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString(BUNDLE_KEY, jsonMovieResponse);
//        outState.putExtra
//    }


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
        Toast.makeText(this, mNavigationDrawerItemTitles[position], Toast.LENGTH_LONG).show();
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


    void setupDrawerToggle(){
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this,mDrawerLayout,R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();
    }
}

