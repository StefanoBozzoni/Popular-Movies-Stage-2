package com.udacity.PopularMovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.PopularMovies.R;
import com.udacity.PopularMovies.model.ReviewItem;


public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    ReviewItem[] mReviewsData;


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView content_tv;

        public ViewHolder(View view) {
            super(view);
            content_tv = (TextView) view.findViewById(R.id.content_tv);
            //(TextView) view.findViewById(R.id.content_tv);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            int adapterPosition = getAdapterPosition();
            i.setData(Uri.parse(mReviewsData[adapterPosition].getUrl()));
            ActivityCompat.startActivity(v.getContext(),i,null);
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
            String content=mReviewsData[position].getContent();
            if (content.length()>90) content=content.substring(0,90);

            holder.content_tv.setText(content+"...");
        }
    }

    @Override
    public int getItemCount() {
        int len = 0;
        if (mReviewsData != null)
            len = mReviewsData.length;
        return len;
    }

    public void setReviewsData(ReviewItem[] reviews) {
        mReviewsData = reviews;
        notifyDataSetChanged();
    }

}
