package com.example.l.myweather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by L on 2015/10/10.
 */
public class CityDataBase extends SQLiteOpenHelper {

    private static String CREATE_CITY = "create table city(" + " _id integer primary key autoincrement, " + " city text, " + " city_id text)";

    public CityDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
