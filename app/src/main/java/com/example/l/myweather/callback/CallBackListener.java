package com.example.l.myweather.callback;

import org.json.JSONObject;

/**
 * Created by L on 2015/10/3.
 */
public interface CallBackListener {
    void onFinish(JSONObject jsonObject);
    void onError(String e);

}
