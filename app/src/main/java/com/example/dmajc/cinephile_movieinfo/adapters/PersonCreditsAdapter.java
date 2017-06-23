package com.example.dmajc.cinephile_movieinfo.adapters;

import android.content.Context;
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
import com.uwetrottmann.tmdb2.entities.CastMember;
import com.uwetrottmann.tmdb2.entities.PersonCastCredit;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by dmajcenic on 21.06.17..
 */

public class PersonCreditsAdapter extends RecyclerView.Adapter<PersonCreditsAdapter.PersonCreditsViewHolder>{

    private Context context;
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex, String title, String posterPath, int ID, int releaseYear);
    }

    final private PersonCreditsAdapter.ListItemClickListener mOnClickListener;

    public PersonCreditsAdapter (Context context, PersonCreditsAdapter.ListItemClickListener listener) {
        this.context = context;
        mOnClickListener = listener;
    }

    public List<PersonCastCredit> credits;
    public int releaseYear;

    public class PersonCreditsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mPoster;
        public TextView mTitle, mYear, mCharacter;
        public RelativeLayout mListItemRL;

        public PersonCreditsViewHolder(View view) {
            super (view);
            mPoster = (ImageView) view.findViewById(R.id.person_credit_list_poster);
            mListItemRL = (RelativeLayout) view.findViewById(R.id.person_credit_list_rl);
            mListItemRL.setOnClickListener(this);
            mTitle = (TextView) view.findViewById(R.id.tv_person_credit_list_title);
            mYear = (TextView) view.findViewById(R.id.tv_person_credit_list_year);
            mCharacter = (TextView) view.findViewById(R.id.tv_person_credit_list_character);
        }

        @Override
        public void onClick(View v) {
            int itemPosition = getAdapterPosition();
            Calendar calendar = new GregorianCalendar();
            try {
                calendar.setTime(credits.get(itemPosition).release_date);
                releaseYear = calendar.get(Calendar.YEAR);
            } catch (Exception ex) {
                ex.printStackTrace();
                releaseYear = 0;
            }
            mOnClickListener.onListItemClick(itemPosition, credits.get(itemPosition).title, "https://image.tmdb.org/t/p/w500" + credits.get(itemPosition).poster_path, credits.get(itemPosition).id, releaseYear);
        }
    }

    public void setCredits (List<PersonCastCredit> castCredits) {
        this.credits = castCredits;
        notifyDataSetChanged();
    }

    @Override
    public PersonCreditsAdapter.PersonCreditsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.person_credit_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        PersonCreditsAdapter.PersonCreditsViewHolder viewHolder = new PersonCreditsAdapter.PersonCreditsViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PersonCreditsAdapter.PersonCreditsViewHolder holder, int position) {
        PersonCastCredit personCastCredit = credits.get(position);
        Glide.with(context).load("https://image.tmdb.org/t/p/w500" + personCastCredit.poster_path).centerCrop().into(holder.mPoster);
        holder.mTitle.setText(personCastCredit.title);
        Calendar calendar = new GregorianCalendar();
        try {
            calendar.setTime(credits.get(position).release_date);
            releaseYear = calendar.get(Calendar.YEAR);
        } catch (Exception ex) {
            ex.printStackTrace();
            releaseYear = 0;
        }
        holder.mYear.setText(Integer.toString(releaseYear));
        holder.mCharacter.setText(personCastCredit.character);
    }

    @Override
    public int getItemCount() {
        if (null == credits)
            return 0;
        return credits.size();
    }
}
