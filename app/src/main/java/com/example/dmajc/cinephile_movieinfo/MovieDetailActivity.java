package com.example.dmajc.cinephile_movieinfo;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class MovieDetailActivity extends AppCompatActivity {
    private TextView mTitleTV;
    private TextView mYearTV;
    private ImageView mPosterIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Intent intentThatStartedThisActivity = getIntent();
        String movieTitle = intentThatStartedThisActivity.getStringExtra("MOVIE_TITLE");
        String moviePosterPath = intentThatStartedThisActivity.getStringExtra("POSTER_PATH");
        String movieYear = intentThatStartedThisActivity.getStringExtra("MOVIE_YEAR");

        mTitleTV = (TextView) findViewById(R.id.tv_movie_detail_title);
        mYearTV = (TextView) findViewById(R.id.tv_movie_detail_year);
        mPosterIV = (ImageView) findViewById(R.id.iv_movie_detail_poster);

        mTitleTV.setText(movieTitle);
        mYearTV.setText(movieYear);
        Glide.with(this).load(moviePosterPath).into(mPosterIV);
    }
}
