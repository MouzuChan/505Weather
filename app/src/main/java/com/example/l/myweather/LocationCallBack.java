package com.example.l.myweather;

/**
 * Created by L on 2015/10/7.
 */
public interface LocationCallBack {
    void onFinish(String return_id,String city_name);
    void onError();
}
