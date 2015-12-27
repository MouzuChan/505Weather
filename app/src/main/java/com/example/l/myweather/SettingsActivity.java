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
        private SharedPreferences sharedPreferences;
        private Context context = MyApplication.getContext();
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
            listPreference = (ListPreference)findPreference("update_rate");
            styleList = (ListPreference) findPreference("style_color");
            sharedPreferences = getPreferenceScreen().getSharedPreferences();
            listPreference.setSummary(sharedPreferences.getString("update_rate", ""));
            final ListPreference styleList = (ListPreference)findPreference("style_color");
            styleList.setSummary(sharedPreferences.getString("style_color",""));
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
                if (key.equals("update_rate")) {
                    listPreference.setSummary(sharedPreferences.getString("update_rate", ""));
                    context.startService(new Intent(context, UpdateService.class));
                } else if (key.equals("update_switch")){
                    boolean b = sharedPreferences.getBoolean("update_switch",false);
                    if (!b){
                        listPreference.setEnabled(false);
                        context.stopService(new Intent(context, UpdateService.class));
                    } else {
                        listPreference.setEnabled(true);
                        context.startService(new Intent(context, UpdateService.class));
                    }
                }  else if (key.equals("style_color")){
                    String color = sharedPreferences.getString("style_color", "");
                    styleList.setSummary(color);
                }
            }
        };

        @Override
        public void onPause() {
            super.onPause();
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

