package com.example.l.myweather;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.l.myweather.settings.AppInfo;
import com.example.l.myweather.settings.AppInfoAdapter;
import com.example.l.myweather.settings.AppInfoDataBase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


public class SettingsActivity extends AppCompatActivity{
    private static Toolbar toolbar;
    private static int FIRST_SCREEN_FLAG = 0;
    private static int CHILD_SCREEN_FLAG = 1;
    private static int flag;
    private SettingsFragment settingsFragment;
    private static ListView app_list;
    private static ArrayList<AppInfo> app_info_list;
    private static AppInfoAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_settings);
        initView();
        getFragmentManager().beginTransaction().add(R.id.content, settingsFragment).commit();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                initAppInfoList();
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask,2000);
    }
    public void initView(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        //toolbar.setTitle("设置");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        settingsFragment = new SettingsFragment();
        //toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        /*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

        app_info_list = new ArrayList<>();

        adapter = new AppInfoAdapter(app_info_list);


    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {


        private ListPreference listPreference;
        private ListPreference widgetColorList;
        private SharedPreferences sharedPreferences;
        private ListPreference notify_background;
        private ListPreference notify_text_color;
        private ListPreference widget_text_color;
        private Context context = MyApplication.getContext();

        private Preference preference;

        private PreferenceScreen mainScreen;
        private PreferenceScreen customScreen;

        private Preference click_time_event;
        private Preference click_weather_event,click_date_event;

        private SharedPreferences app_name_preferences;

        private ListPreference icon_style;
        //SharedPreferences.Editor editor;
        //SharedPreferences package_preferences = context.getSharedPreferences("package_preferences",MODE_MULTI_PROCESS);

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
            listPreference = (ListPreference) findPreference("update_rate");
            widgetColorList = (ListPreference) findPreference("widget_color");
            notify_background = (ListPreference) findPreference("notify_background");
            notify_text_color = (ListPreference) findPreference("notify_text_color");
            widget_text_color = (ListPreference) findPreference("widget_text_color");
            preference = findPreference("custom_click");
            click_time_event = findPreference("click_time_event");
            click_weather_event = findPreference("click_weather_event");
            click_date_event = findPreference("click_date_event");
            mainScreen = (PreferenceScreen) findPreference("main_screen");
            customScreen = (PreferenceScreen) findPreference("custom_screen");

            icon_style = (ListPreference) findPreference("icon_style");
            flag = FIRST_SCREEN_FLAG;
            setKeyBack();
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    flag = CHILD_SCREEN_FLAG;
                    setKeyBack();
                    return false;
                }
            });


            click_time_event.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showSelectedDialog(0);
                    return false;
                }
            });

            click_weather_event.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showSelectedDialog(1);
                    return false;
                }
            });

            click_date_event.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showSelectedDialog(2);
                    return false;
                }
            });

            app_name_preferences = context.getSharedPreferences("app_name_preferences",MODE_APPEND);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            listPreference.setSummary(sharedPreferences.getString("update_rate", "1个小时"));
            widgetColorList.setSummary(sharedPreferences.getString("widget_color", "透明"));
            notify_background.setSummary(sharedPreferences.getString("notify_background", "系统默认底色"));
            notify_text_color.setSummary(sharedPreferences.getString("notify_text_color", "黑色"));
            widget_text_color.setSummary(sharedPreferences.getString("widget_text_color", "白色"));
            icon_style.setSummary(sharedPreferences.getString("icon_style","单色"));
            click_weather_event.setSummary(app_name_preferences.getString("click_weather_event",""));
            click_time_event.setSummary(app_name_preferences.getString("click_time_event",""));
            click_date_event.setSummary(app_name_preferences.getString("click_date_event",""));
            if (sharedPreferences.getBoolean("update_switch", false)) {
                listPreference.setEnabled(true);
            } else {
                listPreference.setEnabled(false);
            }
            if (sharedPreferences.getBoolean("show_notification", false)) {
                notify_background.setEnabled(true);
                notify_text_color.setEnabled(true);
            } else {
                notify_background.setEnabled(false);
                notify_text_color.setEnabled(false);
            }




        }




        @Override
        public void onPause() {
            super.onDestroy();
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onResume() {
            super.onResume();
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }
        public void showSelectedDialog(final int f){
            View view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.selected_app_layout,null);
            app_list = (ListView) view.findViewById(R.id.app_list);
            app_list.setAdapter(adapter);
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(view);
            dialog.show();
            app_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dialog.cancel();

                    String app_name = app_info_list.get(position).getApp_name();
                    String package_name = app_info_list.get(position).getPackage_name();

                    if (app_name != null && package_name != null && app_name.length() > 0 && package_name.length() > 0){

                        if (f == 0){
                            click_time_event.setSummary(app_name);
                            sharedPreferences.edit().putString("click_time_event",package_name).apply();
                            Intent intent = new Intent(context,UpdateService.class);
                            intent.setAction("click_time_event");
                            intent.putExtra("click_time_event",package_name);
                            context.startService(intent);
                            app_name_preferences.edit().putString("click_time_event",app_name).apply();
                        } else if (f == 1){
                            click_weather_event.setSummary(app_name);
                            sharedPreferences.edit().putString("click_weather_event",package_name).apply();
                            Intent intent = new Intent(context,UpdateService.class);
                            intent.setAction("click_weather_event");
                            intent.putExtra("click_weather_event",package_name);
                            context.startService(intent);
                            app_name_preferences.edit().putString("click_weather_event",app_name).apply();
                        } else if (f == 2){
                            click_date_event.setSummary(app_name);
                            sharedPreferences.edit().putString("click_date_event",package_name).apply();
                            Intent intent = new Intent(context,UpdateService.class);
                            intent.setAction("click_date_event");
                            intent.putExtra("click_date_event",package_name);
                            context.startService(intent);
                            app_name_preferences.edit().putString("click_date_event",app_name).apply();
                        }

                        //context.sendBroadcast(new Intent("com.lha.weather.UPDATE_FROM_LOCAL"));
                    }



                }
            });

        }


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
            switch (key) {
                case "update_rate":
                    listPreference.setSummary(sharedPreferences.getString(key, "1个小时"));
                    Intent rateIntent = new Intent(context, UpdateService.class);
                    rateIntent.putExtra(key,sharedPreferences.getString(key,"1个小时"));
                    rateIntent.setAction(key);
                    context.startService(rateIntent);
                    break;
                case "update_switch":
                    Intent serviceIntent = new Intent(context, UpdateService.class);
                    if (sharedPreferences.getBoolean(key, false)) {
                        listPreference.setEnabled(true);
                        serviceIntent.putExtra(key, true);
                        serviceIntent.setAction(key);
                        context.startService(serviceIntent);
                    } else {
                        listPreference.setEnabled(false);
                        serviceIntent.putExtra(key, false);
                        serviceIntent.setAction(key);
                        context.startService(serviceIntent);
                    }
                    break;
                case "show_notification":
                    boolean b = sharedPreferences.getBoolean(key,false);
                    if (b) {
                        WeatherNotification.sendNotification(null, null);
                        notify_background.setEnabled(true);
                        notify_text_color.setEnabled(true);
                    } else {
                        notify_background.setEnabled(false);
                        notify_text_color.setEnabled(false);
                        WeatherNotification.cancelNotification();
                    }
                    Intent intent = new Intent(context, UpdateService.class);
                    intent.setAction(key);
                    intent.putExtra(key,b);
                    context.startService(intent);
                    break;
                case "notify_background":
                    String notify_background_strings = sharedPreferences.getString(key, "系统默认底色");
                    notify_background.setSummary(notify_background_strings);
                    WeatherNotification.sendNotification(null, null);
                    Intent intent1 = new Intent(context, UpdateService.class);
                    intent1.setAction(key);
                    intent1.putExtra(key,notify_background_strings);
                    context.startService(intent1);
                    break;
                case "notify_text_color":
                    String text_color = sharedPreferences.getString(key, "黑色");
                    notify_text_color.setSummary(text_color);
                    WeatherNotification.sendNotification(null, null);
                    Intent intent2 = new Intent(context, UpdateService.class);
                    intent2.setAction(key);
                    intent2.putExtra(key,text_color);
                    context.startService(intent2);
                    break;
                case "widget_color":
                    String widget_color = sharedPreferences.getString(key, "透明");
                    widgetColorList.setSummary(widget_color);
                    //context.sendBroadcast(new Intent("com.lha.weather.UPDATE_FROM_LOCAL"));

                    Intent intent3 = new Intent(context, UpdateService.class);
                    intent3.setAction(key);
                    intent3.putExtra(key,widget_color);
                    context.startService(intent3);
                    break;
                case "widget_text_color":
                    String widgetTextColor = sharedPreferences.getString(key, "白色");
                    widget_text_color.setSummary(widgetTextColor);
                    //context.sendBroadcast(new Intent("com.lha.weather.UPDATE_FROM_LOCAL"));
                    Intent intent4 = new Intent(context, UpdateService.class);
                    intent4.setAction(key);
                    intent4.putExtra(key,widgetTextColor);
                    context.startService(intent4);
                    break;
                case "alarm_notification":
                    boolean b1 = sharedPreferences.getBoolean(key,true);
                    Intent intent5 = new Intent(context,UpdateService.class);
                    intent5.setAction(key);
                    intent5.putExtra(key,b1);
                    context.startService(intent5);
                    break;
                case "icon_style":
                    String icon = sharedPreferences.getString("icon_style","单色");
                    icon_style.setSummary(icon);
                    if (sharedPreferences.getBoolean("show_notification",false)){
                        WeatherNotification.sendNotification(null, null);
                    } else {
                        WeatherNotification.cancelNotification();
                    }
                    Intent intent6 = new Intent(context,UpdateService.class);
                    intent6.setAction(key);
                    intent6.putExtra(key,icon);
                    context.startService(intent6);
                    break;


            }
        }


        public void setKeyBack(){
            if (flag == FIRST_SCREEN_FLAG){
                setPreferenceScreen(mainScreen);
                toolbar.setTitle("设置");
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().finish();
                    }
                });
            } else if (flag == CHILD_SCREEN_FLAG){
                setPreferenceScreen(customScreen);
                toolbar.setTitle("自定义点击事件");
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setPreferenceScreen(mainScreen);
                        flag = FIRST_SCREEN_FLAG;
                        setKeyBack();
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (flag == FIRST_SCREEN_FLAG){
            finish();
        } else if (flag == CHILD_SCREEN_FLAG){
            flag = FIRST_SCREEN_FLAG;
            settingsFragment.setKeyBack();
        }
    }

    public void initAppInfoList(){
       // TimerTask timerTask = new TimerTask() {
         //   @Override
        //    public void run() {
                app_info_list.clear();
                PackageManager packageManager = MyApplication.getContext().getPackageManager();
                Intent mIntent = new Intent(Intent.ACTION_MAIN, null);
                mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                List<ResolveInfo> listAllApps = packageManager.queryIntentActivities(mIntent, 0);
                for (int i = 0; i < listAllApps.size(); i++){
                    ResolveInfo resolveInfo = listAllApps.get(i);
                    AppInfo appInfo = new AppInfo();
                    appInfo.setApp_icon(resolveInfo.loadIcon(packageManager));
                    appInfo.setApp_name(resolveInfo.loadLabel(packageManager).toString());
                    appInfo.setPackage_name(resolveInfo.activityInfo.packageName);
                    app_info_list.add(appInfo);
                }
         //   }
      //  };
       // Timer timer = new Timer();
       // timer.schedule(timerTask,2000);
    }

}

