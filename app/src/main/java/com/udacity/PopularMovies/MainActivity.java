package com.udacity.PopularMovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.udacity.PopularMovies.adapters.MoviesAdapter;
import com.udacity.PopularMovies.data.FavoritesDBContract;
import com.udacity.PopularMovies.data.FavoritesDBHelper;
import com.udacity.PopularMovies.model.MovieItem;
import com.udacity.PopularMovies.utils.JsonUtils;
import com.udacity.PopularMovies.utils.SingleToast;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

//prova
public class MainActivity extends AppCompatActivity
         implements MoviesAdapter.PopularMovieAdapterOnClickHandler,
                    LoaderManager.LoaderCallbacks<MovieItem[]> {

    private MoviesAdapter mMoviesAdapter;
    private Parcelable mRecyclerViewState;
    @BindView(R.id.movies_rv)   MyRecyclerView myRecyclerView;
    @BindView(R.id.progressBar) ProgressBar myProgressBar;

    private static final int    MOVIEDB_SEARCH_LOADER_ID = 25;
    private static final String SEARCH_QUERY_URL_EXTRA   = "QUERY";
    private static final String MOST_POPULAR_QUERY_TAG   = "popular";
    private static final String FAVORITES_QUERY_TAG      = "favorites";
    private static final String TOP_RATED_QUERY_TAG      = "top_rated";
    private static final String MENU_FILTER_TAG          = "used_filter_choice";
    private static final String RECYCLER_VIEW_STATE      = "RecyclerView-State";
    private static final int GRID_SPAN_COUNT_LANDSCAPE   = 5;
    private static final int GRID_SPAN_COUNT_PORTRAIT    = 3;

    private Menu mMenu;
    private boolean appIsLaunched;
    private Toolbar mTopToolbar;
    private SQLiteDatabase mDb;
    private MenuItem mSelectedMenuItem;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mRecyclerViewState=myRecyclerView.onSaveInstanceState();
        outState.putParcelable(RECYCLER_VIEW_STATE,mRecyclerViewState);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.mipmap.ic_movie_logo_round);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

        if (savedInstanceState!=null)
           mRecyclerViewState=savedInstanceState.getParcelable(RECYCLER_VIEW_STATE);
        setContentView(R.layout.activity_main);

        //mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        //setSupportActionBar(mTopToolbar);

        ButterKnife.bind(this);

        int orientation=this.getResources().getConfiguration().orientation;
        int spanCount=((orientation == Configuration.ORIENTATION_PORTRAIT) ? GRID_SPAN_COUNT_PORTRAIT : GRID_SPAN_COUNT_LANDSCAPE);
        MyRecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        myRecyclerView.setLayoutManager(layoutManager);
        myRecyclerView.setHasFixedSize(true);
        final MyApplication myApplication = (MyApplication) getApplicationContext();
        appIsLaunched = myApplication.isJustStarted();
        myApplication.setJustStarted(false);

        Bundle queryBundle = new Bundle();

        if (appIsLaunched) {
            myRecyclerView.resetScrollPosition();
            queryBundle.putString(SEARCH_QUERY_URL_EXTRA, MOST_POPULAR_QUERY_TAG); //set loader with most popular filter

        } else {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String current_Tag_filter = prefs.getString(MENU_FILTER_TAG, MOST_POPULAR_QUERY_TAG);
            queryBundle.putString(SEARCH_QUERY_URL_EXTRA, convertMenuTagToQueryTag(current_Tag_filter)); //restore loader with last used filter
        }

        // Create a DB helper (this will create the DB if run for the first time)
        FavoritesDBHelper dbHelper = new FavoritesDBHelper(this);
        mDb = dbHelper.getWritableDatabase();

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(MOVIEDB_SEARCH_LOADER_ID, queryBundle, MainActivity.this);
    }

    private String convertMenuTagToQueryTag(String current_tag_filter) {
        if (current_tag_filter.equals(getString(R.string.popular_menu)))
           return MOST_POPULAR_QUERY_TAG;
        if (current_tag_filter.equals(getString(R.string.topRated_menu)))
           return TOP_RATED_QUERY_TAG;
        if (current_tag_filter.equals(getString(R.string.favorites_menu)))
            return FAVORITES_QUERY_TAG;
        return MOST_POPULAR_QUERY_TAG;
    }

    private MovieItem[] loadFavoritesFromCursor() {
        Cursor favQry = mDb.query(
                FavoritesDBContract.FavoritesEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                FavoritesDBContract.FavoritesEntry.COLUMN_MOVIE_ID);

        MovieItem[] resMovies = new MovieItem[favQry.getCount()];
        favQry.moveToFirst();
        //iterate on cursor loading movies....
        for (int i=0;i<favQry.getCount();i++) {
            resMovies[i]= new MovieItem();
            int mId= favQry.getInt(favQry.getColumnIndex(FavoritesDBContract.FavoritesEntry.COLUMN_MOVIE_ID));
            String ppath=favQry.getString(favQry.getColumnIndex(FavoritesDBContract.FavoritesEntry.COLUMN_POSTER_PATH));
            resMovies[i].setId(mId);
            resMovies[i].setPoster_path(ppath);
            favQry.moveToNext();
        }
        favQry.close();
        return resMovies;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<MovieItem[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<MovieItem[]>(this) {
            MovieItem[] mMovies = null;

            @Override
            protected void onStartLoading() {
                myProgressBar.setVisibility(View.VISIBLE);
                //ProgressDialog m_PD_loadingIndicator = ProgressDialog.show(MainActivity.this, "Wait...", "loading movies...", true);
                if (mMovies != null) {
                    deliverResult(mMovies);
                    myProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    forceLoad();
                }
            }

            @Override
            public MovieItem[] loadInBackground() {
                String tipoQuery;

                if (args!=null)
                    tipoQuery = args.getString(SEARCH_QUERY_URL_EXTRA);
                else
                    tipoQuery = MOST_POPULAR_QUERY_TAG;

                URL MovieDBURL;
                MovieItem[] moviesToShow=null;
                if (tipoQuery!=null) {
                    if (tipoQuery.equals(FAVORITES_QUERY_TAG)) {
                        moviesToShow = loadFavoritesFromCursor();
                    } else {
                        if (tipoQuery.equals(MOST_POPULAR_QUERY_TAG))
                            MovieDBURL = JsonUtils.buildUrl(MOST_POPULAR_QUERY_TAG);
                        else
                            MovieDBURL = JsonUtils.buildUrl(TOP_RATED_QUERY_TAG);

                        try {
                            String jsonMovieDBResponse = JsonUtils.getResponseFromHttpUrl(MovieDBURL);
                            moviesToShow = JsonUtils.parseMoviesJson(jsonMovieDBResponse);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                }
                return moviesToShow;
            }

            @Override
            public void deliverResult(MovieItem[] movies) {
                mMovies=movies;
                super.deliverResult(movies);
            }
        };
    }


    @Override
    public void onLoadFinished(Loader<MovieItem[]> loader, MovieItem[] movies) {
        myProgressBar.setVisibility(View.INVISIBLE);

        if (movies!=null) {
            //doesn't need anymore : myRecyclerView.restoreScrollPosition();
            mMoviesAdapter = new MoviesAdapter(this);
            myRecyclerView.setAdapter(mMoviesAdapter); /* Setting the adapter attaches it to the RecyclerView in our layout. */
            mMoviesAdapter.setMoviesData(movies);
        }
    }

    @Override
    public void onLoaderReset(Loader<MovieItem[]> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main_activity_menu,menu);
        mMenu = menu;
        super.onCreateOptionsMenu(menu);
        if (appIsLaunched) {
            showActiveMenuItem(mMenu,getString(R.string.popular_menu));
        }
        else {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String menuToActivate = prefs.getString(MENU_FILTER_TAG,getString(R.string.popular_menu));
            showActiveMenuItem(mMenu,menuToActivate);
        }
        return true;
    }

    @Override
    public void onClick(View viewStart, MovieItem aMovie) {
        //doesn't need anymore: myRecyclerView.storeScrollPosition();
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.MOVIE_OBJ_EXTRA,aMovie);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //String transitionName=getString(R.string.transition_string);
        //ActivityOptionsCompat options=ActivityOptionsCompat.makeSceneTransitionAnimation(this,viewStart,transitionName);
        //ActivityCompat.startActivity(this,intent,options.toBundle());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if ((item.getItemId()==R.id.topRated) || (item.getItemId()==R.id.mostPopular) || (item.getItemId()==R.id.favMenuItem)) {
            LoaderManager loaderManager=getSupportLoaderManager();
            Bundle queryBundle = new Bundle();
            myRecyclerView.resetScrollPosition();

            if (item.getItemId()==R.id.topRated) {
                queryBundle.putString(SEARCH_QUERY_URL_EXTRA, TOP_RATED_QUERY_TAG);
                showActiveMenuItem(mMenu,getString(R.string.topRated_menu));
                SingleToast.show(this,"Top Rated selection",Toast.LENGTH_SHORT);
            }

            if (item.getItemId()==R.id.mostPopular) {
                queryBundle.putString(SEARCH_QUERY_URL_EXTRA, MOST_POPULAR_QUERY_TAG);
                showActiveMenuItem(mMenu,getString(R.string.popular_menu));
                SingleToast.show(this,"Most Popular selection",Toast.LENGTH_SHORT);
            }

            if (item.getItemId()==R.id.favMenuItem) {
                queryBundle.putString(SEARCH_QUERY_URL_EXTRA, FAVORITES_QUERY_TAG);
                showActiveMenuItem(mMenu,getString(R.string.favorites_menu));
                SingleToast.show(this,"Favorites selection",Toast.LENGTH_SHORT);
            }
            /*
            for (int i=0;i<=2;i++) {
                mSelectedMenuItem = mMenu.getItem(i);
                Drawable drawableIcon = mSelectedMenuItem.getIcon();
                if (drawableIcon != null) drawableIcon.mutate();
                if (mSelectedMenuItem==item)
                    {drawableIcon.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);}
                else
                    {drawableIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);}
                mSelectedMenuItem.setIcon(drawableIcon);
            }
            */
            loaderManager.restartLoader(MOVIEDB_SEARCH_LOADER_ID, queryBundle, MainActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showActiveMenuItem(Menu pMenu, String menuActivated) {
        MenuItem aMenuItem=pMenu.findItem(R.id.mostPopular);
        //aMenuItem = pMenu.findItem(R.id.topRated);
        //aMenuItem.setTitle(getString(R.string.topRated_menu));
        //aMenuItem = pMenu.findItem(R.id.mostPopular);
        //aMenuItem.setTitle(getString(R.string.popular_menu));
        if (menuActivated.equals(getString(R.string.popular_menu)))
            aMenuItem = pMenu.findItem(R.id.mostPopular);
        if (menuActivated.equals(getString(R.string.topRated_menu)))
            aMenuItem = pMenu.findItem(R.id.topRated);
        if (menuActivated.equals(getString(R.string.favorites_menu)))
            aMenuItem = pMenu.findItem(R.id.favMenuItem);

        for (int i=0;i<=2;i++) {
            mSelectedMenuItem = mMenu.getItem(i);
            Drawable drawableIcon = mSelectedMenuItem.getIcon();
            if (drawableIcon != null) {
                drawableIcon.mutate();
                if (mSelectedMenuItem == aMenuItem) {
                    drawableIcon.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                } else {
                    drawableIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                }
                mSelectedMenuItem.setIcon(drawableIcon);
            }
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString(MENU_FILTER_TAG, menuActivated).apply();
    }

    /*
    private void showActiveMenuItem(Menu pMenu, String menuActivated) {
        MenuItem aMenuItem;
        aMenuItem = pMenu.findItem(R.id.topRated);
        aMenuItem.setTitle(getString(R.string.topRated_menu));
        aMenuItem = pMenu.findItem(R.id.mostPopular);
        aMenuItem.setTitle(getString(R.string.popular_menu));
        if (menuActivated.equals(getString(R.string.popular_menu)))
            aMenuItem = pMenu.findItem(R.id.mostPopular);
         else
            aMenuItem = pMenu.findItem(R.id.topRated);

        aMenuItem.setTitle("("+menuActivated + ")");
        //doesn't works on mobile, Does on emulator: MenuItem.setTitle(Html.fromHtml("<u>"+ menuActivated +"</u>"));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString(MENU_FILTER_TAG, menuActivated).apply();
    }
    */


}

