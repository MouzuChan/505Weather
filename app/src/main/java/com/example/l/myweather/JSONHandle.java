package com.example.l.myweather;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by L on 2016-03-04.
 */
public class JSONHandle {
    private int aqi;
    private String aqi_quality;
    private String[] now_layout_strings = new String[7];
    //private int[] hour_layout_temp_ints;
    //private String[] hour_layout_time_strings;
    private String[] forecast_night_strings;
    private String[] forecast_day_strings;
    private int[] forecast_high_temp;
    private int[] forecast_low_temp;
    private String picUrl;
    private JSONObject jsonObject;
    private String[] aqi_Strings;
    private String[] widget_strings;

    private String[] index_strings;
    private String[] widget2x1_strings;

    private int status_code;
    private String[] alarm_strings;


    public JSONHandle(JSONObject object) {
        this.jsonObject = object;
        aqi_Strings = new String[7];
        index_strings = new String[9];
        forecast_day_strings = new String[6];
        forecast_night_strings = new String[6];
        forecast_high_temp = new int[6];
        forecast_low_temp = new int[6];
        //hour_layout_temp_ints = new int[24];
       //hour_layout_time_strings = new String[24];
        widget_strings = new String[14];
        widget2x1_strings = new String[4];
    }

    public void jsonHandle(int hour) {
        try {
            JSONObject observe = jsonObject.getJSONObject("observe");
            now_layout_strings[2] = observe.getString("temp") + "°";
            now_layout_strings[3] = "体感：" + observe.getString("tigan") + "°" + "  |  " + "湿度：" + observe.getString("shidu");

            if (jsonObject.has("evn")) {
                JSONObject evn = jsonObject.getJSONObject("evn");
                aqi = evn.getInt("aqi");
                aqi_quality = evn.getString("quality");
                now_layout_strings[1] = "空气指数：" + aqi + aqi_quality + "  |  " + observe.getString("wd") +
                observe.getString("wp");
                aqi_Strings[0] = evn.getString("pm25");
                aqi_Strings[1] = evn.getString("pm10");
                aqi_Strings[2] = evn.getString("so2");
                aqi_Strings[3] = evn.getString("co");
                aqi_Strings[4] = evn.getString("no2");
                aqi_Strings[5] = evn.getString("o3");
                aqi_Strings[6] = evn.getString("suggest");
            }
            if (jsonObject.has("forecast")) {
                JSONArray forecast = jsonObject.getJSONArray("forecast");
                for (int i = 0; i < forecast.length(); i++) {
                    JSONObject object = forecast.getJSONObject(i);
                    JSONObject day_object = object.getJSONObject("day");
                    JSONObject night_object = object.getJSONObject("night");
                    forecast_day_strings[i] = day_object.getString("wthr");
                    forecast_night_strings[i] = night_object.getString("wthr");
                    forecast_high_temp[i] = object.getInt("high");
                    forecast_low_temp[i] = object.getInt("low");
                    if (i == 1) {
                        if (hour > 18 || hour < 7){
                            if (night_object.has("bgPic")){
                                picUrl = night_object.getString("bgPic");
                            }
                        } else {
                            if (day_object.has("bgPic")){
                                picUrl = day_object.getString("bgPic");
                            }
                        }
                        now_layout_strings[5] = object.getString("sunrise");
                        now_layout_strings[6] = object.getString("sunset");
                    }
                }
                JSONObject today = forecast.getJSONObject(1);
                JSONObject day = today.getJSONObject("day");
                now_layout_strings[0] = day.getString("wthr");
            }
            /*JSONArray hourfc = jsonObject.getJSONArray("hourfc");
            for (int i = 0; i < hourfc.length(); i++) {
                JSONObject hour = hourfc.getJSONObject(i);
                hour_layout_temp_ints[i] = hour.getInt("wthr");
                String time = hour.getString("time");
                int length = time.length();
                hour_layout_time_strings[i] = time.substring(length - 4, length - 2) + ":" + time.substring(length - 2, length);
            }*/
            now_layout_strings[4] = jsonObject.getJSONObject("meta").getString("up_time") + "发布";

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public String[] getNow_layout_strings() {
        return now_layout_strings;
    }

  /*  public int[] getHour_layout_temp_ints() {
        return hour_layout_temp_ints;
    }

    public String[] getHour_layout_time_strings() {
        return hour_layout_time_strings;
    }*/

    public String[] getForecast_day_strings() {
        return forecast_day_strings;
    }

    public String[] getForecast_night_strings() {
        return forecast_night_strings;
    }

    public int[] getForecast_high_temp() {
        return forecast_high_temp;
    }

    public int[] getForecast_low_temp() {
        return forecast_low_temp;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public int getAqi() {
        return aqi;
    }

    public String getAqi_quality() {
        return aqi_quality;
    }

    public String[] getAqi_Strings() {
        return aqi_Strings;
    }

    public String[] getIndex_strings() {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("indexes");
            JSONObject object = jsonArray.getJSONObject(2);
            index_strings[0] = object.getString("value");
            object = jsonArray.getJSONObject(1);
            index_strings[1] = object.getString("value");
            object = jsonArray.getJSONObject(3);
            index_strings[2] = object.getString("value");
            object = jsonArray.getJSONObject(6);
            index_strings[3] = object.getString("value");
            object = jsonArray.getJSONObject(10);
            index_strings[4] = object.getString("value");
            object = jsonArray.getJSONObject(7);
            index_strings[5] = object.getString("value");
            object = jsonArray.getJSONObject(11);
            index_strings[6] = object.getString("value");
            object = jsonArray.getJSONObject(4);
            index_strings[7] = object.getString("value");
            object = jsonArray.getJSONObject(9);
            index_strings[8] = object.getString("value");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return index_strings;
    }

    public String[] getWidget_strings(int hour) {
        try {
            JSONArray forecast = jsonObject.getJSONArray("forecast");
            JSONObject observe = jsonObject.getJSONObject("observe");
            if (jsonObject.has("evn")){
                JSONObject evn = jsonObject.getJSONObject("evn");
                if (evn.has("aqi") && evn.has("quality")){
                    widget_strings[1] = "空气指数：" + evn.getString("aqi") + evn.getString("quality");
                }
            }

            JSONObject today = forecast.getJSONObject(1);
            if (hour > 18 || hour < 7) {
                widget_strings[0] = today.getJSONObject("night").getString("wthr");
            } else {
                widget_strings[0] = today.getJSONObject("day").getString("wthr");
            }
            widget_strings[12] = observe.getString("temp") + "°";

            widget_strings[13] = jsonObject.getJSONObject("meta").getString("up_time") + "发布";
            for (int i = 1; i < 6; i++) {
                JSONObject object = forecast.getJSONObject(i);
                widget_strings[i + 1] = object.getJSONObject("day").getString("wthr");
                widget_strings[i + 6] = object.getString("low") + "°/" + object.getString("high") + "°";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return widget_strings;
    }

    public String[] getWidget2x1_strings(int hour) {

        try {
            JSONArray forecast = jsonObject.getJSONArray("forecast");
            JSONObject observe = jsonObject.getJSONObject("observe");
            if (jsonObject.has("evn")){
                JSONObject evn = jsonObject.getJSONObject("evn");
                if (evn.has("aqi") && evn.has("quality")){
                    widget2x1_strings[2] = evn.getString("aqi") + " " + evn.getString("quality");
                } else {
                    widget2x1_strings[2] = "空气指数：--";
                }
            }else {
                widget2x1_strings[2] = "空气指数：--";
            }
            JSONObject today = forecast.getJSONObject(1);
            if (hour > 18 || hour < 7) {
                widget2x1_strings[1] = today.getJSONObject("night").getString("wthr");
            } else {
                widget2x1_strings[1] = today.getJSONObject("day").getString("wthr");
            }
            widget2x1_strings[0] = observe.getString("temp") + "°";

            widget2x1_strings[3] = jsonObject.getJSONObject("meta").getString("up_time") + "发布";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return widget2x1_strings;
    }

    public int getStatus_code() {
        try {
            status_code = jsonObject.getJSONObject("meta").getInt("status");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status_code;
    }

    public String[] getAlarm_strings() {
        if (alarm_strings == null) {
            alarm_strings = new String[4];
        }
        if (jsonObject.has("alarm")) {
            try {
                JSONObject alarm = jsonObject.getJSONObject("alarm");
                alarm_strings[0] = alarm.getString("type") + alarm.getString("degree") + "预警";
                alarm_strings[1] = alarm.getString("details");
                alarm_strings[2] = alarm.getString("desc");
                alarm_strings[3] = alarm.getString("icon");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return alarm_strings;
    }
}
