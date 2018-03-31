package com.udacity.PopularMovies.utils;

import android.net.Uri;
import android.util.Log;

import com.udacity.PopularMovies.BuildConfig;
import com.udacity.PopularMovies.model.MovieItem;
import com.udacity.PopularMovies.model.ReviewItem;
import com.udacity.PopularMovies.model.TrailerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class JsonUtils {
    private static final String TAG = "JSonUtils";

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public final static String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie/";
    public final static String POSTER_BASE_URL  = "http://image.tmdb.org/t/p/";
    public final static String YOUTUBE_TN_URL   = "http://img.youtube.com/vi/";

    public final static String W185 ="w185";
    public final static String W500 ="w500";

    final static String PARAM_QUERY = "api_key";
    final static String API_KEY = BuildConfig.MOVIEDB_API_KEY;
    /**
     * Builds the URL used to query MovieDB.
     *
     * @param movieDbQuery The keyword that will be queried for.
     * @return The URL to use to query the weather server.
     */
    public static URL buildUrl(String movieDbQuery) {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendEncodedPath(movieDbQuery)
                .appendQueryParameter(PARAM_QUERY, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static MovieItem parseSingleMovieJson(String json) {
        try {
            JSONObject aMovieItem        = new JSONObject(json);
            //JSONArray arrayJsonRoot  = myJson.getJSONArray("results");
            //int num_movies=arrayJsonRoot.length();
            MovieItem movie = new MovieItem();
            int id                    = getJsonInt(aMovieItem         ,MovieItem.id_Json);
            int vote_count            = getJsonInt(aMovieItem         ,MovieItem.vote_count_Json);
            float vote_average        = getJsonFloat(aMovieItem       ,MovieItem.vote_average_Json);
            boolean video             = getJsonBoolean(aMovieItem     ,MovieItem.video_Json);
            int popularity            = getJsonInt(aMovieItem         ,MovieItem.popularity_Json);
            String poster_path        = getJsonString(aMovieItem      ,MovieItem.poster_path_Json);
            String original_language  = getJsonString(aMovieItem      ,MovieItem.original_language_Json);
            String original_title     = getJsonString(aMovieItem      ,MovieItem.original_title_Json);
            //List<Integer> genre_ids   = getJsonIntegerList(aMovieItem ,MovieItem.genre_ids_Json);
            String backdrop_path      = getJsonString(aMovieItem      ,MovieItem.backdrop_path_Json);
            boolean adult             = getJsonBoolean(aMovieItem     ,MovieItem.adult_Json);
            String overview           = getJsonString(aMovieItem      ,MovieItem.overview_Json);
            Date release_date         = getJsonDate(aMovieItem        ,MovieItem.release_date_Json);
            movie = new MovieItem(id,vote_count,vote_average,video,popularity,poster_path,original_language,original_title,null,
                    backdrop_path,adult,overview,release_date);
            return movie;
        }
        catch (JSONException e) {
            Log.d(TAG,"Couldn't parse Json Movies Object:"+json);
            //e.printStackTrace();
            return null;
        }
    }


    public static MovieItem[] parseMoviesJson(String json) {
        try {
            JSONObject myJson        = new JSONObject(json);
            JSONArray arrayJsonRoot  = myJson.getJSONArray("results");
            int num_movies=arrayJsonRoot.length();

            MovieItem[] movies = new MovieItem[num_movies];
            for (int i=0;i<num_movies;i++) {
              JSONObject aMovieItem     = arrayJsonRoot.getJSONObject(i);
              int id                    = getJsonInt(aMovieItem         ,MovieItem.id_Json);
              int vote_count            = getJsonInt(aMovieItem         ,MovieItem.vote_count_Json);
              float vote_average        = getJsonFloat(aMovieItem       ,MovieItem.vote_average_Json);
              boolean video             = getJsonBoolean(aMovieItem     ,MovieItem.video_Json);
              int popularity            = getJsonInt(aMovieItem         ,MovieItem.popularity_Json);
              String poster_path        = getJsonString(aMovieItem      ,MovieItem.poster_path_Json);
              String original_language  = getJsonString(aMovieItem      ,MovieItem.original_language_Json);
              String original_title     = getJsonString(aMovieItem      ,MovieItem.original_title_Json);
              List<Integer> genre_ids   = getJsonIntegerList(aMovieItem ,MovieItem.genre_ids_Json);
              String backdrop_path      = getJsonString(aMovieItem      ,MovieItem.backdrop_path_Json);
              boolean adult             = getJsonBoolean(aMovieItem     ,MovieItem.adult_Json);
              String overview           = getJsonString(aMovieItem      ,MovieItem.overview_Json);
              Date release_date         = getJsonDate(aMovieItem        ,MovieItem.release_date_Json);
              movies[i] = new MovieItem(id,vote_count,vote_average,video,popularity,poster_path,original_language,original_title,genre_ids,
              backdrop_path,adult,overview,release_date);
            }
            return movies;
        }
        catch (JSONException e) {
            Log.d(TAG,"Couldn't parse Json Movies Object:"+json);
            //e.printStackTrace();
            return null;
        }
    }


    public static ReviewItem[] parseReviewsJson(String json) {
        try {
            JSONObject myJson        = new JSONObject(json);
            JSONArray arrayJsonRoot  = myJson.getJSONArray("results");
            int num_records=arrayJsonRoot.length();

            ReviewItem[] reviews = new ReviewItem[num_records];
            for (int i=0;i<num_records;i++) {
                JSONObject aReviewItem    = arrayJsonRoot.getJSONObject(i);
                String id                 = getJsonString(aReviewItem  ,ReviewItem.id_Json);
                String author             = getJsonString(aReviewItem  ,ReviewItem.author_Json);
                String content            = getJsonString(aReviewItem  ,ReviewItem.content_Json);
                String url                = getJsonString(aReviewItem  ,ReviewItem.url_Json);
                reviews[i] = new ReviewItem(id,author,content,url);
            }
            return reviews;
        }
        catch (JSONException e) {
            Log.d(TAG,"Couldn't parse Json Reviews Object:"+json);
            //e.printStackTrace();
            return null;
        }
    }

    public static TrailerItem[] parseTrailersJson(String json) {
        try {

            JSONObject myJson        = new JSONObject(json);
            JSONArray arrayJsonRoot  = myJson.getJSONArray("results");
            int num_records=arrayJsonRoot.length();

            TrailerItem[] trailers = new TrailerItem[num_records];
            for (int i=0;i<num_records;i++) {
                JSONObject aTrailerItem     = arrayJsonRoot.getJSONObject(i);
                String id              = getJsonString(aTrailerItem  ,TrailerItem.id_Json);
                String iso6391         = getJsonString(aTrailerItem  ,TrailerItem.iso6391_Json);
                String iso31661        = getJsonString(aTrailerItem  ,TrailerItem.iso31661_Json);
                String key             = getJsonString(aTrailerItem  ,TrailerItem.key_Json);
                String name            = getJsonString(aTrailerItem  ,TrailerItem.name_Json);
                String site            = getJsonString(aTrailerItem  ,TrailerItem.site_Json);
                int    size            = getJsonInt(aTrailerItem     ,TrailerItem.size_Json);
                String type            = getJsonString(aTrailerItem  ,TrailerItem.type_Json);

                trailers[i] = new TrailerItem(id,iso6391,iso31661,key,name,site,size,type);
            }
            return trailers;
        }
        catch (JSONException e) {
            Log.d(TAG,"Couldn't parse Json Reviews Object:"+json);
            //e.printStackTrace();
            return null;
        }
    }


    public static String getJsonString(JSONObject pJson,String propertyName) throws JSONException  {
        return pJson.getString(propertyName);
    }

    public static int getJsonInt(JSONObject pJson,String propertyName) throws JSONException  {
        return pJson.getInt(propertyName);
    }

    public static boolean getJsonBoolean(JSONObject pJson,String propertyName) throws JSONException  {
        return pJson.getBoolean(propertyName);
    }

    public static float getJsonFloat(JSONObject pJson, String propertyName) throws JSONException  {
        return Float.valueOf(pJson.getString(propertyName));
    }


    public static List<Integer> getJsonIntegerList(JSONObject pJson, String propertyName) throws JSONException  {
        List<Integer> aList = new ArrayList<Integer>();
        JSONArray array = pJson.getJSONArray(propertyName);
            for (int i = 0; i < array.length(); i++) {
                aList.add(array.getInt(i));
        }
        return aList;
    }

    public static Date getJsonDate(JSONObject pJson,String propertyName) throws JSONException  {

        String release_date_str   = getJsonString(pJson      ,propertyName);
        SimpleDateFormat sdf      = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date release_date = null;
        try {
            release_date = sdf.parse(release_date_str);
        }
        catch (ParseException e) {
            throw new JSONException(e.toString());
        }
        return release_date;
    }

    public static String makeThumbnailURL(String key) {
        return YOUTUBE_TN_URL.concat(key).concat("/hqdefault.jpg");
    }
}
