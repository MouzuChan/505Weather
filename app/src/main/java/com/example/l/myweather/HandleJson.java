package com.example.l.myweather;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by L on 2015/10/2.
 */
public class HandleJson {

    private String weather_pic;
    private String city;
    private String loc_time;
    private String aqi;
    private String weather_txt;
    private String temp;
    private String fengli;
    private String date;
    private String first_max_tmp;
    private String first_weather;
    private String second_weather;
    private String third_weather;
    private String four_weather;

    private String first_day_temp;
    private String second_day_temp;
    private String third_day_temp;
    private String four_day_temp;
    private String fifth_day_temp;
    private String sixth_day_temp;
    private String seventh_day_temp;
    private String qlty;

    private String[] dayWeatherPic;
    private String[] nightWeatherPic;

    private String qiya;
    private String shidu;
    private String richu_riluo;
    private String pm2_5;
    private String jiangshui;
    private String ziwaixian;


    private String first_night_weather_txt;
    private String second_night_weather_txt;
    private String third_night_weather_txt;
    private String four_night_weather_txt;

    private String first_night_temp;
    private String second_night_temp;
    private String third_night_temp;
    private String four_night_temp;
    private String fifth_night_temp;
    private String sixth_night_temp;
    private String seventh_night_temp;

    private String shushidu;
    private String ganmao;
    private String chuanyi;
    private String yundong;
    private String chuxing;
    private String xiche;
    private String fifth_night_weather_txt;
    private String sixth_night_weather_txt;
    private String seventh_night_weather_txt;

    private String fifth_day_weather_txt;
    private String sixth_day_weather_txt;
    private String seventh_day_weather_txt;
    private boolean b;
    private String err_code;
    private String err_msg;

    private String shushidu_content,ganmao_content,xiche_content,chuanyi_content,chuxing_content,yundong_content;





    public void handleJson(JSONObject jsonObject){
        Log.d("HandleJson",jsonObject.toString());
        try{
            dayWeatherPic = new String[7];
            nightWeatherPic = new String[7];
            b = false;
            err_code = jsonObject.getString("showapi_res_code");
            err_msg = jsonObject.getString("showapi_res_error");

            if (jsonObject.has("showapi_res_body")){
                JSONObject all = jsonObject.getJSONObject("showapi_res_body");
                b = true;
                if (all.has("f1")){
                    JSONObject f1 = all.getJSONObject("f1");
                    if (f1.has("day_air_temperature") && f1.has("night_air_temperature")){
                        first_max_tmp = f1.getString("day_air_temperature") + "°" + "/" + f1.getString("night_air_temperature") + "°";
                    }
                    if (f1.has("day_weather")){
                        first_weather = f1.getString("day_weather");
                    }
                    if (f1.has("day_air_temperature")){
                        first_day_temp= f1.getString("day_air_temperature");
                    }
                    if (f1.has("night_air_temperature")){
                        first_night_temp = f1.getString("night_air_temperature");
                    }
                    if (f1.has("night_weather")){
                        first_night_weather_txt = f1.getString("night_weather");
                    }
                    if (f1.has("day_weather_pic")){
                        String first_day_weather_pic = f1.getString("day_weather_pic");
                        dayWeatherPic[0] = first_day_weather_pic;
                    }
                    if (f1.has("night_weather_pic")){
                        String first_night_weather_pic = f1.getString("night_weather_pic");
                        nightWeatherPic[0] = first_night_weather_pic;
                    }

                    if (f1.has("sun_begin_end")){
                        richu_riluo = f1.getString("sun_begin_end");
                    }

                    if (f1.has("air_press")){
                        qiya = f1.getString("air_press");
                    }
                    if (f1.has("jiangshui")){
                        jiangshui = f1.getString("jiangshui");
                    }
                    if (f1.has("ziwaixian")){
                        ziwaixian = f1.getString("ziwaixian");
                    }
                    if (f1.has("index")){
                        JSONObject index = f1.getJSONObject("index");
                        if (index.has("clothes")){
                            JSONObject clothes = index.getJSONObject("clothes");
                            if (clothes.has("title")){
                                chuanyi = clothes.getString("title");
                                chuanyi_content = clothes.getString("desc");
                            }
                        }
                        if (index.has("comfort")){
                            JSONObject comfort = index.getJSONObject("comfort");
                            if (comfort.has("title")){
                                shushidu = comfort.getString("title");
                                shushidu_content = comfort.getString("desc");
                            }
                        }
                        if (index.has("cold")){
                            JSONObject cold = index.getJSONObject("cold");
                            if (cold.has("title")){
                                ganmao = cold.getString("title");
                                ganmao_content = cold.getString("desc");
                            }
                        }
                        if (index.has("sports")){
                            JSONObject sports = index.getJSONObject("sports");
                            if (sports.has("title")){
                                yundong = sports.getString("title");
                                yundong_content = sports.getString("desc");
                            }
                        }
                        if (index.has("travel")){
                            JSONObject travel = index.getJSONObject("travel");
                            if (travel.has("title")){
                                chuxing = travel.getString("title");
                                chuxing_content = travel.getString("desc");
                            }
                        }
                        if (index.has("wash_car")){
                            JSONObject wash_car = index.getJSONObject("wash_car");
                            if (wash_car.has("title")){
                                xiche = wash_car.getString("title");
                                xiche_content = wash_car.getString("desc");
                            }
                        }
                    }
                }
                JSONObject cityInfo = all.getJSONObject("cityInfo");
                city = cityInfo.getString("c3");
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
                    if (now.has("sd")){
                        shidu = now.getString("sd");
                    }
                    if (now.has("wind_direction") && now.has("wind_power") ){
                        fengli = now.getString("wind_direction") + now.getString("wind_power");
                    }
                    if (now.has("aqiDetail")){
                        JSONObject aqiDetail = now.getJSONObject("aqiDetail");
                        if (aqiDetail.has("pm2_5")){
                            pm2_5 = aqiDetail.getString("pm2_5");
                            qlty = aqiDetail.getString("quality");
                        }
                    }
                    loc_time = now.getString("temperature_time");
                    weather_pic = now.getString("weather_pic");
                }
                JSONObject f2 = all.getJSONObject("f2");
                JSONObject f3 = all.getJSONObject("f3");
                JSONObject f4 = all.getJSONObject("f4");
                JSONObject f5 = all.getJSONObject("f5");
                JSONObject f6 = all.getJSONObject("f6");
                JSONObject f7 = all.getJSONObject("f7");

                second_weather = f2.getString("day_weather");
                third_weather = f3.getString("day_weather");
                four_weather = f4.getString("day_weather");


                second_day_temp = f2.getString("day_air_temperature");
                Log.d("ss",second_day_temp);
                third_day_temp = f3.getString("day_air_temperature");
                four_day_temp = f4.getString("day_air_temperature");
                fifth_day_temp = f5.getString("day_air_temperature");
                sixth_day_temp = f6.getString("day_air_temperature");
                seventh_day_temp = f7.getString("day_air_temperature");


                second_night_temp = f2.getString("night_air_temperature");
                third_night_temp = f3.getString("night_air_temperature");
                four_night_temp = f4.getString("night_air_temperature");
                fifth_night_temp = f5.getString("night_air_temperature");
                sixth_night_temp = f6.getString("night_air_temperature");
                seventh_night_temp = f7.getString("night_air_temperature");


                second_night_weather_txt = f2.getString("night_weather");
                third_night_weather_txt = f3.getString("night_weather");
                four_night_weather_txt = f4.getString("night_weather");
                fifth_night_weather_txt = f5.getString("night_weather");
                sixth_night_weather_txt = f6.getString("night_weather");
                if (f7.has("night_weather")){
                    seventh_night_weather_txt = f7.getString("night_weather");
                }
                fifth_day_weather_txt = f5.getString("day_weather");
                sixth_day_weather_txt = f6.getString("day_weather");
                seventh_day_weather_txt = f7.getString("day_weather");


                String second_day_weather_pic = f2.getString("day_weather_pic");
                String third_day_weather_pic = f3.getString("day_weather_pic");
                String four_day_weather_pic = f4.getString("day_weather_pic");


                dayWeatherPic[1] = second_day_weather_pic;
                dayWeatherPic[2] = third_day_weather_pic;
                dayWeatherPic[3] = four_day_weather_pic;
                dayWeatherPic[4] = f5.getString("day_weather_pic");
                dayWeatherPic[5] = f6.getString("day_weather_pic");
                dayWeatherPic[6] = f7.getString("day_weather_pic");


                String second_night_weather_pic = f2.getString("night_weather_pic");
                String third_night_weather_pic = f3.getString("night_weather_pic");
                String four_night_weather_pic = f4.getString("night_weather_pic");


                nightWeatherPic[1] = second_night_weather_pic;
                nightWeatherPic[2] = third_night_weather_pic;
                nightWeatherPic[3] = four_night_weather_pic;
                nightWeatherPic[4] = f5.getString("night_weather_pic");
                nightWeatherPic[5] = f6.getString("night_weather_pic");
                nightWeatherPic[6] = f7.getString("night_weather_pic");

            }



        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public String getChuanyi_content() {
        return chuanyi_content;
    }

    public String getChuxing_content() {
        return chuxing_content;
    }

    public String getGanmao_content() {
        return ganmao_content;
    }

    public String getShushidu_content() {
        return shushidu_content;
    }

    public String getXiche_content() {
        return xiche_content;
    }

    public String getYundong_content() {
        return yundong_content;
    }

    public Boolean getBoolean(){
        return b;
    }

    public String getFifth_day_weather_txt() {
        return fifth_day_weather_txt;
    }

    public String getSixth_day_weather_txt() {
        return sixth_day_weather_txt;
    }

    public String getSeventh_day_weather_txt() {
        return seventh_day_weather_txt;
    }

    public String getFifth_night_weather_txt() {
        return fifth_night_weather_txt;
    }

    public String getSixth_night_weather_txt() {
        return sixth_night_weather_txt;
    }

    public String getSeventh_night_weather_txt() {
        return seventh_night_weather_txt;
    }

    public String getGanmao() {
        return ganmao;
    }

    public String getChuanyi() {
        return chuanyi;
    }

    public String getChuxing() {
        return chuxing;
    }

    public String getShushidu() {
        return shushidu;
    }

    public String getXiche() {
        return xiche;
    }

    public String getYundong() {
        return yundong;
    }


    public String getFirst_night_temp() {
        return first_night_temp;
    }

    public String getFirst_night_weather_txt() {
        return first_night_weather_txt;
    }

    public String getFour_night_temp() {
        return four_night_temp;
    }

    public String getFour_night_weather_txt() {
        return four_night_weather_txt;
    }

    public String getSecond_night_temp() {
        return second_night_temp;
    }

    public String getSecond_night_weather_txt() {
        return second_night_weather_txt;
    }

    public String getThird_night_temp() {
        return third_night_temp;
    }

    public String getThird_night_weather_txt() {
        return third_night_weather_txt;
    }

    public String getPm2_5() {
        return pm2_5;
    }

    public String getZiwaixian() {
        return ziwaixian;
    }

    public String getJiangshui() {
        return jiangshui;
    }

    public String getQiya() {
        return qiya;
    }

    public String getRichu_riluo() {
        return richu_riluo;
    }

    public String getShidu() {
        return shidu;
    }

    public String[] getDayWeatherPic(){
        return dayWeatherPic;
    }
    public String[] getNightWeatherPic(){
        return nightWeatherPic;
    }

    public String getCity() {
        return city;
    }

    public String getWeather_pic() {
        return weather_pic;
    }

    public String getLoc_time(){
        return loc_time;
    }
    public String getAqi(){
        return aqi;
    }
    public String getQlty(){
        return qlty;
    }
    public String getWeather_txt(){
        return weather_txt;
    }
    public String getTemp(){
        return temp + "°";
    }
    public String getFengli(){
        return fengli;
    }
    public String getMax_tmp(){
        return first_max_tmp;
    }

    public String getFirst_day_temp() {
        return first_day_temp;
    }

    public String getSecond_day_temp() {
        return second_day_temp;
    }

    public String getThird_day_temp() {
        return third_day_temp;
    }

    public String getFour_day_temp() {
        return four_day_temp;
    }

    public String getFirst_weather() {
        return first_weather;
    }

    public String getSecond_weather() {
        return second_weather;
    }

    public String getThird_weather() {
        return third_weather;
    }

    public String getFour_weather() {
        return four_weather;
    }

    public String getFifth_day_temp() {
        return fifth_day_temp;
    }

    public String getFifth_night_temp() {
        return fifth_night_temp;
    }

    public String getSeventh_day_temp() {
        return seventh_day_temp;
    }

    public String getSeventh_night_temp() {
        return seventh_night_temp;
    }

    public String getSixth_day_temp() {
        return sixth_day_temp;
    }

    public String getSixth_night_temp() {
        return sixth_night_temp;
    }

    public String getErr_code() {
        return err_code;
    }

    public String getErr_msg() {
        return err_msg;
    }
}
