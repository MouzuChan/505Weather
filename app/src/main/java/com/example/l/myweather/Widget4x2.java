package com.example.l.myweather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
//import android.util.Log;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

/**
 * Created by L on 2015-12-30.
 */
public class Widget4x2 extends AppWidgetProvider{
    private static Context context = MyApplication.getContext();
    public AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    private PackageManager packageManager = context.getPackageManager();
    private String city_id,city;
    private JSONObject jsonObject;
    private SharedPreferences sharedPreferences = context.getSharedPreferences("packageName",context.MODE_APPEND);
    private int[] view_id = new int[12];
    private String[] widget_strings;

    private String weatherCode = "{\"晴\":\"100\",\"多云\":\"101\",\"阴\":\"104\",\"阵雨\":\"300\",\"雷阵雨\":\"302\",\"雷阵雨伴有冰雹\":\"304\",\"雨夹雪\":\"404\",\"小雨\":\"305\",\"小到中雨\":\"305\",\"中雨\":\"306\",\"中到大雨\":\"306\",\"大雨\":\"307\",\"大到暴雨\":\"307\",\"暴雨\":\"310\",\"大暴雨\":\"311\",\"特大暴雨\":\"312\",\"阵雪\":\"407\",\"小雪\":\"400\",\"中雪\":\"401\",\"大雪\":\"402\",\"暴雪\":\"403\",\"雾\":\"501\",\"冻雨\":\"313\",\"沙尘暴\":\"507\",\"浮尘\":\"504\",\"扬沙\":\"503\",\"霾\":\"502\",\"强沙尘暴\":\"508\"}";

    private JSONObject weatherObject;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        if (MyApplication.isConnected()){
            updateWidgetFromInternet();
        } else {
            updateWidgetFromLocal();
        }
}

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        switch (action){
            case "com.lha.weather.USER_UPDATE":
                if (MyApplication.isConnected()){
                    updateWidgetFromInternet();
                    Toast.makeText(context,"更新中..",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context,"网络错误..",Toast.LENGTH_SHORT).show();
                }

                break;
            case "com.lha.weather.UPDATE_FROM_LOCAL":
                updateWidgetFromLocal();
                break;
            case "com.lha.weather.UPDATE_FROM_INTERNET":
                updateWidgetFromInternet();
                break;
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Intent intent = new Intent(context,UpdateService.class);
        intent.setAction("Widget");
        context.startService(intent);
        //alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 10 * 1000, pendingIntent);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Intent intent = new Intent(context,UpdateService.class);
        intent.setAction("Widget");
        context.startService(intent);
    }


    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        int i = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        updateWidget(appWidgetId, i);

    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }

    public  void updateWidget(int appWidgetId,int i){
        if (city_id == null){
            initCity();
        }
        if (city_id != null){
            if (jsonObject == null){
                jsonObject = FileHandle.getJSONObject(city_id);
            }
            if (jsonObject != null) {
                setWidgetViews(appWidgetId,jsonObject,i);
            }
        } else {
            initWidgetView(appWidgetId);
        }



    }


    public void getImage(final String url, final int appWidgetId,final RemoteViews views){
        HttpUtil.makeImageRequest(url, new ImageCallBack() {
            @Override
            public void onFinish(Bitmap bitmap) {
                setImageViewImage(bitmap, appWidgetId, url,views);
            }

            @Override
            public void onError() {

            }
        });

    }


    public  void setWidgetViews(int appWidgetId,JSONObject jsonObject,int a){
        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget4x2_layout);

        if (a == 0){
            a = appWidgetManager.getAppWidgetOptions(appWidgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        }
        if (a <= 100){
            views.setViewVisibility(R.id.forecast_layout,View.GONE);
            views.setViewVisibility(R.id.line, View.GONE);
            views.setTextViewTextSize(R.id.widget_time, TypedValue.COMPLEX_UNIT_SP, 35);
        } else{
            views.setViewVisibility(R.id.forecast_layout,View.VISIBLE);
            views.setViewVisibility(R.id.line,View.VISIBLE);
            views.setTextViewTextSize(R.id.widget_time, TypedValue.COMPLEX_UNIT_SP, 50);
        }
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        view_id[0] = R.id.weather;
        view_id[1] = R.id.aqi;
        view_id[2] = R.id.first_day_weather;
        view_id[3] = R.id.second_day_weather;
        view_id[4] = R.id.third_day_weather;
        view_id[5] = R.id.four_day_weather;
        view_id[6] = R.id.fifth_day_weather;
        view_id[7] = R.id.first_day_temp;
        view_id[8] = R.id.second_day_temp;
        view_id[9] = R.id.third_day_temp;
        view_id[10] = R.id.four_day_temp;
        view_id[11] = R.id.fifth_day_temp;

        SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        switch (defaultPreferences.getString("widget_color","透明")){
            case "蓝色":
                if (defaultPreferences.getBoolean("show_frame",false)){
                    views.setInt(R.id.widget_layout, "setBackgroundResource",R.drawable.blue_background_frame);
                } else {
                    views.setInt(R.id.widget_layout, "setBackgroundResource",R.drawable.widget_background);
                }
                break;
            case "透明":
                if (defaultPreferences.getBoolean("show_frame",false)){
                    views.setInt(R.id.widget_layout,"setBackgroundResource",R.drawable.touming_frame);
                } else {
                    views.setInt(R.id.widget_layout,"setBackgroundColor",Color.TRANSPARENT);
                }

                break;
        }
        switch (defaultPreferences.getString("widget_text_color","白色")){
            case "白色":
                for (int i = 0; i < 12; i++){
                    views.setTextColor(view_id[i],Color.WHITE);
                }
                views.setTextColor(R.id.city,Color.WHITE);
                views.setTextColor(R.id.update_time,Color.WHITE);
                views.setTextColor(R.id.widget_time,Color.WHITE);
                views.setTextColor(R.id.date,Color.WHITE);
                views.setTextColor(R.id.first_day,Color.WHITE);
                views.setTextColor(R.id.second_day,Color.WHITE);
                views.setTextColor(R.id.third_day,Color.WHITE);
                views.setTextColor(R.id.four_day,Color.WHITE);
                views.setTextColor(R.id.fifth_day,Color.WHITE);
                views.setInt(R.id.weather_image, "setColorFilter", Color.WHITE);
                views.setInt(R.id.line, "setBackgroundColor", Color.parseColor("#32ffffff"));
                break;
            case "黑色":
                for (int i = 0; i < 12; i++){
                    views.setTextColor(view_id[i],Color.BLACK);
                }
                views.setTextColor(R.id.city,Color.BLACK);
                views.setTextColor(R.id.update_time,Color.BLACK);
                views.setTextColor(R.id.widget_time,Color.BLACK);
                views.setTextColor(R.id.date,Color.BLACK);
                views.setTextColor(R.id.first_day,Color.BLACK);
                views.setTextColor(R.id.second_day,Color.BLACK);
                views.setTextColor(R.id.third_day,Color.BLACK);
                views.setTextColor(R.id.four_day,Color.BLACK);
                views.setTextColor(R.id.fifth_day,Color.BLACK);
                views.setInt(R.id.line,"setBackgroundColor",Color.BLACK);
                views.setInt(R.id.weather_image, "setColorFilter", Color.BLACK);

                break;
        }

        HandleJSON jsonHandle = new HandleJSON(jsonObject);
        widget_strings = jsonHandle.getWidget_strings();
        for (int i = 0; i < 12; i++){
            if (widget_strings[i] != null && !widget_strings[i].isEmpty()){
                if (i == 0){
                    views.setTextViewText(view_id[i],widget_strings[i] + "   " + widget_strings[12]);
                }else {
                    views.setTextViewText(view_id[i],widget_strings[i]);
                }
            }
        }
        String _week = "";

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        if (week == 1){
            _week = "周日";
            views.setTextViewText(R.id.third_day,"周二");
            views.setTextViewText(R.id.four_day,"周三");
            views.setTextViewText(R.id.fifth_day,"周四");
        } else if (week == 2){
            _week = "周一";
            views.setTextViewText(R.id.third_day,"周三");
           views.setTextViewText(R.id.four_day,"周四");
            views.setTextViewText(R.id.fifth_day,"周五");
        }else if (week == 3){
            _week = "周二";
            views.setTextViewText(R.id.third_day,"周四");
            views.setTextViewText(R.id.four_day,"周五");
            views.setTextViewText(R.id.fifth_day,"周六");
        }else if (week == 4){
            _week = "周三";
            views.setTextViewText(R.id.third_day,"周五");
            views.setTextViewText(R.id.four_day,"周六");
            views.setTextViewText(R.id.fifth_day,"周日");
        }else if (week == 5){
            _week = "周四";
            views.setTextViewText(R.id.third_day,"周六");
            views.setTextViewText(R.id.four_day,"周日");
            views.setTextViewText(R.id.fifth_day,"周一");
        }else if (week == 6){
            _week = "周五";
            views.setTextViewText(R.id.third_day,"周日");
            views.setTextViewText(R.id.four_day,"周一");
            views.setTextViewText(R.id.fifth_day,"周二");
        }else if (week == 7){
            _week = "周六";
            views.setTextViewText(R.id.third_day,"周一");
            views.setTextViewText(R.id.four_day,"周二");
            views.setTextViewText(R.id.fifth_day,"周三");
        }
        views.setTextViewText(R.id.first_day,"今天");
        views.setTextViewText(R.id.second_day,"明天");
        CalendarUtil calendarUtil = new CalendarUtil();
        String chineseDay = calendarUtil.getChineseMonth(year,month,day) + calendarUtil.getChineseDay(year,month,day);
        views.setTextViewText(R.id.date,month + "月" + day + "日  " + _week);
        views.setTextViewText(R.id.city,city);
        views.setTextViewText(R.id.update_time,chineseDay);
        String h = hour + "";
        String m = minute + "";
        if (hour < 10){
            h = "0" + h;
        }
        if (minute < 10){
            m = "0" + m;
        }
        views.setTextViewText(R.id.widget_time,h + ":" + m);
        Intent updateIntent = new Intent("com.lha.weather.USER_UPDATE");
        PendingIntent updatePi = PendingIntent.getBroadcast(context, 0, updateIntent, 0);
        views.setOnClickPendingIntent(R.id.update_layout, updatePi);

        Intent activityIntent = new Intent(context,MainActivity.class);
        PendingIntent activityPi = PendingIntent.getActivity(context, 0, activityIntent, 0);
        views.setOnClickPendingIntent(R.id.weather_layout, activityPi);
        views.setOnClickPendingIntent(R.id.forecast_layout,activityPi);


        Intent clockIntent;
        String clockPackageName = sharedPreferences.getString("clockPackageName","");
        String packageName;
        if (clockPackageName.equals("")){
            List<PackageInfo> packageInfo = packageManager.getInstalledPackages(PackageManager.MATCH_DEFAULT_ONLY);
            for (int i = 0; i < packageInfo.size(); i++){
                packageName = packageInfo.get(i).packageName;
                if (packageName.contains("clock")){
                    clockPackageName = packageName;
                    sharedPreferences.edit().putString("clockPackageName",clockPackageName);
                }
            }
            sharedPreferences.edit().apply();
        }
        if (!clockPackageName.equals("")){
            clockIntent = packageManager.getLaunchIntentForPackage(clockPackageName);
            PendingIntent clockPi = PendingIntent.getActivity(context, 0, clockIntent, 0);
            views.setOnClickPendingIntent(R.id.widget_time, clockPi);
        }

        if (weatherObject == null){
            try {
                weatherObject = new JSONObject(weatherCode);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            String code = weatherObject.getString(widget_strings[0]);
            String url = "http://files.heweather.com/cond_icon/" + code + ".png";
            String fileName = url.replace("/","").replace(".","").replace(":", "");
            Bitmap bitmap = FileHandle.getImage(fileName);

            if (bitmap == null){
                getImage(url, appWidgetId, views);
            }
            else {
                views.setImageViewBitmap(R.id.weather_image, bitmap);

            }
        } catch (Exception e){
            e.printStackTrace();
        }
        appWidgetManager.updateAppWidget(appWidgetId, views);


    }


    public  void updateWidgetFromLocal(){
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,Widget4x2.class));
        if (appWidgetIds.length > 0){
            for (int i:appWidgetIds){
                updateWidget(i,0);
            }
        }
    }

    public void updateWidgetFromInternet(){
        if (MyApplication.isConnected()){
            if (city_id == null){
                initCity();
            }
            if (city_id != null){
                String url = "http://aider.meizu.com/app/weather/listWeather?cityIds=" + city_id;
                final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,Widget4x2.class));
                final boolean b = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("show_notification",false);
                if (appWidgetIds.length != 0 || b){
                    HttpUtil.makeBaiduHttpRequest(url, new CallBackListener() {
                        @Override
                        public void onFinish(JSONObject jsonObject) {

                            for (int in : appWidgetIds){
                                setWidgetViews(in,jsonObject,0);
                            }
                            //context.sendBroadcast(new Intent("com.lha.weather.UPDATE_FROM_LOCAL"));
                            if (b){
                                WeatherNotification.sendNotification(jsonObject,city);
                            }
                            FileHandle.saveJSONObject(jsonObject, city_id);
                        }

                        @Override
                        public void onError(String e) {

                        }
                    });
                }
            }
        }  else {
            updateWidgetFromLocal();
        }

    }

    public void initCity(){
        CityDataBase cityDataBase = CityDataBase.getInstance();
        SQLiteDatabase db = cityDataBase.getWritableDatabase();
        Cursor cursor = db.query("city",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            city_id =  cursor.getString(cursor.getColumnIndex("city_id"));
            city =  cursor.getString(cursor.getColumnIndex("city"));
        }
        cursor.close();
    }


    public void setImageViewImage(Bitmap bitmap,int appWidgetId,String fileName,RemoteViews views){
        views.setImageViewBitmap(R.id.weather_image, bitmap);

        appWidgetManager.updateAppWidget(appWidgetId, views);
        String name = fileName.replace("/","").replace(".","").replace(":","");
        FileHandle.saveImage(bitmap,name);
    }


    public void initWidgetView(int appWidgetId){
        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget4x2_layout);
        views.setTextViewText(R.id.widget_time, "");
        views.setTextViewText(R.id.city, "");
        views.setTextViewText(R.id.update_time, "");
        views.setTextViewText(R.id.aqi, "");
        views.setTextViewText(R.id.weather,"");
        views.setTextViewText(R.id.first_day_weather, "");
        views.setTextViewText(R.id.second_day_weather, "");
        views.setTextViewText(R.id.third_day_weather, "");
        views.setTextViewText(R.id.four_day_weather, "");
        views.setTextViewText(R.id.fifth_day_weather, "");
        views.setTextViewText(R.id.first_day_temp, "");
        views.setTextViewText(R.id.second_day_temp, "");
        views.setTextViewText(R.id.third_day_temp, "");
        views.setTextViewText(R.id.four_day_temp,"");
        views.setTextViewText(R.id.fifth_day_temp, "");
        views.setTextViewText(R.id.third_day, "");
        views.setTextViewText(R.id.four_day, "");
        views.setTextViewText(R.id.fifth_day, "");
        views.setTextViewText(R.id.second_day, "");
        views.setTextViewText(R.id.date,"未找到已添加城市...");
        appWidgetManager.updateAppWidget(appWidgetId,views);
    }
}
