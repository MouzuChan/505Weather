package com.example.l.myweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


public class SettingsActivity extends PreferenceActivity {
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //window.setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_settings);
        initView();
        getFragmentManager().beginTransaction().add(R.id.content,new SettingsFragment()).commit();
    }
    public void initView(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("设置");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static class SettingsFragment extends PreferenceFragment{
        private ListPreference listPreference;
        private ListPreference widgetColorList;
        private SharedPreferences sharedPreferences;
        private ListPreference notify_background;
        private ListPreference notify_text_color;
        private ListPreference widget_text_color;
        private Context context = MyApplication.getContext();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
            listPreference = (ListPreference)findPreference("update_rate");
            widgetColorList = (ListPreference) findPreference("widget_color");
            notify_background = (ListPreference) findPreference("notify_background");
            notify_text_color = (ListPreference) findPreference("notify_text_color");
            widget_text_color = (ListPreference) findPreference("widget_text_color");
            sharedPreferences = getPreferenceScreen().getSharedPreferences();
            listPreference.setSummary(sharedPreferences.getString("update_rate", "1个小时"));
            widgetColorList.setSummary(sharedPreferences.getString("widget_color","透明"));
            notify_background.setSummary(sharedPreferences.getString("notify_background","系统默认底色"));
            notify_text_color.setSummary(sharedPreferences.getString("notify_text_color","黑色"));
            widget_text_color.setSummary(sharedPreferences.getString("widget_text_color","白色"));
            if (sharedPreferences.getBoolean("update_switch",false)){
                listPreference.setEnabled(true);
            } else {
                listPreference.setEnabled(false);
            }
            if (sharedPreferences.getBoolean("show_notification",false)){
                notify_background.setEnabled(true);
                notify_text_color.setEnabled(true);
            } else {
                notify_background.setEnabled(false);
                notify_text_color.setEnabled(false);
            }
            sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);


        }

        private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                switch (key){
                    case "update_rate":
                        listPreference.setSummary(sharedPreferences.getString("update_rate", ""));
                        Intent rateIntent = new Intent(context,UpdateService.class);
                        rateIntent.setAction("ChangeUpdateRate");
                        context.startService(rateIntent);
                        break;
                    case "update_switch":
                        boolean b = sharedPreferences.getBoolean("update_switch",false);
                        Intent serviceIntent = new Intent(context, UpdateService.class);
                        if (b){
                            listPreference.setEnabled(true);
                            serviceIntent.putExtra("updateSwitch",true);
                            serviceIntent.setAction("updateSwitch");
                            context.startService(serviceIntent);
                        }
                        else {
                            listPreference.setEnabled(false);
                            serviceIntent.putExtra("updateSwitch",false);
                            serviceIntent.setAction("updateSwitch");
                            context.startService(serviceIntent);
                        }
                        break;
                    case "widget_color":
                        String widget_color = sharedPreferences.getString("widget_color","蓝色");
                        widgetColorList.setSummary(widget_color);
                        Intent intent = new Intent("com.lha.weather.UPDATE_FROM_LOCAL");
                        context.sendBroadcast(intent);
                        break;
                    case "show_notification":
                        b = sharedPreferences.getBoolean("show_notification",false);
                        if (b){
                            WeatherNotification.sendNotification(null,null);
                            notify_background.setEnabled(true);
                            notify_text_color.setEnabled(true);
                        } else {
                            notify_background.setEnabled(false);
                            notify_text_color.setEnabled(false);
                            WeatherNotification.cancelNotification();
                        }
                        break;
                    case "notify_background":
                        String notify_background_strings = sharedPreferences.getString("notify_background","系统默认底色");
                        notify_background.setSummary(notify_background_strings);
                        WeatherNotification.sendNotification(null,null);
                        break;
                    case "notify_text_color":
                        String text_color = sharedPreferences.getString("notify_text_color","黑色");
                        notify_text_color.setSummary(text_color);
                        WeatherNotification.sendNotification(null,null);
                        break;
                    case "widget_text_color":
                        String color = sharedPreferences.getString("widget_text_color","白色");
                        widget_text_color.setSummary(color);
                        context.sendBroadcast(new Intent("com.lha.weather.UPDATE_FROM_LOCAL"));
                        break;
                    case "show_frame":
                        Intent intent3 = new Intent("com.lha.weather.UPDATE_FROM_LOCAL");
                        context.sendBroadcast(intent3);
                        break;
                }

            }
        };

        @Override
        public void onDestroy() {
            super.onDestroy();
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        }

    }

}

