package com.example.l.myweather.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.l.myweather.database.CityDataBase;
import com.example.l.myweather.MyApplication;
import com.example.l.myweather.R;
import com.example.l.myweather.ui.MainActivity;
import com.example.l.myweather.UpdateService;
import com.example.l.myweather.util.FileHandle;
import com.example.l.myweather.util.HandleJSON;
import com.example.l.myweather.util.TimeAndDate;
import com.example.l.myweather.util.WeatherToCode;

import org.json.JSONObject;

/**
 * Created by L on 2015-12-30.
 */
public class Widget4x2 extends AppWidgetProvider{

    public String city_id;
    public String city;
    private Context context = MyApplication.getContext();
    private AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    private int[] view_id = new int[12];
    private int[] week_view_id = new int[5];
    private String[] widget_strings;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        updateWidgetFromLocal();
}

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        switch (action){
            case "com.lha.weather.UPDATE_FROM_LOCAL":
                updateWidgetFromLocal();
                Log.d("update", "Local");
                break;
            /*case "android.intent.action.TIME_TICK":
                Log.d("update","update time");
                updateWidgetFromLocal();
                break;*/
            /*
            case "com.lha.weather.UPDATE_FROM_INTERNET":
                updateWidgetFromInternet();
                Log.d("update","internet");
                break;
            */
            case "com.lha.weather.USER_UPDATE":
                String data = intent.getStringExtra("data");
                String city = intent.getStringExtra("city");
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    setWidgetViews(city,jsonObject);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d("Widget","onEnabled");
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

    public void updateWidget(){
        initCity();
        if (city_id != null && !city_id.isEmpty()){
            JSONObject jsonObject = FileHandle.getJSONObject(city_id);
            if (jsonObject != null) {
                setWidgetViews(city,jsonObject);
            }
        }
        //else {
          //  initWidgetView();
        //}
    }


    public void setWidgetViews(String city,JSONObject jsonObject) {

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, Widget4x2.class));
        if (appWidgetIds.length > 0){
            //SharedPreferences sharedPreferences = context.getSharedPreferences("packageName", Context.MODE_APPEND);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget4x2_layout);
            week_view_id[0] = R.id.first_day;
            week_view_id[1] = R.id.second_day;
            week_view_id[2] = R.id.third_day;
            week_view_id[3] = R.id.four_day;
            week_view_id[4] = R.id.fifth_day;
            TimeAndDate timeAndDate = new TimeAndDate();
            String[] week_strings = timeAndDate.getFullWeek();
            week_strings[0] = "今天";
            week_strings[1] = "明天";
            for (int i = 0; i < 5; i++){
                views.setTextViewText(week_view_id[i],week_strings[i]);
            }


            String chineseDay = timeAndDate.getChineseDay();
            int month = timeAndDate.getMonth();
            int day = timeAndDate.getDay();
            String _week = timeAndDate.getTodayWeek();
            int hour = timeAndDate.getHour();
            int minute = timeAndDate.getMinute();


            views.setTextViewText(R.id.date,month + "/" + day + " " + _week);
            views.setTextViewText(R.id.city, city);
            views.setTextViewText(R.id.chinese_calendar, chineseDay);
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
                    views.setInt(R.id.widget_layout, "setBackgroundResource",R.drawable.widget_background);
                    break;
                case "透明":
                    views.setInt(R.id.widget_layout,"setBackgroundColor",Color.TRANSPARENT);
                    break;
                case "半透黑":
                    views.setInt(R.id.widget_layout, "setBackgroundResource",R.drawable.widget_background_2);
                    break;
                case "透明（带边框）":
                    views.setInt(R.id.widget_layout, "setBackgroundResource",R.drawable.touming_frame);
                    break;
                case "半透白":
                    views.setInt(R.id.widget_layout, "setBackgroundResource",R.drawable.widget_background_3);
                    break;
                default:
                    break;

            }
            int color;
            String icon_style = defaultPreferences.getString("icon_style","单色");
            switch (defaultPreferences.getString("widget_text_color","白色")){
                case "白色":
                    color = Color.WHITE;
                    views.setTextColor(R.id.update_time,Color.parseColor("#b4FFFFFF"));
                    views.setInt(R.id.line, "setBackgroundColor", Color.parseColor("#8CFFFFFF"));
                    if (icon_style.equals("单色")){
                        views.setInt(R.id.weather_image,"setColorFilter",Color.WHITE);
                    } else {
                        views.setInt(R.id.weather_image,"setColorFilter",Color.TRANSPARENT);
                    }
                    break;
                case "黑色":
                    color = Color.BLACK;
                    views.setTextColor(R.id.update_time,Color.parseColor("#b4000000"));
                    views.setInt(R.id.line, "setBackgroundColor", Color.parseColor("#8c000000"));
                    if (icon_style.equals("单色")){
                        views.setInt(R.id.weather_image,"setColorFilter",Color.BLACK);
                    }else {
                        views.setInt(R.id.weather_image,"setColorFilter",Color.TRANSPARENT);
                    }
                    break;
                default:
                    views.setInt(R.id.line, "setBackgroundColor", Color.parseColor("#8cffffff"));
                    views.setTextColor(R.id.update_time, Color.parseColor("#b4FFFFFF"));
                    color = Color.WHITE;
                    if (icon_style.equals("单色")){
                        views.setInt(R.id.weather_image,"setColorFilter",Color.WHITE);
                    }else {
                        views.setInt(R.id.weather_image,"setColorFilter",Color.TRANSPARENT);
                    }
                    break;
            }
            for (int i = 0; i < 12; i++){
                views.setTextColor(view_id[i],color);
            }
            views.setTextColor(R.id.city,color);
            views.setTextColor(R.id.widget_time,color);
            views.setTextColor(R.id.date,color);
            views.setTextColor(R.id.first_day,color);
            views.setTextColor(R.id.second_day,color);
            views.setTextColor(R.id.third_day,color);
            views.setTextColor(R.id.four_day,color);
            views.setTextColor(R.id.fifth_day, color);
            views.setTextColor(R.id.chinese_calendar,color);

            HandleJSON jsonHandle = new HandleJSON(jsonObject);
            widget_strings = jsonHandle.getWidget_strings();
            for (int i = 0; i < 12; i++){
                if (widget_strings[i] != null && !widget_strings[i].isEmpty()){
                    if (i == 0){
                        views.setTextViewText(view_id[i],widget_strings[i] + "  " + widget_strings[12]);
                    }else {
                        views.setTextViewText(view_id[i],widget_strings[i]);
                    }
                }
            }

            String h = hour + "";
            String m = minute + "";
            if (hour < 10){
                h = "0" + h;
            }
            if (minute < 10){
                m = "0" + m;
            }
            views.setTextViewText(R.id.update_time,widget_strings[13]);
            views.setTextViewText(R.id.widget_time, h + ":" + m);
            Intent updateIntent = new Intent(context,UpdateService.class);
            updateIntent.setAction("user_update");
            PendingIntent updatePi = PendingIntent.getService(context,0,updateIntent,0);
            views.setOnClickPendingIntent(R.id.update_time, updatePi);

            Intent activityIntent = new Intent(context,MainActivity.class);
            PendingIntent activityPi = PendingIntent.getActivity(context, 0, activityIntent, 0);
            //views.setOnClickPendingIntent(R.id.weather_layout, activityPi);
            views.setOnClickPendingIntent(R.id.forecast_layout,activityPi);

            //SharedPreferences package_preferences = context.getSharedPreferences("package_preferences",Context.MODE_MULTI_PROCESS);

            String clockPackageName = defaultPreferences.getString("click_time_event","");
            //String packageName;
            String weatherPackageName = defaultPreferences.getString("click_weather_event","");
            String datePackageName = defaultPreferences.getString("click_date_event","");

            if (clockPackageName.isEmpty()){
                clockPackageName = "com.android.deskclock";
            }
            if (weatherPackageName.isEmpty()){
                weatherPackageName = "com.lha.weather";
            }
            if (datePackageName.isEmpty()){
                datePackageName = "com.android.calendar";
            }
            PackageManager packageManager = context.getPackageManager();
            Intent clockIntent = packageManager.getLaunchIntentForPackage(clockPackageName);
            if (clockIntent != null){
                PendingIntent clockPi = PendingIntent.getActivity(context, 0, clockIntent, 0);
                views.setOnClickPendingIntent(R.id.widget_time, clockPi);
            }
            Intent weatherIntent = packageManager.getLaunchIntentForPackage(weatherPackageName);
            if (weatherIntent != null){
                PendingIntent weatherPi = PendingIntent.getActivity(context,0,weatherIntent,0);
                views.setOnClickPendingIntent(R.id.weather_layout,weatherPi);
            }
            Intent dateIntent = packageManager.getLaunchIntentForPackage(datePackageName);
            if (dateIntent != null){
                PendingIntent datePi = PendingIntent.getActivity(context,0,dateIntent,0);
                views.setOnClickPendingIntent(R.id.date,datePi);
                views.setOnClickPendingIntent(R.id.chinese_calendar,datePi);
            }

            WeatherToCode weatherToCode = WeatherToCode.newInstance();
            int drawable_id;
            switch (icon_style){
                case "单色":
                    drawable_id = weatherToCode.getDrawableSmallId(widget_strings[0],hour);
                    break;
                case "彩色":
                    drawable_id = weatherToCode.getDrawableId(widget_strings[0],hour);
                    break;
                default:
                    drawable_id = weatherToCode.getDrawableSmallId(widget_strings[0],hour);
                    break;
            }
            views.setImageViewResource(R.id.weather_image,drawable_id);
            appWidgetManager.updateAppWidget(appWidgetIds, views);
        }



    }


    public void updateWidgetFromLocal(){

        //if (appWidgetIds.length > 0){
            //for (int i:appWidgetIds){
                updateWidget();
           // }
        //}
    }

    /*public void updateWidgetFromInternet(){
        Log.d("Widget","Internet");
        if (MyApplication.isConnected()){
            if (city_id == null){
                initCity();
            }
            if (city_id != null){
                Context context = MyApplication.getContext();
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                String url = "http://aider.meizu.com/app/weather/listWeather?cityIds=" + city_id;
                final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,Widget4x2.class));
                final boolean b = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("show_notification",false);
                if (appWidgetIds.length != 0 || b){
                    HttpUtil.makeBaiduHttpRequest(url, new CallBackListener() {
                        @Override
                        public void onFinish(JSONObject jsonObject) {
                            //for (int in : appWidgetIds){
                            setWidgetViews(city,jsonObject);
                            //}
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

    }*/

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

}
