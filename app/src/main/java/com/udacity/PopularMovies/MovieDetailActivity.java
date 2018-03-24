package com.udacity.PopularMovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.PopularMovies.adapters.ReviewsAdapter;
import com.udacity.PopularMovies.adapters.TrailersAdapter;
import com.udacity.PopularMovies.model.MovieInfo;
import com.udacity.PopularMovies.model.MovieItem;
import com.udacity.PopularMovies.model.ReviewItem;
import com.udacity.PopularMovies.model.TrailerItem;
import com.udacity.PopularMovies.utils.JsonUtils;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MovieDetailActivity extends AppCompatActivity
         implements LoaderCallbacks<MovieInfo> {

    private static final int  MOVIEDB_DETAIL_LOADER_ID = 26;

    public static final String MOVIE_OBJ_EXTRA = "MOVIES_ARR";
    private String mMovieId;

    @BindView(R.id.moviePoster)       ImageView    m_poster;
    @BindView(R.id.backDropImage)     ImageView    m_bckDropImg;
    @BindView(R.id.title_tv)          TextView     m_title;
    @BindView(R.id.date_released_tv)  TextView     m_releaseDate;
    @BindView(R.id.vote_average_tv)   TextView     m_voteAverage;
    @BindView(R.id.overview_tv)       TextView     m_overview;
    @BindView(R.id.voter_average)     RatingBar    m_voterAverage;
    @BindView(R.id.addFavBtn)         ImageButton  mFavBtn;
    @BindView(R.id.reviews_rv)        RecyclerView mReviewsRecyclerView;
    @BindView(R.id.trailers_rv)       RecyclerView mTrailersRecyclerView;

    private ReviewsAdapter  mReviewsAdapter;
    private TrailersAdapter mTrailersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_detail);
        ButterKnife.bind(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mReviewsRecyclerView.setLayoutManager(layoutManager);
        mReviewsRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mTrailersRecyclerView.setLayoutManager(layoutManager2);
        mTrailersRecyclerView.setHasFixedSize(true);

        Intent intent = getIntent();
        if ((intent.getExtras()!=null) && (intent.hasExtra(MOVIE_OBJ_EXTRA))) {
            MovieItem thisMovie =  (MovieItem) intent.getExtras().get(MOVIE_OBJ_EXTRA);
            if (thisMovie.getOriginal_title()!=null)
                Toast.makeText(this,thisMovie.getOriginal_title(),Toast.LENGTH_SHORT).show();

            //String url = JsonUtils.POSTER_BASE_URL + thisMovie.getPoster_path();   //   moviesData[position]
            String url = JsonUtils.POSTER_BASE_URL +JsonUtils.W500+thisMovie.getBackdrop_path();   //   moviesData[position]
            Picasso.with(this).load(url).error(R.drawable.ic_error).into(m_bckDropImg);
            String url2 = JsonUtils.POSTER_BASE_URL +JsonUtils.W185+thisMovie.getPoster_path();
            Picasso.with(this).load(url2).error(R.drawable.ic_error).into(m_poster);
            mMovieId = Integer.toString(thisMovie.getId());
            m_title.setText(thisMovie.getOriginal_title());
            m_releaseDate.setText(thisMovie.getRelease_date_printable());
            m_voteAverage.setText(String.valueOf(thisMovie.getVote_average()));
            m_overview.setText(thisMovie.getOverview());
            m_voterAverage.setRating(thisMovie.getVote_average());

            getSupportLoaderManager().initLoader(MOVIEDB_DETAIL_LOADER_ID,null ,this);
        }

        ScrollView sv = (ScrollView)findViewById(R.id.mainscrollview);
        sv.post(new Runnable() {
            public void run() {
                ScrollView sv = (ScrollView)findViewById(R.id.mainscrollview);
                sv.fullScroll(sv.FOCUS_UP);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        //Up button must behave like back button otherwise animation doesn't show
        onBackPressed();
        return true;
    }

    @OnClick(R.id.addFavBtn)
    public void addFavClick(View v) {
        int drawableResourceId=0;
        if (mFavBtn.getTag()!=null) drawableResourceId = (int) mFavBtn.getTag();

        if (drawableResourceId==1) {
            mFavBtn.setImageResource(R.drawable.ic_favorite_border_red_24dp);
            mFavBtn.setTag(0);
        } else {
            mFavBtn.setImageResource(R.drawable.ic_favorite_red_24dp);
            mFavBtn.setTag(1);
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<MovieInfo> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<MovieInfo>(this) {
            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Nullable
            @Override
            public MovieInfo loadInBackground() {
                try {

                    URL movieDBURL;
                    String jsonMovieDBResponse="";
                    movieDBURL = JsonUtils.buildUrl(mMovieId+"/reviews");
                    jsonMovieDBResponse = JsonUtils.getResponseFromHttpUrl(movieDBURL);
                    ReviewItem[]  jsonReviews  = JsonUtils.parseReviewsJson(jsonMovieDBResponse);
                    movieDBURL = JsonUtils.buildUrl(mMovieId+"/videos");
                    jsonMovieDBResponse = JsonUtils.getResponseFromHttpUrl(movieDBURL);
                    TrailerItem[] jsonTrailers = JsonUtils.parseTrailersJson(jsonMovieDBResponse);

                    return new MovieInfo(mMovieId,jsonReviews,jsonTrailers);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<MovieInfo> loader, MovieInfo movieInfo) {
        if (movieInfo!=null) {

            //Set recyclerviews Adapters here
            if ((movieInfo!=null) && (movieInfo.getReviews()!=null)) {
                mReviewsAdapter = new ReviewsAdapter();
                mReviewsRecyclerView.setAdapter(mReviewsAdapter);
                mReviewsAdapter.setReviewsData(movieInfo.getReviews());

            }
            if ((movieInfo!=null) && (movieInfo.getTrailers()!=null)) {
                mTrailersAdapter = new TrailersAdapter();
                mTrailersRecyclerView.setAdapter(mTrailersAdapter);
                mTrailersAdapter.setTrailersData(movieInfo.getTrailers());
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<MovieInfo> loader) {

    }

}
