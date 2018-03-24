package com.udacity.PopularMovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.PopularMovies.R;
import com.udacity.PopularMovies.model.ReviewItem;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.PopularMovies.R;
import com.udacity.PopularMovies.model.TrailerItem;


public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {

    TrailerItem[] mTrailersData;


    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView name_tv;

        public ViewHolder(View view) {
            super(view);
            name_tv = (TextView) view.findViewById(R.id.content_tv);
            //(TextView) view.findViewById(R.id.content_tv);
            //view.setOnClickListener(this);
            //view.setOnLongClickListener(this);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context cnx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(cnx);
        View thisView = inflater.inflate(R.layout.reviews_rc_record, parent, false);
        return new ViewHolder(thisView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if ((holder != null) && (getItemCount() != 0)) {
            String content=mTrailersData[position].getName();
            if (content.length()>90) content=content.substring(0,90);

            holder.name_tv.setText(content+"...");
        }
    }

    @Override
    public int getItemCount() {
        int len = 0;
        if (mTrailersData != null)
            len = mTrailersData.length;
        return len;
    }

    public void setTrailersData(TrailerItem[] trailer) {
        mTrailersData = trailer;
        notifyDataSetChanged();
    }

}
