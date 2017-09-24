package com.example.dmajc.cinephile_movieinfo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dmajc.cinephile_movieinfo.R;
import com.uwetrottmann.tmdb2.entities.Movie;
import com.uwetrottmann.tmdb2.entities.MovieResultsPage;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by dmajc on 19.3.2017..
 */

public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.PopularMovieViewHolder> {

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex, String titleText, String posterPath, String year, int movieID);
    }

    public static final String TAG = "MovieGridAdapter";

    private List<Movie> mMovieData;

    private Context context;

    final private ListItemClickListener mOnClickListener;

    public MovieGridAdapter(Context context, ListItemClickListener listener) {
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
        final Movie movie = mMovieData.get(position);
        final RelativeLayout relativeLayout = (RelativeLayout) holder.itemView.findViewById(R.id.rl_movie_item_info);

        Calendar calendar = new GregorianCalendar();
        try {
            calendar.setTime(movie.release_date);
            holder.listItemYearTextView.setText("(" + calendar.get(Calendar.YEAR) + ")");
        } catch (Exception ex) {
            holder.listItemYearTextView.setText("(can't get year)");
        }

        holder.listItemTitleTextView.setText(movie.title);
        holder.listItemRatingTextView.setText("★" + Double.toString(movie.vote_average));

        Glide.with(context).load("https://image.tmdb.org/t/p/w300" + movie.poster_path).placeholder(R.drawable.movie_night).into(holder.listItemImageView);
        Log.v(TAG, "image was set");

        relativeLayout.setVisibility(View.INVISIBLE);
        holder.listItemImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(relativeLayout.getVisibility() == View.INVISIBLE){
                    relativeLayout.setVisibility(View.VISIBLE);
                } else {
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

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class PopularMovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView listItemImageView;
        public TextView listItemTitleTextView;
        public RelativeLayout listItemRelativeLayout;
        public TextView listItemYearTextView;
        public TextView listItemRatingTextView;
        public ImageButton listItemImageButton;

        public PopularMovieViewHolder(View itemView) {
            super(itemView);

            listItemImageView = (ImageView) itemView.findViewById(R.id.iv_movie_item);
            listItemTitleTextView = (TextView) itemView.findViewById(R.id.tv_movie_item_title);
            listItemRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.rl_movie_item_info);
            listItemImageButton = (ImageButton) itemView.findViewById(R.id.ib_go);
            listItemImageButton.setOnClickListener(this);
            listItemYearTextView = (TextView) itemView.findViewById(R.id.tv_movie_item_year);
            listItemRatingTextView = (TextView) itemView.findViewById(R.id.tv_movie_item_rating);
        }

        @Override
        public void onClick(View v) {
            int itemPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(itemPosition, listItemTitleTextView.getText().toString(), "https://image.tmdb.org/t/p/w300" + mMovieData.get(itemPosition).poster_path, listItemYearTextView.getText().toString(), mMovieData.get(itemPosition).id);
        }
    }

    public void setMovieData(MovieResultsPage movieData) {
        if (movieData == null) {
            return;
        }
        mMovieData = movieData.results;
        notifyDataSetChanged();
    }
}
