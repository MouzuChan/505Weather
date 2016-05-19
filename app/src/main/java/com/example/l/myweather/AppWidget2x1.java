package com.example.l.myweather;

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

import org.json.JSONObject;

import java.util.Calendar;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget2x1 extends AppWidgetProvider {

    private Context context = MyApplication.getContext();
    private AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        updateFromLocal();
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created

        if (sharedPreferences.getBoolean("update_switch", false)){
            Intent intent = new Intent(context,UpdateService.class);
            intent.setAction("Widget");
            context.startService(intent);
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Intent intent = new Intent(context,UpdateService.class);
        intent.setAction("Widget");
        context.startService(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        switch (intent.getAction()){
            case "com.lha.weather.UPDATE_FROM_LOCAL":
                updateFromLocal();
                break;
            case "com.lha.weather.USER_UPDATE":
                String data = intent.getStringExtra("data");
                String city = intent.getStringExtra("city");
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    setWidgetView(city,jsonObject);
                }catch (Exception e){
                    e.printStackTrace();
                }
                Log.d("appwidget2x1","user update");
                break;
        }
    }

    /*
    void updateFromInternet(){
        Context context = MyApplication.getContext();
        String city_id = "";
        String city = "";
        CityDataBase cityDataBase = CityDataBase.getInstance();
        SQLiteDatabase db = cityDataBase.getWritableDatabase();
        Cursor cursor = db.query("city",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            city_id =  cursor.getString(cursor.getColumnIndex("city_id"));
            city =  cursor.getString(cursor.getColumnIndex("city"));
        }
        cursor.close();
        if (!city_id.isEmpty()){
            String url = "http://aider.meizu.com/app/weather/listWeather?cityIds=" + city_id;
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, AppWidget2x1.class));
            if (appWidgetIds.length > 0){
                final String finalCity = city;
                HttpUtil.makeHttpRequest(url, new CallBackListener() {
                    @Override
                    public void onFinish(JSONObject jsonObject) {
                        setWidgetView(finalCity,jsonObject);
                    }

                    @Override
                    public void onError(String e) {

                    }
                });
            }
        }


    }
    */

    void updateFromLocal(){
        String city_id = "";
        String city = "";
        CityDataBase cityDataBase = CityDataBase.getInstance();
        SQLiteDatabase db = cityDataBase.getWritableDatabase();
        Cursor cursor = db.query("city",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            city_id =  cursor.getString(cursor.getColumnIndex("city_id"));
            city =  cursor.getString(cursor.getColumnIndex("city"));
        }
        cursor.close();
        if (!city_id.isEmpty()){
            JSONObject jsonObject = FileHandle.getJSONObject(city_id);
            if (jsonObject != null){
                setWidgetView(city,jsonObject);
            }
        }

    }

    void setWidgetView(String city,JSONObject jsonObject){
        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.app_widget2x1);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, AppWidget2x1.class));
        if (appWidgetIds.length > 0){
            HandleJSON handleJSON = new HandleJSON(jsonObject);
            String[] widget_strings = handleJSON.getWidget2x1_strings();
            views.setTextViewText(R.id.real_temp,widget_strings[0]);
            views.setTextViewText(R.id.weather,widget_strings[2] + " " + widget_strings[6] + "    " +  widget_strings[1]);
            views.setTextViewText(R.id.city,city);
            views.setTextViewText(R.id.high_low_temp,widget_strings[5] + "\n" + widget_strings[4]);
            views.setTextViewText(R.id.update_time,widget_strings[3]);

            String packageName = sharedPreferences.getString("click_weather_event","");
            if (packageName.isEmpty()){
                packageName = "com.lha.weather";
            }
            PackageManager packageManager = context.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            if (intent == null){
                intent = new Intent(context,UpdateService.class);
            }
            PendingIntent activityPi = PendingIntent.getActivity(context,0,intent,0);
            views.setOnClickPendingIntent(R.id.relative_layout, activityPi);

            Intent updateIntent = new Intent(context,UpdateService.class);
            updateIntent.setAction("user_update");
            PendingIntent updatePi = PendingIntent.getService(context,0,updateIntent,0);
            views.setOnClickPendingIntent(R.id.update_time, updatePi);
            switch (sharedPreferences.getString("widget_text_color","白色")){
                case "白色":
                    views.setTextColor(R.id.city,Color.WHITE);
                    views.setTextColor(R.id.weather,Color.WHITE);
                    views.setTextColor(R.id.real_temp,Color.WHITE);
                    views.setTextColor(R.id.update_time, Color.parseColor("#b4FFFFFF"));
                    views.setTextColor(R.id.high_low_temp,Color.parseColor("#b4FFFFFF"));
                    break;
                case "黑色":
                    views.setTextColor(R.id.city,Color.BLACK);
                    views.setTextColor(R.id.weather,Color.BLACK);
                    views.setTextColor(R.id.real_temp,Color.BLACK);
                    views.setTextColor(R.id.update_time, Color.parseColor("#b4000000"));
                    views.setTextColor(R.id.high_low_temp,Color.parseColor("#b4000000"));
                    break;
            }

            switch (sharedPreferences.getString("widget_color","透明")){
                case "蓝色":
                    views.setInt(R.id.relative_layout, "setBackgroundResource", R.drawable.widget_background);
                    break;
                case "透明":
                    views.setInt(R.id.relative_layout, "setBackgroundColor", Color.TRANSPARENT);
                    break;
                case "半透黑":
                    views.setInt(R.id.relative_layout, "setBackgroundResource", R.drawable.widget_background_2);
                    break;
                case "透明（带边框）":
                    views.setInt(R.id.relative_layout, "setBackgroundResource", R.drawable.touming_frame);
                    break;
                case "半透白":
                    views.setInt(R.id.relative_layout, "setBackgroundResource", R.drawable.widget_background_3);
                    break;
                default:
                    break;
            }
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            WeatherToCode weatherToCode = WeatherToCode.newInstance();
            int drawable_id;
            switch (sharedPreferences.getString("icon_style","单色")){
                case "单色":
                    drawable_id = weatherToCode.getDrawableSmallId(widget_strings[1],hour);
                    break;
                case "彩色":
                    drawable_id = weatherToCode.getDrawableId(widget_strings[1],hour);
                    break;
                default:
                    drawable_id = weatherToCode.getDrawableSmallId(widget_strings[1],hour);
                    break;
            }
            if (drawable_id != 0){
                views.setImageViewResource(R.id.weather_image,drawable_id);
            }



            appWidgetManager.updateAppWidget(appWidgetIds,views);
        }


    }

}

