package com.example.android.popularmovies;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by safwanx on 11/15/16.
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
