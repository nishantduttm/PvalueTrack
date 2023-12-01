package com.example.votingapp.CrashHelper;

import android.app.Application;

public class ErrorHandler extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(getApplicationContext()));
    }}