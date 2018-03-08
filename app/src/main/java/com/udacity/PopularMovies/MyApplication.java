package com.udacity.PopularMovies;

import android.app.Application;

public class MyApplication extends Application {
    private boolean isJustStarted;

    public boolean isJustStarted() {
        return isJustStarted;
    }

    public void setJustStarted(boolean justStarted) {
        isJustStarted = justStarted;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isJustStarted=true;
    }

}
