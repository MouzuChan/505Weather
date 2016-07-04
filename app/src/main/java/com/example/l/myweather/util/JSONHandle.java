package com.example.l.myweather.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.l.myweather.database.CityDataBase;

import org.json.JSONObject;

/**
 * Created by L on 2016-06-29.
 */
public class JSONHandle {

    private JSONObject jsonObject;

    private String[] now_layout_strings;
    private String[] forecast_day_strings;
    private int[] forecast_high_temp;
    private int[] forecast_low_temp;
    private String[] aqi_Strings;
    private String[] widget_strings;
    private String[] index_strings;
    private String[] widget2x1_strings;
    private String[] alarm_strings;
    private String[] sun_rise_down_time;

    private String aqi;
    private String aqi_quality;


    private JSONObject object;

   /* private String[] weather_key = new String[]{"city_name","city_id","now_weather","now_temp","aqi","aqi_quality",
            "day0_weather","day1_weather","day2_weather","day3_weather","day4_weather","day5_weather","day6_weather","day7_weather",
            "day8_weather","day9_weather","day10_weather","day11_weather","day12_weather","day13_weather","night0_weather","night1_weather",
            "night2_weather","night3_weather","night4_weather","night5_weather","night6_weather","night7_weather","night8_weather",
            "night9_weather","night10_weather","night11_weather","night12_weather","night13_weather","day0_temp",
            "day1_temp","day2_temp","day3_temp","day4_temp","day5_temp","day6_temp","day7_temp","day8_temp","day9_temp","day10_temp","day11_temp",
            "day12_temp","day13_temp","night0_temp","night1_temp","night2_temp","night3_temp","night4_temp","night5_temp","night6_temp","night7_temp",
            "night8_temp","night9_temp","night10_temp","night11_temp","night12_temp","night13_temp","pm25","pm10","co","mp","so2","o3","no2",
            "aqi_suggest","up_time","body_temp","wind","humidity","sun_rise","sun_set","index0","index1","index2","index3","index4","index5","index6",
            "index7","index8"}; */


    public JSONHandle(JSONObject jsonObject){
        this.jsonObject = jsonObject;
        sun_rise_down_time = new String[5];
        now_layout_strings =  new String[4];
        aqi_Strings = new String[7];
        index_strings = new String[9];
        forecast_day_strings = new String[7];
        forecast_high_temp = new int[7];
        forecast_low_temp = new int[7];
        widget_strings = new String[15];
        widget2x1_strings = new String[7];
        alarm_strings = new String[4];
    }

    public String[] getNow_layout_strings() {
        try{
            now_layout_strings[0] = jsonObject.getJSONArray("forecast").getJSONObject(1).getString("weather");
        } catch (Exception e){
            e.printStackTrace();
        }


        return now_layout_strings;
    }
}
