package com.example.dmajc.cinephile_movieinfo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class PersonActivity extends AppCompatActivity {

    private TextView mPersonName, mPersonID;
    private ImageView mPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        Intent intentThatStartedThisActivity = getIntent();
        String personName = intentThatStartedThisActivity.getStringExtra("PERSON_NAME");
        String personPosterPath = intentThatStartedThisActivity.getStringExtra("POSTER_PATH");
        int personID = intentThatStartedThisActivity.getIntExtra("PERSON_ID", 0);

        mPersonName = (TextView) findViewById(R.id.person_name);
        mPoster = (ImageView) findViewById(R.id.poster_path);

        mPersonName.setText(personName);
        Glide.with(this).load("https://image.tmdb.org/t/p/w500" + personPosterPath).centerCrop().placeholder(R.drawable.boss).into(mPoster);
    }

}
