package com.udacity.PopularMovies.adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.PopularMovies.R;
import com.udacity.PopularMovies.model.TrailerItem;
import com.udacity.PopularMovies.utils.JsonUtils;


public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {

    private TrailerItem[] mTrailersData;
    private Context rcContext;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView name_tv;
        public final ImageView imgThumbnail;

        public ViewHolder(View view) {
            super(view);
            name_tv      = (TextView)  view.findViewById(R.id.name_tv);
            imgThumbnail = (ImageView) view.findViewById(R.id.imgThumbnail);
            view.setOnClickListener(this);
            //view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            String url = "https://www.youtube.com/watch?v=".concat(mTrailersData[position].getKey());
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            ActivityCompat.startActivity(v.getContext(),i,null);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        rcContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(rcContext);
        View thisView = inflater.inflate(R.layout.trailers_rc_record, parent, false);
        return new ViewHolder(thisView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if ((holder != null) && (getItemCount() != 0)) {
            String content=mTrailersData[position].getName();
            if (content.length()>90) content=content.substring(0,90);
            holder.name_tv.setText(content+"...");
            //Set the thumbnail image
            String thumbnailURL = JsonUtils.makeThumbnailURL(mTrailersData[position].getKey());
            if (holder.imgThumbnail!=null)
                Picasso.with(rcContext).load(thumbnailURL).error(R.drawable.ic_error).into(holder.imgThumbnail);
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
