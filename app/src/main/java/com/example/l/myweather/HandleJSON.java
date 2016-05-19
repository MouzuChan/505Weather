package com.example.l.myweather;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by L on 2016-03-20.
 */
public class HandleJSON {

    private String[] now_layout_strings;
    private String[] forecast_day_strings;
    private int[] forecast_high_temp;
    private int[] forecast_low_temp;
    private String[] aqi_Strings;
    private String[] widget_strings;
    private String[] index_strings;
    private String[] widget2x1_strings;
    private String[] alarm_strings;
    private int aqi;
    private String aqiQuality;
    private String[] sun_rise_down_time;

    private JSONObject object;
    private JSONObject aqiObject;
    public HandleJSON(JSONObject jsonObject){

        sun_rise_down_time = new String[5];
        now_layout_strings =  new String[4];
        aqi_Strings = new String[7];
        index_strings = new String[9];
        forecast_day_strings = new String[7];
        forecast_high_temp = new int[7];
        forecast_low_temp = new int[7];
        widget_strings = new String[14];
        widget2x1_strings = new String[7];
        alarm_strings = new String[4];
        try {
           /* if (jsonObject.getString("code").equals("200")){
                this.object = jsonObject.getJSONArray("value").getJSONObject(0);
                if (object.has("pm25")){
                    aqiObject = object.getJSONObject("pm25");
                }
            }*/
            this.object = jsonObject;
            if (object.has("pm25")){
                aqiObject = object.getJSONObject("pm25");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String[] getNow_layout_strings() {
        try {
            if (object != null){
                if (object.has("realtime")){
                    JSONObject realtime= object.getJSONObject("realtime");
                    now_layout_strings[0] = realtime.getString("weather");
                    if (aqiObject != null){
                        now_layout_strings[1] = aqiObject.getString("aqi") + " " + aqiObject.getString("quality") ;
                    }
                    now_layout_strings[2] = realtime.getString("temp") + "°";
                    //now_layout_strings[3] = "体感：" + realtime.getString("sendibleTemp") + "°" + "  |  " + "湿度：" + realtime.getString("sD") + "%";
                    String time = realtime.getString("time");
                    now_layout_strings[3] = time.substring(time.length() - 8,time.length() - 3) + "发布";
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return now_layout_strings;
    }

    public void handleForecastData(){
        JSONObject forecastObject;
        try {
            if (object.has("weathers")){
                JSONArray weathers = object.getJSONArray("weathers");
                    for (int i = 0; i < weathers.length(); i++){
                        forecastObject = weathers.getJSONObject(i);
                        forecast_day_strings[i] = forecastObject.getString("weather");
                        forecast_low_temp[i] = Integer.valueOf(forecastObject.getString("temp_night_c"));
                        forecast_high_temp[i] = Integer.valueOf(forecastObject.getString("temp_day_c"));
                    }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String[] getIndex_strings() {
        try {
            if (object.has("indexes")){
                JSONArray jsonArray = object.getJSONArray("indexes");
                JSONObject object = jsonArray.getJSONObject(24);
                index_strings[0] = object.getString("level");
                object = jsonArray.getJSONObject(23);
                index_strings[1] = object.getString("level");
                object = jsonArray.getJSONObject(19);
                index_strings[2] = object.getString("level");
                object = jsonArray.getJSONObject(5);
                index_strings[3] = object.getString("level");
                object = jsonArray.getJSONObject(3);
                index_strings[4] = object.getString("level");
                object = jsonArray.getJSONObject(15);
                index_strings[5] = object.getString("level");
                object = jsonArray.getJSONObject(1);
                index_strings[6] = object.getString("level");
                object = jsonArray.getJSONObject(7);
                index_strings[7] = object.getString("level");
                object = jsonArray.getJSONObject(9);
                index_strings[8] = object.getString("level");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return index_strings;
    }

    public String[] getAqi_Strings() {
        try {
            if (object.has("pm25")){
                JSONObject evn = object.getJSONObject("pm25");
                aqi = Integer.valueOf(evn.getString("aqi"));
                aqiQuality = evn.getString("quality");
                aqi_Strings[0] = evn.getString("pm25");
                aqi_Strings[1] = evn.getString("pm10");
                aqi_Strings[2] = evn.getString("so2");
                aqi_Strings[3] = evn.getString("co");
                aqi_Strings[4] = evn.getString("no2");
                aqi_Strings[5] = evn.getString("o3");
                aqi_Strings[6] = "空气质量好于 " + evn.getString("cityrank") + "% 的城市";
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return aqi_Strings;
    }

    public int[] getForecast_low_temp() {
        return forecast_low_temp;
    }

    public int[] getForecast_high_temp() {
        return forecast_high_temp;
    }

    public String[] getForecast_day_strings() {
        return forecast_day_strings;
    }

    public String[] getAlarm_strings() {
        try {
            if (object.has("alarms")){
                JSONArray alarms = object.getJSONArray("alarms");
                if (alarms.length() > 0){
                    JSONObject alarm = alarms.getJSONObject(0);
                    alarm_strings[0] = alarm.getString("alarmLevelNoDesc") + alarm.getString("alarmTypeDesc");
                    alarm_strings[1] = alarm_strings[0];
                    alarm_strings[2] = alarm.getString("alarmContent");
                }


            }
        } catch (Exception e){
                e.printStackTrace();
        }
        return alarm_strings;
    }

    public int getAqi() {
        return aqi;
    }

    public String getAqiQuality() {
        return aqiQuality;
    }

    public String[] getSun_rise_down_time() {
        try {
            if (object.has("weathers")){
                JSONArray weathers = object.getJSONArray("weathers");
                sun_rise_down_time[0] = weathers.getJSONObject(0).getString("sun_rise_time");
                sun_rise_down_time[1] = weathers.getJSONObject(0).getString("sun_down_time");
            }

            if (object.has("realtime")){
                JSONObject realtime= object.getJSONObject("realtime");
                sun_rise_down_time[2] = "体感：" + realtime.getString("sendibleTemp") + "℃";
                sun_rise_down_time[3] = realtime.getString("wD") + " " + realtime.getString("wS");
                sun_rise_down_time[4] = "湿度：" + realtime.getString("sD") + "%";
            }

        } catch (Exception e){
            e.printStackTrace();
        }



        return sun_rise_down_time;
    }

    public String[] getWidget2x1_strings() {
        try {
            JSONObject realtime = object.getJSONObject("realtime");
            widget2x1_strings[0] = realtime.getString("temp") + "°";
            widget2x1_strings[1] = realtime.getString("weather");
            JSONObject pm25 = object.getJSONObject("pm25");
            widget2x1_strings[2] = pm25.getString("aqi");
            String time = realtime.getString("time");
            widget2x1_strings[3] = time.substring(time.length() - 8,time.length() - 3) + " 发布";
            JSONArray weathers = object.getJSONArray("weathers");
            JSONObject forecastObject = weathers.getJSONObject(0);
            //forecast_low_temp[i] = Integer.valueOf(forecastObject.getString("temp_night_c"));
            //forecast_high_temp[i] = Integer.valueOf(forecastObject.getString("temp_day_c"));
            widget2x1_strings[4] = forecastObject.getString("temp_night_c") + "°";
            widget2x1_strings[5] = forecastObject.getString("temp_day_c") + "°";
            widget2x1_strings[6] = pm25.getString("quality");
        } catch (Exception e){
            e.printStackTrace();
        }
        return widget2x1_strings;
    }

    public String[] getWidget_strings() {
        try {
            JSONObject realtime = object.getJSONObject("realtime");
            widget_strings[0] = realtime.getString("weather");
            widget_strings[12] = realtime.getString("temp") + "°";
            JSONArray weathers = object.getJSONArray("weathers");
            for (int i = 0; i< 5; i++){
                JSONObject weather = weathers.getJSONObject(i);
                widget_strings[i + 2] = weather.getString("weather");
                widget_strings[i + 7] = weather.getString("temp_night_c") + "/" + weather.getString("temp_day_c") + "°";
            }
            JSONObject evn = object.getJSONObject("pm25");
            widget_strings[1] = "AQI：" + evn.getString("aqi") + " " + evn.getString("quality");
            String time = realtime.getString("time");
            widget_strings[13] = time.substring(time.length() - 8,time.length() - 3) + " 发布";
        } catch (Exception e){
            e.printStackTrace();
        }

        return widget_strings;
    }

}
