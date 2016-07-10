package com.example.l.myweather.settings;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.l.myweather.base.MyApplication;

/**
 * Created by L on 2016-04-28.
 */
public class AppInfoDataBase extends SQLiteOpenHelper {

    private static AppInfoDataBase appInfoDataBase;

    private String CREATE_APP_INFO = "create table app_info(" + " _id integer primary key autoincrement, " + " app_name text, " + " package_name text)";

    public AppInfoDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_APP_INFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static AppInfoDataBase getInstance(){
        if (appInfoDataBase == null){
            appInfoDataBase = new AppInfoDataBase(MyApplication.getContext(),"APP_INFO",null,1);
        }
        return appInfoDataBase;
    }
}
