package com.example.dmajc.cinephile_movieinfo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dmajc.cinephile_movieinfo.R;
import com.uwetrottmann.tmdb2.entities.CastMember;

import org.w3c.dom.Text;

import java.util.List;

import static android.support.v7.appcompat.R.styleable.View;

/**
 * Created by dmajcenic on 20.06.17..
 */

public class CreditsAdapter  extends RecyclerView.Adapter<CreditsAdapter.CreditsViewHolder>{

    private Context context;

    public CreditsAdapter (Context context) {
        this.context = context;
    }

    public List<CastMember> credits;

    public class CreditsViewHolder extends RecyclerView.ViewHolder {
        public ImageView mCreditImage;
        public TextView mCastName;

        public CreditsViewHolder(View view) {
            super (view);
            mCreditImage = (ImageView) view.findViewById(R.id.iv_credits);
            mCastName = (TextView) view.findViewById(R.id.tv_credit_list_item_name);
        }
    }

    public void setCredits (List<CastMember> credits) {
        this.credits = credits;
        notifyDataSetChanged();
    }

    @Override
    public CreditsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.credit_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        CreditsViewHolder viewHolder = new CreditsViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CreditsAdapter.CreditsViewHolder holder, int position) {
        CastMember castMember = credits.get(position);
        Glide.with(context).load("https://image.tmdb.org/t/p/w500" + castMember.profile_path).placeholder(R.drawable.boss).into(holder.mCreditImage);
        holder.mCastName.setText(castMember.name);
    }

    @Override
    public int getItemCount() {
        if (null == credits)
            return 0;
        return credits.size();
    }
}
