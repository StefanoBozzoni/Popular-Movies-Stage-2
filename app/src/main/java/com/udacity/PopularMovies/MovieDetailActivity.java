package com.udacity.PopularMovies;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.PopularMovies.model.MovieItem;
import com.udacity.PopularMovies.utils.JsonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String MOVIE_OBJ_EXTRA = "MOVIES_ARR";

    @BindView(R.id.moviePoster)       ImageView m_poster;
    @BindView(R.id.title_tv)          TextView  m_title;
    @BindView(R.id.date_released_tv)  TextView  m_releaseDate;
    @BindView(R.id.vote_average_tv)   TextView  m_voteAverage;
    @BindView(R.id.overview_tv)       TextView  m_overview;
    @BindView(R.id.voter_average)     RatingBar m_voterAverage;

    private static final int ORIENTATION_90 = 1;
    private static final int ORIENTATION_0 = 0;
    private static final int ORIENTATION_180 = 2;
    private static final int ORIENTATION_270 = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        //Disable rotation because animation would be wrong returning back to mainactivity
        int orientation = this.getResources().getConfiguration().orientation;
        final int rotation = this.getWindowManager().getDefaultDisplay().getOrientation();

        if (orientation == Configuration.ORIENTATION_PORTRAIT)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (rotation == Surface.ROTATION_180)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (orientation==Surface.ROTATION_270)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

        Intent intent = getIntent();
        if ((intent.getExtras()!=null) && (intent.hasExtra(MOVIE_OBJ_EXTRA))) {
            MovieItem thisMovie =  (MovieItem) intent.getExtras().get(MOVIE_OBJ_EXTRA);
            if (thisMovie.getOriginal_title()!=null)
                Toast.makeText(this,thisMovie.getOriginal_title(),Toast.LENGTH_SHORT).show();

            String url = JsonUtils.POSTER_BASE_URL + thisMovie.getPoster_path();   //   moviesData[position]
            Picasso.with(this).load(url).error(R.drawable.ic_error).into(m_poster);

            m_title.setText(thisMovie.getOriginal_title());
            m_releaseDate.setText(thisMovie.getRelease_date_printable());
            m_voteAverage.setText(String.valueOf(thisMovie.getVote_average()));
            m_overview.setText(thisMovie.getOverview());
            m_voterAverage.setRating(thisMovie.getVote_average());
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        //Up button must behave like back button otherwise animation doesn't show
        onBackPressed();
        return true;
    }
}
