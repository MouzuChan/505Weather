package com.example.l.myweather.util;

import com.example.l.myweather.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by L on 2016-05-13.
 */
public class WeatherToCode {
    private Map<String,Integer> map_day = new HashMap<>();

    private  Map<String,Integer> map_night = new HashMap<>();

    private Map<String,Integer> map_day_small = new HashMap<>();

    private Map<String,Integer> map_night_small = new HashMap<>();

    public static WeatherToCode weatherToCode;

    public WeatherToCode(){
        map_day.put("晴", R.drawable.sunny);
        map_day.put("多云",R.drawable.cloudy2);
        map_day.put("阴",R.drawable.cloudy5);
        map_day.put("阵雨",R.drawable.shower1);
        map_day.put("雷阵雨",R.drawable.tstorm1);
        map_day.put("雨夹雪",R.drawable.sleet);
        map_day.put("小雨",R.drawable.shower1);
        map_day.put("小到中雨",R.drawable.shower1);
        map_day.put("中雨",R.drawable.shower2);
        map_day.put("中到大雨",R.drawable.shower2);
        map_day.put("大雨",R.drawable.light_rain);
        map_day.put("大到暴雨",R.drawable.shower3);
        map_day.put("暴雨",R.drawable.shower3);
        map_day.put("大暴雨",R.drawable.shower3);
        map_day.put("特大暴雨",R.drawable.shower3);
        map_day.put("小雪",R.drawable.snow1);
        map_day.put("中雪",R.drawable.snow2);
        map_day.put("大雪",R.drawable.snow3);
        map_day.put("暴雪",R.drawable.snow5);
        map_day.put("冻雨",R.drawable.light_rain);
        map_day.put("冰雹",R.drawable.hail);
        map_day.put("薄雾",R.drawable.mist);
        map_day.put("雾",R.drawable.fog);
        map_day.put("霾",R.drawable.fog);
        map_day.put("浮尘",R.drawable.fog);
        map_day.put("扬沙",R.drawable.fog);
        map_day.put("沙尘暴",R.drawable.fog);
        map_day.put("强沙尘暴",R.drawable.fog);


        map_night.put("晴",R.drawable.sunny_night);
        map_night.put("多云",R.drawable.cloudy2_night);
        map_night.put("阴",R.drawable.cloudy4_night);
        map_night.put("阵雨",R.drawable.shower1_night);
        map_night.put("雷阵雨",R.drawable.tstorm1_night);
        map_night.put("雨夹雪",R.drawable.sleet);
        map_night.put("小雨",R.drawable.shower1_night);
        map_night.put("小到中雨",R.drawable.shower1_night);
        map_night.put("中雨",R.drawable.shower2_night);
        map_night.put("中到大雨",R.drawable.shower2_night);
        map_night.put("大雨",R.drawable.light_rain);
        map_night.put("大到暴雨",R.drawable.shower3);
        map_night.put("暴雨",R.drawable.shower3);
        map_night.put("大暴雨",R.drawable.shower3);
        map_night.put("特大暴雨",R.drawable.shower3);
        map_night.put("小雪",R.drawable.snow1_night);
        map_night.put("中雪",R.drawable.snow2_night);
        map_night.put("大雪",R.drawable.snow3_night);
        map_night.put("暴雪",R.drawable.snow5);
        map_night.put("冻雨",R.drawable.light_rain);
        map_night.put("冰雹",R.drawable.hail);
        map_night.put("薄雾",R.drawable.mist_night);
        map_night.put("雾",R.drawable.fog_night);
        map_night.put("霾",R.drawable.fog_night);
        map_night.put("浮尘",R.drawable.fog_night);
        map_night.put("扬沙",R.drawable.fog_night);
        map_night.put("沙尘暴",R.drawable.fog_night);
        map_night.put("强沙尘暴",R.drawable.fog_night);


        map_day_small.put("晴",R.drawable.sunny_day_small);
        map_day_small.put("多云",R.drawable.duoyun_day_small);
        map_day_small.put("阴",R.drawable.yin_small);
        map_day_small.put("阵雨",R.drawable.zhenyu_day_small);
        map_day_small.put("雷阵雨",R.drawable.leizhenyu);
        map_day_small.put("雨夹雪",R.drawable.yubingbao);
        map_day_small.put("小雨",R.drawable.xiaoyu);
        map_day_small.put("小到中雨",R.drawable.xiaoyu);
        map_day_small.put("中雨",R.drawable.dayu_small);
        map_day_small.put("中到大雨",R.drawable.dayu_small);
        map_day_small.put("大雨",R.drawable.dayu_small);
        map_day_small.put("大到暴雨",R.drawable.dayu_small);
        map_day_small.put("暴雨",R.drawable.baoyu_small);
        map_day_small.put("大暴雨",R.drawable.baoyu_small);
        map_day_small.put("特大暴雨",R.drawable.baoyu_small);
        map_day_small.put("小雪",R.drawable.xiaoxue_day_small);
        map_day_small.put("中雪",R.drawable.zhongxue_day_small);
        map_day_small.put("大雪",R.drawable.zhongxue_day_small);
        map_day_small.put("暴雪",R.drawable.daxue_small);
        map_day_small.put("冻雨",R.drawable.dayu_small);
        map_day_small.put("冰雹",R.drawable.hail_small);
        map_day_small.put("薄雾",R.drawable.fog_day_small);
        map_day_small.put("雾",R.drawable.fog_day_small);
        map_day_small.put("霾",R.drawable.fog_day_small);
        map_day_small.put("浮尘",R.drawable.fog_day_small);
        map_day_small.put("扬沙",R.drawable.fog_day_small);
        map_day_small.put("沙尘暴",R.drawable.fog_day_small);
        map_day_small.put("强沙尘暴",R.drawable.fog_day_small);


        map_night_small.put("晴",R.drawable.moon_small);
        map_night_small.put("多云",R.drawable.cloud_night_small);
        map_night_small.put("阴",R.drawable.cloud_night_small);
        map_night_small.put("阵雨",R.drawable.zhongyu_night);
        map_night_small.put("雷阵雨",R.drawable.leizhenyu);
        map_night_small.put("雨夹雪",R.drawable.yubingbao);
        map_night_small.put("小雨",R.drawable.xiaoyu);
        map_night_small.put("小到中雨",R.drawable.xiaoyu);
        map_night_small.put("中雨",R.drawable.dayu_small);
        map_night_small.put("中到大雨",R.drawable.dayu_small);
        map_night_small.put("大雨",R.drawable.dayu_small);
        map_night_small.put("大到暴雨",R.drawable.dayu_small);
        map_night_small.put("暴雨",R.drawable.baoyu_small);
        map_night_small.put("大暴雨",R.drawable.baoyu_small);
        map_night_small.put("特大暴雨",R.drawable.baoyu_small);
        map_night_small.put("小雪",R.drawable.xiaoxue_night_small);
        map_night_small.put("中雪",R.drawable.zhongxue_night_small);
        map_night_small.put("大雪",R.drawable.zhongxue_night_small);
        map_night_small.put("暴雪",R.drawable.daxue_small);
        map_night_small.put("冻雨",R.drawable.dayu_small);
        map_night_small.put("冰雹",R.drawable.hail_small);
        map_night_small.put("薄雾",R.drawable.fog_night_small);
        map_night_small.put("雾",R.drawable.fog_night_small);
        map_night_small.put("霾",R.drawable.fog_night_small);
        map_night_small.put("浮尘",R.drawable.fog_night_small);
        map_night_small.put("扬沙",R.drawable.fog_night_small);
        map_night_small.put("沙尘暴",R.drawable.fog_night_small);
        map_night_small.put("强沙尘暴",R.drawable.fog_night_small);
    }

    public int getDrawableId(String weather,int hour){
        int drawable_id = 0;
        if (hour > 18 || hour < 7){
            if (map_night.containsKey(weather)){
                drawable_id = map_night.get(weather);
            }
        } else {
            if (map_day.containsKey(weather)){
                drawable_id = map_day.get(weather);
            }
        }
        return drawable_id;
    }

    public int getDrawableSmallId(String weather,int hour){
        int drawable_id;
        if (hour > 18 || hour < 7){
            if (map_night_small.containsKey(weather)){
                drawable_id = map_night_small.get(weather);
            } else {
                drawable_id = 0;
            }

        } else {
            if (map_day_small.containsKey(weather)){
                drawable_id = map_day_small.get(weather);
            } else {
                drawable_id = 0;
            }

        }
        return drawable_id;
    }

    public static WeatherToCode newInstance() {
        if (weatherToCode == null){
            weatherToCode = new WeatherToCode();
        }
        return weatherToCode;
    }


}
