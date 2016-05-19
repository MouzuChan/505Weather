package com.example.l.myweather.settings;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;

/**
 * Created by L on 2016-04-27.
 */
public class AppInfo {
    private String app_name;
    private String package_name;
    private Drawable app_icon;

    public AppInfo(){

    }

    public void setApp_icon(Drawable app_icon) {
        this.app_icon = app_icon;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public Drawable getApp_icon() {
        return app_icon;
    }

    public String getApp_name() {
        return app_name;
    }

    public String getPackage_name() {
        return package_name;
    }
}
