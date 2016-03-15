package com.example.l.myweather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
//import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by L on 2015/10/19.
 */
public class NewAppWidget extends AppWidgetProvider {
    private static Context context = MyApplication.getContext();
    private static PackageManager packageManager;
    private static int USER_UPDATE_FLAG = 0;
    //private AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    private String city;
    private String city_id;

    private int[] views_id = new int[4];
    private String[] widget2x1_strings;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        int N = appWidgetIds.length;
        for (int i = 0; i < N; i++){
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        //Log.d("TAG", intent.getAction());
        switch (intent.getAction()){
            case "com.lha.weather.USER_UPDATE":
                USER_UPDATE_FLAG = 1;
                updateWidget(1);
                break;
            case "com.lha.weather.UPDATE_FROM_INTERNET":
                updateWidget(1);
                break;
            case "com.lha.weather.UPDATE_FROM_LOCAL":
                updateWidget(0);
                break;
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        if (sharedPreferences.getBoolean("update_switch", false)){
            Intent intent = new Intent(context,UpdateService.class);
            intent.setAction("Widget");
            context.startService(intent);
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }


    public void updateAppWidget(final Context context,final AppWidgetManager appWidgetManager,final int appWidgetId){

        if (city_id == null){
            initCity();
        }
        if (city_id != null){
            if (MyApplication.isConnected()){
                String url = "http://zhwnlapi.etouch.cn/Ecalender/api/v2/weather?citykey=" + city_id;
                HttpUtil.makeHttpRequest(url, new CallBackListener() {
                    @Override
                    public void onFinish(JSONObject jsonObject) {
                        if (USER_UPDATE_FLAG == 1) {
                            Toast.makeText(context, "更新成功^_^", Toast.LENGTH_SHORT).show();
                            USER_UPDATE_FLAG = 0;
                        }
                        setWidgetViews(context, appWidgetManager, appWidgetId, jsonObject, city_id);
                        FileHandle.saveJSONObject(jsonObject, city_id);
                    }

                    @Override
                    public void onError(String e) {
                        if (USER_UPDATE_FLAG == 1) {
                            Toast.makeText(context, "网络超时⊙﹏⊙‖∣", Toast.LENGTH_SHORT).show();
                            USER_UPDATE_FLAG = 0;
                        }
                        JSONObject jsonObject = FileHandle.getJSONObject(city_id);
                        if (jsonObject != null) {
                            setWidgetViews(context, appWidgetManager, appWidgetId, jsonObject, city_id);
                        }
                    }
                });
            } else {
                if (USER_UPDATE_FLAG == 1){
                    Toast.makeText(context,"没有网络@_@",Toast.LENGTH_SHORT).show();
                    USER_UPDATE_FLAG = 0;
                }
                JSONObject jsonObject = FileHandle.getJSONObject(city_id);
                if (jsonObject != null){
                    setWidgetViews(context, appWidgetManager, appWidgetId, jsonObject, city_id);
                }
            }
        } else {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
            views.setTextViewText(R.id.date,"未找到已添加的城市...");
            views.setTextViewText(R.id.aqi,"");
            views.setTextViewText(R.id.chinese_calendar,"");
            views.setTextViewText(R.id.weather_txt,"");
            views.setTextViewText(R.id.temp, "");
            views.setTextViewText(R.id.city, "");
            Intent intent = new Intent(context,MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.relative_layout, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }


    public void updateFromLocal(final Context context,final AppWidgetManager appWidgetManager,final int appWidgetId){
        if (city_id == null){
            initCity();
        }
        if (city_id != null){
            JSONObject jsonObject = FileHandle.getJSONObject(city_id);
            if (jsonObject != null){
                setWidgetViews(context,appWidgetManager,appWidgetId,jsonObject,city_id);
            }

        }  else {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
            views.setTextViewText(R.id.date,"未找到已添加的城市...");
            views.setTextViewText(R.id.aqi,"");
            views.setTextViewText(R.id.chinese_calendar,"");
            views.setTextViewText(R.id.weather_txt,"");
            views.setTextViewText(R.id.temp, "");
            views.setTextViewText(R.id.city, "");
            Intent intent = new Intent(context,MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.relative_layout, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }



    public void setWidgetViews(Context context,AppWidgetManager appWidgetManager,int appWidgetId,JSONObject jsonObject,String id){

        String _week = "";
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        if (week == 1){
            _week = "周日";
        } else if (week == 2){
            _week = "周一";
        }else if (week == 3){
            _week = "周二";
        }else if (week == 4){
            _week = "周三";
        }else if (week == 5){
            _week = "周四";
        }else if (week == 6){
            _week = "周五";
        }else if (week == 7){
            _week = "周六";
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        JSONHandle jsonHandle = new JSONHandle(jsonObject);
        widget2x1_strings = jsonHandle.getWidget2x1_strings(hour);

        views_id[0] = R.id.temp;
        views_id[1] = R.id.weather_txt;
        views_id[2] = R.id.aqi;
        views_id[3] = R.id.update_time;

        for (int i = 0; i < 4; i++){
            if (widget2x1_strings[i] != null && !widget2x1_strings[i].isEmpty()){
                views.setTextViewText(views_id[i],widget2x1_strings[i]);
            }
        }


        views.setTextViewText(R.id.city,city);
        views.setTextViewText(R.id.date, month + "/" + day  + " " + _week);
        CalendarUtil calendarUtil = new CalendarUtil();
        String chineseDay = calendarUtil.getChineseMonth(year,month,day) + calendarUtil.getChineseDay(year,month,day);

        views.setTextViewText(R.id.chinese_calendar, chineseDay);

        Intent intent = new Intent(context,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.weather_layout, pendingIntent);
        List<PackageInfo> packageInfo = packageManager.getInstalledPackages(PackageManager.MATCH_DEFAULT_ONLY);
        Intent intent2 = null;
        String packageName;

        for (int a = 0; a < packageInfo.size(); a++){
            packageName = packageInfo.get(a).packageName;
            //ApplicationInfo applicationInfo = packageInfo.get(a).applicationInfo;
            //String appName = (String)applicationInfo.loadLabel(packageManager);
            if (packageName.contains("clock")){
                intent2 = packageManager.getLaunchIntentForPackage(packageName);
            }
        }
        if (intent2 != null){
            PendingIntent pi = PendingIntent.getActivity(context,0,intent2,0);
            views.setOnClickPendingIntent(R.id.clock_layout,pi);
        } else {
            //Toast.makeText(context,"未找到时钟应用",Toast.LENGTH_SHORT).show();
        }
        Intent updateIntent = new Intent("com.lha.weather.USER_UPDATE");
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(context,0,updateIntent,0);
        views.setOnClickPendingIntent(R.id.update_time, updatePendingIntent);

        switch (sharedPreferences.getString("widget_color","透明")){
            case "蓝色":
                views.setInt(R.id.relative_layout,"setBackgroundResource",R.drawable.widget_background);
                views.setTextColor(R.id.city, Color.WHITE);
                views.setTextColor(R.id.date,Color.WHITE);
                views.setTextColor(R.id.chinese_calendar,Color.WHITE);
                views.setTextColor(R.id.temp,Color.WHITE);
                views.setTextColor(R.id.aqi,Color.WHITE);
                views.setTextColor(R.id.weather_txt,Color.WHITE);
                views.setTextColor(R.id.update_time,Color.WHITE);

                break;
            case "透明":
                views.setInt(R.id.relative_layout,"setBackgroundResource",R.drawable.touming_background);
                views.setTextColor(R.id.city, Color.WHITE);
                views.setTextColor(R.id.date,Color.WHITE);
                views.setTextColor(R.id.chinese_calendar,Color.WHITE);
                views.setTextColor(R.id.temp,Color.WHITE);
                views.setTextColor(R.id.aqi,Color.WHITE);
                views.setTextColor(R.id.weather_txt,Color.WHITE);
                views.setTextColor(R.id.update_time,Color.WHITE);
                break;
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    public void updateWidget(int i){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,NewAppWidget.class));
        if (appWidgetIds.length != 0){
            if (i == 0){
                for (int in = 0; in < appWidgetIds.length; in++){
                    updateFromLocal(context, appWidgetManager, appWidgetIds[in]);
                }
            } else if (i == 1){
                for (int in = 0; in < appWidgetIds.length; in++){
                    updateAppWidget(context,appWidgetManager,appWidgetIds[in]);
                }
            }

        }
    }

    public  void initCity() {
        packageManager = context.getPackageManager();
        CityDataBase cityDataBase = CityDataBase.getInstance();
        SQLiteDatabase db = cityDataBase.getWritableDatabase();
        Cursor cursor = db.query("city", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            city_id = cursor.getString(cursor.getColumnIndex("city_id"));
            city = cursor.getString(cursor.getColumnIndex("city"));
        }
        cursor.close();

    }


}
