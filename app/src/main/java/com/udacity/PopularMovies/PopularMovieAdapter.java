package com.udacity.PopularMovies;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import com.udacity.PopularMovies.model.MovieItem;
import com.udacity.PopularMovies.utils.JsonUtils;

import java.net.URL;

public class PopularMovieAdapter extends MyRecyclerView.Adapter<PopularMovieAdapter.PMViewHolder> {

    private MovieItem[] moviesData;
    private Context rcContext;
    private final PopularMovieAdapterOnClickHandler mClickHandler;

    public interface PopularMovieAdapterOnClickHandler {
        void onClick(View v, MovieItem aMovie);
    }

    public PopularMovieAdapter(PopularMovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }


    public class PMViewHolder extends    MyRecyclerView.ViewHolder
                              implements View.OnClickListener,
                                         View.OnLongClickListener   {


        public final ImageView movieImage;

        public PMViewHolder(View view) {
            super(view);
            movieImage = (ImageView) view.findViewById(R.id.imageView_pm);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieItem thisMovie = moviesData[adapterPosition];
            //ImageView iv = (ImageView) v.findViewById(R.id.imageView_pm);
            mClickHandler.onClick(v,thisMovie);
        }


        @Override
        public boolean onLongClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieItem thisMovie = moviesData[adapterPosition];
            Toast.makeText(rcContext, thisMovie.getOriginal_title(), Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    @Override
    public PMViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout in the view and return the viewholder
        Context context = parent.getContext();
        rcContext=context;
        int layoutIdForMovies = R.layout.popular_movies;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForMovies, parent, false);
        return new PMViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PMViewHolder holder, int position) {
        //Get the data[position] and load it in the viewholder
        if ((getItemCount()!=0) && (!moviesData[position].equals(""))) {
            //example uri : http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
            String url = JsonUtils.POSTER_BASE_URL + moviesData[position].getPoster_path();   //   moviesData[position]

            if (holder.movieImage!=null) {
                Picasso.with(rcContext).load(url)
                        //.placeholder(R.drawable.ic_error)
                        .error(R.drawable.ic_error)
                        .into(holder.movieImage);
            }
        }
    }

    @Override
    public int getItemCount() {
        int len=0;
        if (moviesData!=null)
           len = moviesData.length;
        return len;
    }

    public void setMoviesData(MovieItem[] movies) {
        moviesData = movies;
        notifyDataSetChanged();
    }

}
