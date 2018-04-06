package com.udacity.PopularMovies;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.udacity.PopularMovies.data.FavoritesDBContract;
import com.udacity.PopularMovies.data.FavoritesDBContract.FavoritesEntry;

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
    private String    mMovieId;
    private MovieItem mMovie;

    @BindView(R.id.moviePoster)       ImageView    m_poster;
    @BindView(R.id.backDropImage)     ImageView    m_bckDropImg;
    @BindView(R.id.title_tv)          TextView     m_title;
    @BindView(R.id.date_released_tv)  TextView     m_releaseDate;
    @BindView(R.id.vote_average_tv)   TextView     m_voteAverage;
    @BindView(R.id.overview_tv)       TextView     m_overview;
    @BindView(R.id.voter_average)     RatingBar    m_voterAverage;
    @BindView(R.id.addFavBtn)         ImageButton  mFavBtn;
    @BindView(R.id.reviews_rv)        MyRecyclerView mReviewsRecyclerView;
    @BindView(R.id.trailers_rv)       MyRecyclerView mTrailersRecyclerView;
    @BindView(R.id.trailers_title_tv) TextView     mTrailersTitleRv;
    @BindView(R.id.reviews_title_tv)  TextView     mReviewsTitleRv;
    @BindView(R.id.mainScrollView)    ScrollView   mScrollView;

    private ReviewsAdapter  mReviewsAdapter;
    private TrailersAdapter mTrailersAdapter;
    //private SQLiteDatabase  mDb;

    private Parcelable mRecyclerViewState;
    private Parcelable mRecyclerViewState2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_detail);
        ButterKnife.bind(this);

        //FavoritesDBHelper dbHelper = new FavoritesDBHelper(this);
        //mDb = dbHelper.getWritableDatabase();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mReviewsRecyclerView.setLayoutManager(layoutManager);
        mReviewsRecyclerView.setHasFixedSize(true);
        mReviewsRecyclerView.setNestedScrollingEnabled(false);

        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mTrailersRecyclerView.setLayoutManager(layoutManager2);
        mTrailersRecyclerView.setHasFixedSize(true);
        mTrailersRecyclerView.setNestedScrollingEnabled(false);

        Intent intent = getIntent();
        if ((intent.getExtras()!=null) && (intent.hasExtra(MOVIE_OBJ_EXTRA))) {

            MovieItem thisMovie =  (MovieItem) intent.getExtras().get(MOVIE_OBJ_EXTRA);
            if (thisMovie!=null) {
                if (thisMovie.getOriginal_title() != null)
                    Toast.makeText(this, thisMovie.getOriginal_title(), Toast.LENGTH_SHORT).show();

                mMovieId = Integer.toString(thisMovie.getId());
                Cursor favQry = getContentResolver().query(
                                                    FavoritesDBContract.CONTENT_URI,
                                                    null,
                                                    FavoritesEntry.COLUMN_MOVIE_ID + "=?",
                                                    new String[]{mMovieId},
                                                    null
                                                    );
                setFavoritesOn(favQry.getCount() != 0);
                favQry.close();
            }

            getSupportLoaderManager().initLoader(MOVIEDB_DETAIL_LOADER_ID,null ,this);
        }

    }

    private void showMovie(MovieItem thisMovie) {
        String url = JsonUtils.POSTER_BASE_URL +JsonUtils.W500+thisMovie.getBackdrop_path();   //   moviesData[position]
        Picasso.with(this).load(url).error(R.drawable.ic_error).into(m_bckDropImg);
        String url2 = JsonUtils.POSTER_BASE_URL +JsonUtils.W185+thisMovie.getPoster_path();
        Picasso.with(this).load(url2).error(R.drawable.ic_error).into(m_poster);
        m_title.setText(thisMovie.getOriginal_title());
        m_releaseDate.setText(getString(R.string.release_date,thisMovie.getRelease_date_printable()));
        m_voteAverage.setText(String.format("%s%s", String.valueOf(thisMovie.getVote_average()), getString(R.string.max_vote)));
        m_overview.setText(thisMovie.getOverview());
        m_voterAverage.setRating(thisMovie.getVote_average());
    }

    @Override
    public boolean onSupportNavigateUp() {
        //Up button must behave like back button otherwise animation doesn't show
        onBackPressed();
        return true;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("SCROLL_POSITION",  new int[]{ mScrollView.getScrollX(), mScrollView.getScrollY()});
        //Note that we don't need to save recyclerview's instance state here because it does internally
        //since MyRecyclerView is derived from RecyclerView and implements onSaveInstaceState and onRestoreInstanceState
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] position = savedInstanceState.getIntArray("SCROLL_POSITION");
        if(position != null)
            mScrollView.post(new Runnable() {
                public void run() {
                    mScrollView.scrollTo(position[0], position[1]);
                }
            });
    }

    @OnClick(R.id.addFavBtn)
    public void addFavClick(View v) {
        int drawableResourceId=0;
        if (mFavBtn.getTag()!=null) drawableResourceId = (int) mFavBtn.getTag();
        ContentResolver cr=getContentResolver();

        if ((mMovie!=null) && (cr!=null)) {
            if (drawableResourceId == 1) {
                if (mMovie.deleteFromDB(cr)) setFavoritesOn(false);
            } else {
                if (!mMovie.ExistInDB(cr) && mMovie.AddToDB(cr)) setFavoritesOn(true);
            }
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

                    //loading reviews
                    String jsonMovieDBResponse;//="";
                    movieDBURL = JsonUtils.buildUrl(mMovieId+"/reviews");
                    jsonMovieDBResponse = JsonUtils.getResponseFromHttpUrl(movieDBURL);
                    ReviewItem[]  jsonReviews  = JsonUtils.parseReviewsJson(jsonMovieDBResponse);

                    //loading trailers
                    movieDBURL = JsonUtils.buildUrl(mMovieId+"/videos");
                    jsonMovieDBResponse = JsonUtils.getResponseFromHttpUrl(movieDBURL);
                    TrailerItem[] jsonTrailers = JsonUtils.parseTrailersJson(jsonMovieDBResponse);

                    //Loading movie
                    movieDBURL             = JsonUtils.buildUrl(mMovieId);
                    jsonMovieDBResponse    = JsonUtils.getResponseFromHttpUrl(movieDBURL);
                    MovieItem jsonMovies   = JsonUtils.parseSingleMovieJson(jsonMovieDBResponse);

                    return new MovieInfo(mMovieId, jsonReviews, jsonTrailers, jsonMovies);

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
            if (movieInfo.getReviews()!=null) {
                if (movieInfo.getReviews().length!=0) {
                    mReviewsTitleRv.setText(getString(R.string.reviews_title));
                    mReviewsAdapter = new ReviewsAdapter();
                    mReviewsRecyclerView.setAdapter(mReviewsAdapter);
                    mReviewsAdapter.setReviewsData(movieInfo.getReviews());
                } else {
                    mReviewsTitleRv.setText(R.string.no_reviews_message);
                }
            }

            if (movieInfo.getTrailers()!=null) {
                if (movieInfo.getTrailers().length!=0) {
                    mTrailersTitleRv.setText(getString(R.string.trailers_title));
                    mTrailersAdapter = new TrailersAdapter();
                    mTrailersRecyclerView.setAdapter(mTrailersAdapter);
                    mTrailersAdapter.setTrailersData(movieInfo.getTrailers());
                } else {
                    mTrailersTitleRv.setText(R.string.no_trailers_message);
                }
            }

            if (movieInfo.getMovie()!=null) {
                mMovie=movieInfo.getMovie();
                showMovie(mMovie);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<MovieInfo> loader) {

    }

    private void setFavoritesOn(boolean showstate) {
        if (showstate) {
            mFavBtn.setImageResource(R.drawable.ic_favorite_red_24dp);
            mFavBtn.setTag(1);}
        else {
            mFavBtn.setImageResource(R.drawable.ic_favorite_border_red_24dp);
            mFavBtn.setTag(0);
        }

    }

}
