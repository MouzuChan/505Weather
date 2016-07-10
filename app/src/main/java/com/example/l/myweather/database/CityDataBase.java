package com.example.l.myweather.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.l.myweather.base.MyApplication;

/**
 * Created by L on 2015/10/10.
 */
public class CityDataBase extends SQLiteOpenHelper {

    private static CityDataBase mCityDataBase;


    private static String CREATE_CITY = "create table if not exists city(" + " _id integer primary key autoincrement, " + " city text, " + " city_id text)";

    /*
    private static String CREATE_WEATHER = "create table if not exists weather(" +
            "city_name text, " +
            "city_id text," +
            "day0_weather text," +
            "day1_weather text," +
            "day2_weather text," +
            "day3_weather text," +
            "day4_weather text," +
            "day5_weather text," +
            "day6_weather text," +
            "day7_weather text," +
            "day8_weather text," +
            "day9_weather text," +
            "day10_weather text," +
            "day11_weather text," +
            "day12_weather text," +
            "day13_weather text," +
            "day14_weather text," +
            "night0_weather text," +
            "night1_weather text," +
            "night2_weather text," +
            "night3_weather text," +
            "night4_weather text," +
            "night5_weather text," +
            "night6_weather text," +
            "night7_weather text," +
            "night8_weather text," +
            "night9_weather text," +
            "night10_weather text," +
            "night11_weather text," +
            "night12_weather text," +
            "night13_weather text," +
            "night14_weather text," +
            "day0_temp text," +
            "day1_temp text," +
            "day2_temp text," +
            "day3_temp text," +
            "day4_temp text," +
            "day5_temp text," +
            "day6_temp text," +
            "day7_temp text," +
            "day8_temp text," +
            "day9_temp text," +
            "day10_temp text," +
            "day11_temp text," +
            "day12_temp text," +
            "day13_temp text," +
            "day14_temp text," +
            "night0_temp text," +
            "night1_temp text," +
            "night2_temp text," +
            "night3_temp text," +
            "night4_temp text," +
            "night5_temp text," +
            "night6_temp text," +
            "night7_temp text," +
            "night8_temp text," +
            "night9_temp text," +
            "night10_temp text," +
            "night11_temp text," +
            "night12_temp text," +
            "night13_temp text," +
            "night14_temp text," +
            "aqi text," +
            "aqi_quality text," +
            "pm25 text," +
            "pm10 text," +
            "co text," +
            "mp text," +
            "so2 text," +
            "o3 text," +
            "no2 text," +
            "aqi_suggest text," +
            "up_time text," +
            "body_temp text," +
            "wind text," +
            "humidity text," +
            "sun_rise text," +
            "sun_set text," +
            "index0 text," +
            "index1 text," +
            "index2 text," +
            "index3 text," +
            "index4 text," +
            "index5 text," +
            "index6 text," +
            "index7 text," +
            "index8 text)";
    */



    public CityDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*switch (newVersion){
            case 2:
                db.beginTransaction();
                try {
                    db.execSQL(CREATE_WEATHER);
                    db.setTransactionSuccessful();
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                }
                break;
        }*/
    }

    public static CityDataBase getInstance(){
        if (mCityDataBase == null){
            mCityDataBase = new CityDataBase(MyApplication.getContext(),"CITY_LIST",null,1);
        }
        return mCityDataBase;
    }
}
