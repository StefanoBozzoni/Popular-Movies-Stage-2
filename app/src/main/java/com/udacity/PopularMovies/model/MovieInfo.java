package com.udacity.PopularMovies.model;


import android.os.Parcel;
import android.os.Parcelable;

public class MovieInfo implements Parcelable {

    private String movieId;
    private ReviewItem[]  reviews;
    private TrailerItem[] trailers;
    private MovieItem movie;


    public MovieInfo(String movieId,ReviewItem[] reviews, TrailerItem[] trailers, MovieItem aMovie) {
        this.movieId  = movieId;
        this.reviews  = reviews;
        this.trailers = trailers;
        this.movie    = aMovie;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        movieId = movieId;
    }


    public ReviewItem[] getReviews() {
        return reviews;
    }

    public void setReviews(ReviewItem[] reviews) {
        reviews = reviews;
    }


    public TrailerItem[] getTrailers() {
        return trailers;
    }

    public void setTrailers(TrailerItem[] trailers) {
        trailers = trailers;
    }


    public MovieItem getMovie() {
        return movie;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.movieId);
        dest.writeTypedArray(this.reviews, flags);
        dest.writeTypedArray(this.trailers, flags);
    }

    protected MovieInfo(Parcel in) {
        this.movieId = in.readString();
        this.reviews = in.createTypedArray(ReviewItem.CREATOR);
        this.trailers = in.createTypedArray(TrailerItem.CREATOR);
    }

    public static final Parcelable.Creator<MovieInfo> CREATOR = new Parcelable.Creator<MovieInfo>() {
        @Override
        public MovieInfo createFromParcel(Parcel source) {
            return new MovieInfo(source);
        }

        @Override
        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };
}
