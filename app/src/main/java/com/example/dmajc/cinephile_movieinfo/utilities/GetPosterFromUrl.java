package com.example.dmajc.cinephile_movieinfo.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by dmajc on 20.3.2017..
 */

public class GetPosterFromUrl {

    private static final String TAG = "GetPosterFromUrl";
    String movieInfo;

    public GetPosterFromUrl (String movieInfo) {
        this.movieInfo = movieInfo;
    }

    public Drawable getPosterAsDrawable() {
        /*try {
            return new RetrieveDrawable().get();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;*/
        String partialUrl = movieInfo.substring(0, movieInfo.indexOf(" "));
        String url = "https://image.tmdb.org/t/p/w342" + partialUrl;
        /*Drawable posterAsDrawable = LoadImageFromWebOperations(url);
        Log.v(TAG, "loading poster from " + url);
        return posterAsDrawable;*/
        try {
            return new RetrieveDrawable().execute(url).get();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Bitmap getPosterAsBitmap() {
        try {
            return new RetrieveBitmap().get();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    private class RetrieveDrawable extends AsyncTask<String, Void, Drawable> {

        @Override
        protected Drawable doInBackground(String... params) {
            String url = params[0];
            Bitmap x;

            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.connect();
                InputStream input = connection.getInputStream();
                x = BitmapFactory.decodeStream(input);
                return new BitmapDrawable(x);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }
    }

    private class RetrieveBitmap extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... params) {
            String partialUrl = movieInfo.substring(0, movieInfo.indexOf(" "));
            String url = "https://image.tmdb.org/t/p/w500" + partialUrl;
            try {
                return BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
