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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dmajc.cinephile_movieinfo.R;
import com.example.dmajc.cinephile_movieinfo.utilities.GetPosterFromUrl;
import com.example.dmajc.cinephile_movieinfo.utilities.QueryResult;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by dmajc on 19.3.2017..
 */

public class PopularMovieAdapter extends RecyclerView.Adapter<PopularMovieAdapter.PopularMovieViewHolder> {

    private int mNumberItems;

    public static final String TAG = "PopularMovieAdapter";

    private ArrayList<QueryResult> mMovieData;

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
        QueryResult movie = mMovieData.get(position);

        try {
            holder.listItemTitleTextView.setText(movie.getName());
            holder.listItemYearTextView.setText("(" + movie.getReleaseDate().substring(0, movie.getReleaseDate().indexOf("-")) + ")");
            Glide.with(context).load(movie.getImagePath()).into(holder.listItemImageView);
            Log.v(TAG, "image was set");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        final PopularMovieViewHolder newHolder = holder;
        holder.listItemImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(newHolder.listItemRelativeLayout.getVisibility() == View.INVISIBLE) {
                    newHolder.listItemRelativeLayout.setVisibility(View.VISIBLE);
                } else {
                    newHolder.listItemRelativeLayout.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (null == mMovieData) return 0;
        return mMovieData.size();
    }

    public class PopularMovieViewHolder extends RecyclerView.ViewHolder {

        public final ImageView listItemImageView;
        public TextView listItemTitleTextView;
        public RelativeLayout listItemRelativeLayout;
        public TextView listItemYearTextView;

        public PopularMovieViewHolder(View itemView) {
            super(itemView);

            listItemImageView = (ImageView) itemView.findViewById(R.id.iv_movie_item);
            listItemTitleTextView = (TextView) itemView.findViewById(R.id.tv_movie_item_title);
            listItemRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.rl_movie_item_info);
            listItemYearTextView = (TextView) itemView.findViewById(R.id.tv_movie_item_year);
        }

    }

    public void setMovieData(ArrayList<QueryResult> movieData) {
        mMovieData = movieData;
        Log.v(TAG, "Movie data was set for " + mMovieData.get(0).getName());
        notifyDataSetChanged();
    }
}
