package com.example.l.myweather;

import org.json.JSONObject;

/**
 * Created by L on 2015/11/1.
 */
public class HandleJsonForWidget {
    private String city;
    private String aqi;
    private String temp;
    private String weather_txt;
    private String qlty;
    private String loc_time;
    private String weather_pic;
    public void handleJson(JSONObject jsonObject){
        try {
            if (jsonObject.has("showapi_res_body")){
                JSONObject all = jsonObject.getJSONObject("showapi_res_body");
                if (all.has("cityInfo")){
                    JSONObject cityInfo = all.getJSONObject("cityInfo");
                    if (cityInfo.has("c3")){
                        city = cityInfo.getString("c3");
                    }
                }
                if (all.has("now")){
                    JSONObject now = all.getJSONObject("now");
                    if (now.has("aqi")){
                        aqi = now.getString("aqi");
                    }
                    if (now.has("temperature")){
                        temp = now.getString("temperature");
                    }
                    if (now.has("weather")){
                        weather_txt = now.getString("weather");
                    }
                    if (now.has("aqiDetail")){
                        JSONObject aqiDetail = now.getJSONObject("aqiDetail");
                        if (aqiDetail.has("quality")){
                            qlty = aqiDetail.getString("quality");
                        }
                    }
                    if (now.has("temperature_time")){
                        loc_time = now.getString("temperature_time");
                    }
                    if (now.has("weather_pic")){
                        weather_pic = now.getString("weather_pic");
                    }

                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public String getWeather_pic() {
        return weather_pic;
    }

    public String getCity() {
        return city;
    }

    public String getAqi() {
        return aqi;
    }

    public String getLoc_time() {
        return loc_time;
    }

    public String getQlty() {
        return qlty;
    }

    public String getTemp() {
        return temp;
    }

    public String getWeather_txt() {
        return weather_txt;
    }
}
