package com.example.dmajc.cinephile_movieinfo.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.example.dmajc.cinephile_movieinfo.MainActivity;
import com.example.dmajc.cinephile_movieinfo.MovieDetailActivity;
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

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex, String titleText, String posterPath, String year);
    }

    public static final String TAG = "PopularMovieAdapter";

    private ArrayList<QueryResult> mMovieData;

    private Context context;

    final private ListItemClickListener mOnClickListener;

    public PopularMovieAdapter(Context context, ListItemClickListener listener) {
        this.context = context;
        mOnClickListener = listener;
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
        final QueryResult movie = mMovieData.get(position);
        final RelativeLayout relativeLayout = (RelativeLayout) holder.itemView.findViewById(R.id.rl_movie_item_info);

        holder.listItemTitleTextView.setText(movie.getName().trim());
        holder.listItemYearTextView.setText("(" + movie.getReleaseDate().substring(0, movie.getReleaseDate().indexOf("-")) + ")");
        Glide.with(context).load(movie.getImagePath()).into(holder.listItemImageView);
        Log.v(TAG, "image was set");
        //to be able to toggle visibility of a particular RecyclerView item without it getting recycled, the below answer was used
        //http://stackoverflow.com/questions/30584141/recyclerview-ambiguos-setvisibility-function-clicking-on-one-view-affects-multi
        if(movie.isClicked()){
            relativeLayout.setVisibility(View.VISIBLE);
        } else if (!movie.isClicked()) {
            relativeLayout.setVisibility(View.INVISIBLE);
        }
        holder.listItemImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                movie.toggle();
                if(movie.isClicked()){
                    relativeLayout.setVisibility(View.VISIBLE);
                } else if (!movie.isClicked()) {
                    relativeLayout.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (null == mMovieData) return 0;
        return mMovieData.size();
    }

    public class PopularMovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView listItemImageView;
        public TextView listItemTitleTextView;
        public RelativeLayout listItemRelativeLayout;
        public TextView listItemYearTextView;
        public ImageView listItemImageButtonView;

        public PopularMovieViewHolder(View itemView) {
            super(itemView);

            listItemImageView = (ImageView) itemView.findViewById(R.id.iv_movie_item);
            listItemTitleTextView = (TextView) itemView.findViewById(R.id.tv_movie_item_title);
            listItemRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.rl_movie_item_info);
            listItemYearTextView = (TextView) itemView.findViewById(R.id.tv_movie_item_year);
            listItemImageButtonView = (ImageView) itemView.findViewById(R.id.iv_media_type);
            listItemImageButtonView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int itemPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(itemPosition, listItemTitleTextView.getText().toString(), mMovieData.get(itemPosition).getImagePath(), listItemYearTextView.getText().toString());
        }
    }

    public void setMovieData(ArrayList<QueryResult> movieData) {
        mMovieData = movieData;
        Log.v(TAG, "Movie data was set for " + mMovieData.get(0).getName());
        notifyDataSetChanged();
    }
}
