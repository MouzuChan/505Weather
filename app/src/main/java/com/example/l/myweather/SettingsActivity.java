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
        private Context context = MyApplication.getContext();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
            listPreference = (ListPreference)findPreference("update_rate");
            widgetColorList = (ListPreference) findPreference("widget_color");
            sharedPreferences = getPreferenceScreen().getSharedPreferences();
            listPreference.setSummary(sharedPreferences.getString("update_rate", ""));
            widgetColorList.setSummary(sharedPreferences.getString("widget_color",""));
            if (sharedPreferences.getBoolean("update_switch",false)){
                listPreference.setEnabled(true);
            } else {
                listPreference.setEnabled(false);

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
                        } else {
                            WeatherNotification.cancelNotification();
                        }
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

