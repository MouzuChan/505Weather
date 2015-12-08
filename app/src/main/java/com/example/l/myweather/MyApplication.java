package com.example.l.myweather;

import android.app.Application;
import android.content.Context;

/**
 * Created by L on 2015/10/2.
 */
public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        context = getApplicationContext();
        super.onCreate();
    }

    public static Context getContext(){
        return context;
    }
}
