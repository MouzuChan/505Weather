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
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONObject;
import java.util.List;

/**
 * Created by L on 2015/10/19.
 */
public class NewAppWidget extends AppWidgetProvider {
    private Context context = MyApplication.getContext();
    //private PackageManager packageManager;
    //private int USER_UPDATE_FLAG = 0;
    private AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    private SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    public String city;
    public String city_id;

    private int[] views_id = new int[4];
    private String[] widget2x1_strings;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        updateFromLocal();
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        //Log.d("TAG", intent.getAction());
        switch (intent.getAction()){
            case "com.lha.weather.UPDATE_FROM_LOCAL":
                updateFromLocal();
                break;
            case "com.lha.weather.USER_UPDATE":
                String data = intent.getStringExtra("data");
                String city = intent.getStringExtra("city");
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    setWidgetViews(city,jsonObject);
                }catch (Exception e){
                    e.printStackTrace();
                }
                Log.d("newAppwidget","user update");
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
        Intent intent = new Intent(context,UpdateService.class);
        intent.setAction("Widget");
        context.startService(intent);
    }

    public void setWidgetViews(String city,JSONObject jsonObject){
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context,NewAppWidget.class));
        if (appWidgetIds.length > 0){
            //packageManager = context.getPackageManager();
            TimeAndDate timeAndDate = new TimeAndDate();
            String _week = timeAndDate.getTodayWeek();
            int month = timeAndDate.getMonth();
            int day = timeAndDate.getDay();
            String chineseDay = timeAndDate.getChineseDay();
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
            HandleJSON jsonHandle = new HandleJSON(jsonObject);
            widget2x1_strings = jsonHandle.getWidget2x1_strings();

            views_id[0] = R.id.temp;
            views_id[1] = R.id.weather_txt;
            views_id[2] = R.id.aqi;
            views_id[3] = R.id.update_time;

            for (int i = 0; i < 4; i++){
                if (widget2x1_strings[i] != null && !widget2x1_strings[i].isEmpty()){
                    if (i == 2){
                        views.setTextViewText(views_id[i],widget2x1_strings[i] + " " + widget2x1_strings[6]);
                    }else {
                        views.setTextViewText(views_id[i],widget2x1_strings[i]);
                    }
                }
            }
            views.setTextViewText(R.id.high_low_temp,widget2x1_strings[5] + "\n" + widget2x1_strings[4]);
            switch (sharedPreferences.getString("widget_text_color","白色")){
                case "白色":
                    for (int i = 0; i < 3; i++){
                        views.setTextColor(views_id[i],Color.WHITE);
                    }
                    views.setTextColor(R.id.city,Color.WHITE);
                    views.setTextColor(R.id.date,Color.WHITE);
                    views.setTextColor(R.id.chinese_calendar,Color.WHITE);
                    views.setTextColor(R.id.update_time, Color.parseColor("#b4FFFFFF"));
                    views.setTextColor(R.id.high_low_temp,Color.parseColor("#b4FFFFFF"));
                    break;
                case "黑色":
                    for (int i = 0; i < 3; i++){
                        views.setTextColor(views_id[i],Color.BLACK);
                    }
                    views.setTextColor(R.id.high_low_temp,Color.parseColor("#b4000000"));
                    views.setTextColor(R.id.update_time,Color.parseColor("#b4000000"));
                    views.setTextColor(R.id.city,Color.BLACK);
                    views.setTextColor(R.id.date,Color.BLACK);
                    views.setTextColor(R.id.chinese_calendar,Color.BLACK);
                    break;
            }




            views.setTextViewText(R.id.city,city);
            views.setTextViewText(R.id.date, month + "/" + day + "  " + _week);
            views.setTextViewText(R.id.chinese_calendar,"农历" +  chineseDay);

            /*Intent intent = new Intent(context,MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.weather_layout, pendingIntent);
            List<PackageInfo> packageInfo = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
            Intent intent2 = null;
            String packageName;

            for (int a = 0; a < packageInfo.size(); a++){
                packageName = packageInfo.get(a).packageName;
                if (packageName.contains("clock")){
                    intent2 = packageManager.getLaunchIntentForPackage(packageName);
                }
            }
            if (intent2 != null){
                PendingIntent pi = PendingIntent.getActivity(context,0,intent2,0);
                views.setOnClickPendingIntent(R.id.clock_layout,pi);
            } else {
                //Toast.makeText(context,"未找到时钟应用",Toast.LENGTH_SHORT).show();
            }*/
            String datePackageName = sharedPreferences.getString("click_date_event","");
            String weatherPackageName = sharedPreferences.getString("click_weather_event","");

            if (datePackageName.length() == 0){
                datePackageName = "com.android.calendar";
            }
            if (weatherPackageName.length() == 0){
                weatherPackageName = "com.lha.weather";
            }
            PackageManager packageManager = context.getPackageManager();
            Intent dateIntent = packageManager.getLaunchIntentForPackage(datePackageName);
            if (dateIntent != null){
                PendingIntent clockPi = PendingIntent.getActivity(context, 0, dateIntent, 0);
                views.setOnClickPendingIntent(R.id.clock_layout, clockPi);
            }
            Intent weatherIntent = packageManager.getLaunchIntentForPackage(weatherPackageName);
            if (weatherIntent != null){
                PendingIntent weatherPi = PendingIntent.getActivity(context,0,weatherIntent,0);
                views.setOnClickPendingIntent(R.id.weather_layout,weatherPi);
            }
            Intent updateIntent = new Intent(context,UpdateService.class);
            updateIntent.setAction("user_update");
            PendingIntent updatePi = PendingIntent.getService(context,0,updateIntent,0);
            views.setOnClickPendingIntent(R.id.update_time, updatePi);

            switch (sharedPreferences.getString("widget_color","透明")){
                case "蓝色":
                    views.setInt(R.id.relative_layout, "setBackgroundResource",R.drawable.widget_background);
                    break;
                case "透明":
                    views.setInt(R.id.relative_layout,"setBackgroundColor",Color.TRANSPARENT);
                    break;
                case "半透黑":
                    views.setInt(R.id.relative_layout, "setBackgroundResource",R.drawable.widget_background_2);
                    break;
                case "透明（带边框）":
                    views.setInt(R.id.relative_layout, "setBackgroundResource",R.drawable.touming_frame);
                    break;
                case "半透白":
                    views.setInt(R.id.relative_layout, "setBackgroundResource",R.drawable.widget_background_3);
                    break;

                default:
                    break;
            }
            appWidgetManager.updateAppWidget(appWidgetIds, views);
        }
    }




    public void updateFromLocal(){
        initCity();
        if (city_id != null){
            JSONObject jsonObject = FileHandle.getJSONObject(city_id);
            if (jsonObject != null){
                setWidgetViews(city,jsonObject);
            }
        }

    }

    /*public void updateFromInternet(){
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        if (city_id == null){
            initCity();
        }
        if (city_id != null){
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,NewAppWidget.class));
            if (appWidgetIds.length != 0){
                if (MyApplication.isConnected()){
                    String url = "http://aider.meizu.com/app/weather/listWeather?cityIds=" + city_id;
                    HttpUtil.makeHttpRequest(url, new CallBackListener() {
                        @Override
                        public void onFinish(JSONObject jsonObject) {
                            setWidgetViews(city,jsonObject);
                            if (sharedPreferences.getBoolean("show_notification",false)){
                                WeatherNotification.sendNotification(jsonObject,city);
                            }
                            FileHandle.saveJSONObject(jsonObject, city_id);
                        }

                        @Override
                        public void onError(String e) {
                            //if (USER_UPDATE_FLAG == 1) {
                            //    Toast.makeText(context, "网络超时...", Toast.LENGTH_SHORT).show();
                           //     USER_UPDATE_FLAG = 0;
                           // }
                        }
                    });
                }
            }
        }
    }*/



    public void initCity() {

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
