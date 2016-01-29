package com.example.l.myweather;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class SettingsActivity extends PreferenceActivity {
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        }
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
        private ListPreference styleList;
        private ListPreference widgetColorList;
        private SharedPreferences sharedPreferences;
        private Context context = MyApplication.getContext();
        //private SwitchPreference widget_background;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
            listPreference = (ListPreference)findPreference("update_rate");
            styleList = (ListPreference) findPreference("style_color");
            widgetColorList = (ListPreference) findPreference("widget_color");
            sharedPreferences = getPreferenceScreen().getSharedPreferences();
            listPreference.setSummary(sharedPreferences.getString("update_rate", ""));
            final ListPreference styleList = (ListPreference)findPreference("style_color");
            styleList.setSummary(sharedPreferences.getString("style_color", ""));
            widgetColorList.setSummary(sharedPreferences.getString("widget_color",""));
            if (sharedPreferences.getBoolean("update_switch",false)){
                listPreference.setEnabled(true);
            } else {
                listPreference.setEnabled(false);

            }
            //widget_background = (SwitchPreference)findPreference("widget_background");
            sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);


        }

        private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                switch (key){
                    case "update_rate":
                        listPreference.setSummary(sharedPreferences.getString("update_rate", ""));
                        context.startService(new Intent(context, UpdateService.class));
                        break;
                    case "update_switch":
                        boolean b = sharedPreferences.getBoolean("update_switch",false);

                        //int appWidgetIds[] = Widget4x2.appWidgetManager.getAppWidgetIds(new ComponentName(context, Widget4x2.class));
                        //int newWidgetIds[] = Widget4x2.appWidgetManager.getAppWidgetIds(new ComponentName(context,NewAppWidget.class));
                        Intent serviceIntent = new Intent(context, UpdateService.class);
                        //context.stopService(serviceIntent);
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
                    case "style_color":
                        String color = sharedPreferences.getString("style_color", "青色");
                        styleList.setSummary(color);
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
                            //Log.d("FUCK","FUCK");
                        } else {
                            WeatherNotification.cancelNotification();
                        }
                        Intent intent1 = new Intent(context,UpdateService.class);
                        intent1.setAction("showNotification");
                        intent1.putExtra("showNotification", b);
                        context.startService(intent1);
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



    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        String color = sharedPreferences.getString("style_color", "青色");
        switch (color){
            case "蓝色":
                toolbar.setBackgroundColor(Color.parseColor("#104d8e"));
                setTheme(R.style.lanseTheme);
                break;
            case "灰色":
                setTheme(R.style.huiseTheme);
                toolbar.setBackgroundColor(Color.GRAY);
                break;
            case "青色":
                setTheme(R.style.qingseTheme);
                toolbar.setBackgroundColor(Color.parseColor("#FF00786F"));
                break;
            case "绿色":
                setTheme(R.style.lvseTheme);
                toolbar.setBackgroundColor(Color.parseColor("#2e8b57"));
                break;
            case "黑色":
                setTheme(R.style.heiseTheme);
                toolbar.setBackgroundColor(Color.BLACK);
                break;
            case "咖啡色":
                setTheme(R.style.kafeiseTheme);
                toolbar.setBackgroundColor(Color.parseColor("#5f4421"));
                break;
        }
    }
}

