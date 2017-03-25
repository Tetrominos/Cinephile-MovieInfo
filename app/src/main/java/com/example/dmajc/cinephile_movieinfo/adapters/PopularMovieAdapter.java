package com.example.dmajc.cinephile_movieinfo.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dmajc.cinephile_movieinfo.R;
import com.example.dmajc.cinephile_movieinfo.utilities.GetPosterFromUrl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by dmajc on 19.3.2017..
 */

public class PopularMovieAdapter extends RecyclerView.Adapter<PopularMovieAdapter.PopularMovieViewHolder> {

    private int mNumberItems;

    public static final String TAG = "PopularMovieAdapter";

    private String[] mMovieData;

    private Context context;

    public PopularMovieAdapter(Context context) {
        this.context = context;
    }
    @Override
    public PopularMovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        PopularMovieViewHolder viewHolder = new PopularMovieViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PopularMovieViewHolder holder, int position) {
        String movie = mMovieData[position];

        try {
            /*Drawable poster = new GetPosterFromUrl(movie).getPosterAsDrawable();
            holder.listItemImageView.setImageDrawable(poster);*/
            int firstSpace = movie.indexOf(" ");
            String partialUrl = movie.substring(0, movie.indexOf(" "));
            String url = "https://image.tmdb.org/t/p/w500" + partialUrl;
            String MovieName = movie.substring(movie.indexOf("-")+1, movie.indexOf("-", movie.indexOf("-") + 1));
            holder.listItemTitleTextView.setText(MovieName);
            Glide.with(context).load(url).into(holder.listItemImageView);
            Log.v(TAG, "image was set");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //Bitmap poster = new GetPosterFromUrl(movie).getPosterAsBitmap();
        //holder.listItemImageView.setImageBitmap(poster);
    }

    @Override
    public int getItemCount() {
        if (null == mMovieData) return 0;
        return mMovieData.length;
    }

    public class PopularMovieViewHolder extends RecyclerView.ViewHolder {

        public final ImageView listItemImageView;
        public TextView listItemTitleTextView;

        public PopularMovieViewHolder(View itemView) {
            super(itemView);

            listItemImageView = (ImageView) itemView.findViewById(R.id.iv_movie_item);
            listItemTitleTextView = (TextView) itemView.findViewById(R.id.tv_movie_item);
        }
    }

    public void setMovieData(String[] movieData) {
        mMovieData = movieData;
        Log.v(TAG, mMovieData[0]);
        notifyDataSetChanged();
    }
}
