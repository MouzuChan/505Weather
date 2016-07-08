package com.example.l.myweather.callback;

/**
 * Created by L on 2016-07-08.
 */
public interface CheckUpdateCallBack {

    void hasUpdate(String newVersionName,String changelog,String url);
    void noUpdate();
    void onError(int errorCode);

}
