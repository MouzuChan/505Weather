package com.example.l.myweather.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.example.l.myweather.base.MyApplication;
import com.example.l.myweather.callback.CallBackListener;
import com.example.l.myweather.callback.CheckUpdateCallBack;

import org.json.JSONObject;

/**
 * Created by L on 2016-07-08.
 */
public class CheckUpdate {
    private String url;
    private Context mContext;
    private int versionCode;
    private String versionName;

    private int newVersionCode;
    private String newVersionName;

    private String changelog;
    private String install_url;

    public CheckUpdate(){
        url = "https://api.fir.im/apps/latest/577f4fa2748aac40af00003b?api_token=31c3a4654a8b3bc47d68af9a664466bb";
        mContext = MyApplication.getContext();
        initVersionCode();
    }

    public void sendHttpRequest(final CheckUpdateCallBack checkUpdateCallBack){

        HttpUtil.makeHttpRequest(url, new CallBackListener() {
            @Override
            public void onFinish(JSONObject jsonObject) {
                if (jsonObject != null){
                    try {
                        newVersionCode = jsonObject.getInt("version");
                        newVersionName = jsonObject.getString("versionShort");
                        changelog = jsonObject.getString("changelog");
                        install_url = jsonObject.getString("install_url");

                        if (checkUpdateCallBack != null){
                            if (newVersionCode > versionCode){
                                checkUpdateCallBack.hasUpdate(newVersionName,changelog,install_url);
                            } else {
                                checkUpdateCallBack.noUpdate();
                            }
                        }

                    } catch (Exception e){
                        e.printStackTrace();
                        if (checkUpdateCallBack != null){
                            checkUpdateCallBack.onError(0);
                        }
                    }
                }
            }

            @Override
            public void onError(String e) {
                if (checkUpdateCallBack != null){
                    checkUpdateCallBack.onError(1);
                }
            }
        });
    }

    public void initVersionCode(){
        try{
            PackageManager packageManager = mContext.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(mContext.getPackageName(),0);
            versionCode = packageInfo.versionCode;
            versionName = packageInfo.versionName;
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
