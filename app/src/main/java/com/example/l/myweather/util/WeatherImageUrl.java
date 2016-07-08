package com.example.l.myweather.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by L on 2016-06-30.
 */
public class WeatherImageUrl {

    private Map<String,String> map_day = new HashMap<>();
    private Map<String,String> map_night = new HashMap<>();

    public WeatherImageUrl(){
        map_day.put("晴","http://static.etouch.cn/imgs/upload/1464156616.6833.jpg");
        map_day.put("多云","http://static.etouch.cn/imgs/upload/1464156680.4088.jpg");
        map_day.put("阴","http://static.etouch.cn/imgs/upload/1464156697.6173.jpg");
        map_day.put("阵雨","http://static.etouch.cn/imgs/upload/1464156721.5622.jpg");
        map_day.put("雷阵雨","http://static.etouch.cn/imgs/upload/1464156742.4928.jpg");
        map_day.put("雨夹雪","http://static.etouch.cn/imgs/upload/1446801371.5782.jpg");
        map_day.put("小雨","http://static.etouch.cn/imgs/upload/1464156806.3523.jpg");
        map_day.put("小到中雨","http://static.etouch.cn/imgs/upload/1464156835.9677.jpg");
        map_day.put("中雨","http://static.etouch.cn/imgs/upload/1464156835.9677.jpg");
        map_day.put("中到大雨","http://static.etouch.cn/imgs/upload/1464156872.1353.jpg");
        map_day.put("大雨","http://static.etouch.cn/imgs/upload/1464156872.1353.jpg");
        map_day.put("大到暴雨","http://static.etouch.cn/imgs/upload/1464156893.7236.jpg");
        map_day.put("暴雨","http://static.etouch.cn/imgs/upload/1464156893.7236.jpg");
        map_day.put("大暴雨","http://static.etouch.cn/imgs/upload/1464156893.7236.jpg");
        map_day.put("特大暴雨","http://static.etouch.cn/imgs/upload/1464156893.7236.jpg");
        map_day.put("小雪","http://static.etouch.cn/imgs/upload/1454119940.3162.jpg");
        map_day.put("中雪","http://static.etouch.cn/imgs/upload/1454119940.3162.jpg");
        map_day.put("大雪","http://static.etouch.cn/imgs/upload/1454119940.3162.jpg");
        map_day.put("暴雪","http://static.etouch.cn/imgs/upload/1454119940.3162.jpg");
        map_day.put("冻雨","http://static.etouch.cn/imgs/upload/1464156872.1353.jpg");
        map_day.put("冰雹","http://static.etouch.cn/imgs/upload/1464156806.3523.jpg");
        map_day.put("薄雾","http://static.etouch.cn/imgs/upload/1464157261.3416.jpg");
        map_day.put("雾","http://static.etouch.cn/imgs/upload/1464157261.3416.jpg");
        map_day.put("霾","http://static.etouch.cn/imgs/upload/1442471142.3815.jpg");
        map_day.put("浮尘","http://static.etouch.cn/imgs/upload/1442471142.3815.jpg");
        map_day.put("扬沙","http://static.etouch.cn/imgs/upload/1442471142.3815.jpg");
        map_day.put("沙尘暴","http://static.etouch.cn/imgs/upload/1442471142.3815.jpg");
        map_day.put("强沙尘暴","http://static.etouch.cn/imgs/upload/1442471142.3815.jpg");


        map_night.put("晴","http://static.etouch.cn/imgs/upload/1464156624.5772.jpg");
        map_night.put("多云","http://static.etouch.cn/imgs/upload/1464156685.511.jpg");
        map_night.put("阴","http://static.etouch.cn/imgs/upload/1464156697.6173.jpg");
        map_night.put("阵雨","http://static.etouch.cn/imgs/upload/1464156721.5622.jpg");
        map_night.put("雷阵雨","http://static.etouch.cn/imgs/upload/1464156742.4928.jpg");
        map_night.put("雨夹雪","http://static.etouch.cn/imgs/upload/1446801371.5782.jpg");
        map_night.put("小雨","http://static.etouch.cn/imgs/upload/1464156806.3523.jpg");
        map_night.put("小到中雨","http://static.etouch.cn/imgs/upload/1464156835.9677.jpg");
        map_night.put("中雨","http://static.etouch.cn/imgs/upload/1464156835.9677.jpg");
        map_night.put("中到大雨","http://static.etouch.cn/imgs/upload/1464156872.1353.jpg");
        map_night.put("大雨","http://static.etouch.cn/imgs/upload/1464156872.1353.jpg");
        map_night.put("大到暴雨","http://static.etouch.cn/imgs/upload/1464156893.7236.jpg");
        map_night.put("暴雨","http://static.etouch.cn/imgs/upload/1464156893.7236.jpg");
        map_night.put("大暴雨","http://static.etouch.cn/imgs/upload/1464156893.7236.jpg");
        map_night.put("特大暴雨","http://static.etouch.cn/imgs/upload/1464156893.7236.jpg");
        map_night.put("小雪","http://static.etouch.cn/suishen/weather/nighticyrain.jpg");
        map_night.put("中雪","http://static.etouch.cn/suishen/weather/nighticyrain.jpg");
        map_night.put("大雪","http://static.etouch.cn/suishen/weather/nighticyrain.jpg");
        map_night.put("暴雪","http://static.etouch.cn/suishen/weather/nighticyrain.jpg");
        map_night.put("冻雨","http://static.etouch.cn/imgs/upload/1464156872.1353.jpg");
        map_night.put("冰雹","http://static.etouch.cn/imgs/upload/1464156806.3523.jpg");
        map_night.put("薄雾","http://static.etouch.cn/imgs/upload/1464157261.3416.jpg");
        map_night.put("雾","http://static.etouch.cn/imgs/upload/1464157261.3416.jpg");
        map_night.put("霾","http://static.etouch.cn/imgs/upload/1442472108.7831.jpg");
        map_night.put("浮尘","http://static.etouch.cn/imgs/upload/1442472108.7831.jpg");
        map_night.put("扬沙","http://static.etouch.cn/imgs/upload/1442472108.7831.jpg");
        map_night.put("沙尘暴","http://static.etouch.cn/imgs/upload/1442472108.7831.jpg");
        map_night.put("强沙尘暴","http://static.etouch.cn/imgs/upload/1442472108.7831.jpg");
    }

    public String get_url(String weather,int hour) {
        if (hour > 18 || hour < 7){
            if (map_night.containsKey(weather)){
                return map_night.get(weather);
            }
        } else {
            if (map_day.containsKey(weather)){
                return map_day.get(weather);
            }
        }
        return null;
    }

}
