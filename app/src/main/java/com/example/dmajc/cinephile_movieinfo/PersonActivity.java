package com.example.dmajc.cinephile_movieinfo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dmajc.cinephile_movieinfo.adapters.DrawerItemCustomAdapter;
import com.example.dmajc.cinephile_movieinfo.adapters.PersonCreditsAdapter;
import com.example.dmajc.cinephile_movieinfo.models.DataModel;
import com.google.firebase.auth.FirebaseAuth;
import com.uwetrottmann.tmdb2.Tmdb;
import com.uwetrottmann.tmdb2.entities.Person;
import com.uwetrottmann.tmdb2.entities.PersonCastCredit;
import com.uwetrottmann.tmdb2.entities.PersonCredits;
import com.uwetrottmann.tmdb2.services.PeopleService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.Call;

public class PersonActivity extends AppCompatActivity implements PersonCreditsAdapter.ListItemClickListener {

    private TextView mPersonName, mPersonBirthValue, mPersonDeath, mPersonDeathValue, mPersonBio;
    private ImageView mPoster;
    private int personID;
    private PersonCredits mPersonCredits;
    private Person mPerson;
    private RecyclerView mCredits;

    private PersonCreditsAdapter ca;

    private Tmdb tmdb = new Tmdb("22fae8008755665b5b342cdb43e177af");
    PeopleService peopleService = tmdb.personService();

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
        setContentView(R.layout.activity_person);

        mAuth = FirebaseAuth.getInstance();

        mTitle = mDrawerTitle = getTitle();
        mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.person_detail_drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.person_detail_left_drawer);

        DataModel[] drawerItem = new DataModel[4];

        drawerItem[0] = new DataModel(R.drawable.movie_icon, mNavigationDrawerItemTitles[0]);
        drawerItem[1] = new DataModel(R.drawable.favorites, mNavigationDrawerItemTitles[1]);
        drawerItem[2] = new DataModel(R.drawable.about, mNavigationDrawerItemTitles[2]);
        drawerItem[3] = new DataModel(R.drawable.sign_out, mNavigationDrawerItemTitles[3]);

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, R.layout.list_view_item_row, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new PersonActivity.DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.person_detail_drawer_layout);
        setupDrawerToggle();
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intentThatStartedThisActivity = getIntent();
        String personName = intentThatStartedThisActivity.getStringExtra("PERSON_NAME");
        String personPosterPath = intentThatStartedThisActivity.getStringExtra("POSTER_PATH");
        personID = intentThatStartedThisActivity.getIntExtra("PERSON_ID", 0);

        mPersonName = (TextView) findViewById(R.id.person_name);
        mPoster = (ImageView) findViewById(R.id.poster_path);
        mCredits = (RecyclerView) findViewById(R.id.person_credits_rv);
        mPersonBirthValue = (TextView) findViewById(R.id.tv_person_credit_birthday_value);
        mPersonDeath = (TextView) findViewById(R.id.tv_person_credit_deathday);
        mPersonDeathValue = (TextView) findViewById(R.id.tv_person_credit_deathday_value);
        mPersonBio = (TextView) findViewById(R.id.tv_person_credit_bio_value);

        ca = new PersonCreditsAdapter(this, this);
        mCredits.setAdapter(ca);

        mPersonName.setText(personName);
        Glide.with(this).load("https://image.tmdb.org/t/p/w500" + personPosterPath).centerCrop().placeholder(R.drawable.boss).into(mPoster);

        new FetchPersonCredits().execute();
        new FetchPersonDetails().execute();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mCredits.setLayoutManager(llm);
        mCredits.setNestedScrollingEnabled(false);
    }

    @Override
    public void onListItemClick(int clickedItemIndex, String title, String posterPath, int id, int year) {
        Intent startDetailActivity = new Intent(PersonActivity.this, MovieDetailActivity.class);
        startDetailActivity.putExtra("MOVIE_TITLE", title);
        startDetailActivity.putExtra("MOVIE_YEAR", Integer.toString(year));
        startDetailActivity.putExtra("POSTER_PATH", posterPath);
        startDetailActivity.putExtra("MOVIE_ID", id);
        startActivity(startDetailActivity);
    }


    public class FetchPersonCredits extends AsyncTask<Void, Void, PersonCredits> {
        @Override
        protected PersonCredits doInBackground(Void... params) {
            Call<PersonCredits> call = peopleService.movieCredits(personID, null);
            PersonCredits personCredits = null;
            try {
                personCredits = call.execute().body();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return personCredits;
        }

        @Override
        protected void onPostExecute(PersonCredits personCredits) {
            if (personCredits != null) {
                mPersonCredits = personCredits;
                List<PersonCastCredit> castCredits = mPersonCredits.cast;
                ca.setCredits(castCredits);
            }
        }
    }

    public class FetchPersonDetails extends AsyncTask<Void, Void, Person> {
        @Override
        protected Person doInBackground(Void... params) {
            Call<Person> call = peopleService.summary(personID);
            Person person = null;
            try {
                person = call.execute().body();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return person;
        }

        @Override
        protected void onPostExecute(Person person) {
            if (person != null) {
                mPerson = person;
                SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
                if (mPerson.birthday != null) {
                    mPersonBirthValue.setText(sdf.format(mPerson.birthday));
                } else {
                    mPersonBirthValue.setText("Not available");
                }
                if (mPerson.deathday != null) {
                    mPersonDeath.setVisibility(View.VISIBLE);
                    mPersonDeathValue.setVisibility(View.VISIBLE);
                    mPersonDeathValue.setText(sdf.format(mPerson.deathday));
                }
                mPersonBio.setText(mPerson.biography);
                mPersonBio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showFullDescription(mPerson.biography);
                    }
                });
            }
        }
    }

    private void showFullDescription(String fullDescription) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PersonActivity.this);
        builder.setTitle(getString(R.string.biography));
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
                startActivity(new Intent(PersonActivity.this, MainActivity.class));
                break;
            case 1:
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(PersonActivity.this, FavoriteMoviesActivity.class));
                break;
            case 2:
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(PersonActivity.this, AboutActivity.class));
                break;
            case 3:
                mAuth.signOut();
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(PersonActivity.this, LoginActivity.class));
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
