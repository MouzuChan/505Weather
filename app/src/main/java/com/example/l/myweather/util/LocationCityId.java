package com.example.l.myweather.util;

import android.widget.Toast;

import com.example.l.myweather.base.MyApplication;
import com.example.l.myweather.callback.CallBackListener;
import com.example.l.myweather.callback.LocationCallBack;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * Created by L on 2015/10/7.
 */
public class LocationCityId {
    private String return_id;
    private String city_name;

    public void getLocationCityId(final String city, final String district, final LocationCallBack locationCallBack) {

        String cityName = "";
        try {
            cityName = URLEncoder.encode(city,"UTF-8");
        } catch (Exception e){
            e.printStackTrace();
        }

        String url = "http://apis.baidu.com/apistore/weatherservice/citylist?cityname=" + cityName;
        HttpUtil.makeBaiduHttpRequest(url, new CallBackListener() {
            @Override
            public void onFinish(JSONObject jsonObject) {

                try {

                    String errMsg = jsonObject.getString("errMsg");

                    if (errMsg.equals("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("retData");
                        for (int i = jsonArray.length() - 1; i >=0; i--) {
                            JSONObject jo = jsonArray.getJSONObject(i);
                            String name_cn = jo.getString("name_cn");
                            if (name_cn.equals(district)) {
                                return_id = jo.getString("area_id");
                                city_name = district;
                                break;
                            } else if (name_cn.equals(city)) {
                                return_id = jo.getString("area_id");
                                city_name = city;
                                break;
                            }
                        }
                        if (locationCallBack != null) {
                            locationCallBack.onFinish(return_id, city_name);
                        }


                    } else {
                        locationCallBack.onError();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String e) {
                Toast.makeText(MyApplication.getContext(), "更新失败,网络超时", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
