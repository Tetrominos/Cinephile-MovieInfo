package com.example.dmajc.cinephile_movieinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dmajc.cinephile_movieinfo.adapters.DrawerItemCustomAdapter;
import com.example.dmajc.cinephile_movieinfo.models.DataModel;
import com.google.firebase.auth.FirebaseAuth;

public class AboutActivity extends AppCompatActivity  {

    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mAuth = FirebaseAuth.getInstance();

        mTitle = mDrawerTitle = getTitle();
        mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.about_drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.about_left_drawer);

        DataModel[] drawerItem = new DataModel[4];

        drawerItem[0] = new DataModel(R.drawable.movie_icon, mNavigationDrawerItemTitles[0]);
        drawerItem[1] = new DataModel(R.drawable.favorites, mNavigationDrawerItemTitles[1]);
        drawerItem[2] = new DataModel(R.drawable.about, mNavigationDrawerItemTitles[2]);
        drawerItem[3] = new DataModel(R.drawable.sign_out, mNavigationDrawerItemTitles[3]);

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.list_view_item_row, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AboutActivity.DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.about_drawer_layout);
        setupDrawerToggle();
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

    }

    private void selectItem(int position) {
        Toast.makeText(this, mNavigationDrawerItemTitles[position], Toast.LENGTH_LONG).show();
        switch (position) {
            case 0:
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(AboutActivity.this, MainActivity.class));
                break;
            case 1:
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(AboutActivity.this, FavoriteMoviesActivity.class));
                break;
            case 2:
                mDrawerLayout.closeDrawers();
                break;
            case 3:
                mAuth.signOut();
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(AboutActivity.this, LoginActivity.class));
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
