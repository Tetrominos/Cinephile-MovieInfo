package com.example.dmajc.cinephile_movieinfo;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dmajc.cinephile_movieinfo.adapters.CreditsAdapter;
import com.example.dmajc.cinephile_movieinfo.adapters.PersonCreditsAdapter;
import com.uwetrottmann.tmdb2.Tmdb;
import com.uwetrottmann.tmdb2.entities.Person;
import com.uwetrottmann.tmdb2.entities.PersonCastCredit;
import com.uwetrottmann.tmdb2.entities.PersonCredits;
import com.uwetrottmann.tmdb2.services.PeopleService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Call;

public class PersonActivity extends AppCompatActivity implements PersonCreditsAdapter.ListItemClickListener {

    private TextView mPersonName, mPersonBirthValue, mPersonDeath, mPersonDeathValue, mPersonBio;
    private ImageView mPoster;
    private int personID;
    private PersonCredits mPersonCredits;
    private Person mPerson;
    private ImageView mImdbIV;
    private RecyclerView mCredits;

    private PersonCreditsAdapter ca;

    private Tmdb tmdb = new Tmdb("22fae8008755665b5b342cdb43e177af");
    PeopleService peopleService = tmdb.personService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

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

        // COMPLETED (6) Override the doInBackground method to perform your network requests
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

        // COMPLETED (7) Override the onPostExecute method to display the results of the network request
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

        // COMPLETED (6) Override the doInBackground method to perform your network requests
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

        // COMPLETED (7) Override the onPostExecute method to display the results of the network request
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
            }
        }
    }
}
