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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

/**
 * Created by L on 2015-12-30.
 */
public class Widget4x2 extends AppWidgetProvider{

    private static Context context = MyApplication.getContext();
    private static int USER_UPDATE_FLAG = 0;
    //private static AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    //private static Intent intent = new Intent("com.lha.weather.UPDATE_TIME");
    //private static PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
    public static AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    private static PackageManager packageManager = context.getPackageManager();
    //public static RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget4x2_layout);



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int i : appWidgetIds){
            updateWidget(i,appWidgetManager);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        switch (action){
            case "com.lha.weather.USER_UPDATE4x2":
                USER_UPDATE_FLAG = 1;
                updateWidgetFromInternet();
                break;
            case "com.lha.weather.UPDATE_TIME":
                setWidgetTime();
                break;
            case "com.lha.weather.UPDATE_FROM_LOCAL":
                updateWidgetFromLocal();
                break;
            case "com.lha.weather.UPDATE_FROM_INTERNET":
                updateWidgetFromInternet();
        }

        Log.d("TAG", action);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Intent intent = new Intent(context,UpdateService.class);
        context.startService(intent);

    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, NewAppWidget.class));
        if (!sharedPreferences.getBoolean("update_switch", false) || appWidgetIds.length == 0){
            Intent intent = new Intent(context,UpdateService.class);
            context.stopService(intent);
        }
        //alarmManager.cancel(pendingIntent);
    }

    public static void updateWidget(int appWidgetId,AppWidgetManager appWidgetManager){
        String city_id = getCityId();
        JSONObject jsonObject = null;
        if (city_id != null){
            jsonObject = getJSONObjectFromLocal(city_id);
        }
        if (jsonObject != null){
            setWidgetViews(appWidgetManager, appWidgetId, jsonObject);
        }
        //alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 10 * 1000, pendingIntent);
    }


    public static void getImage(final String url, final int appWidgetId,final RemoteViews views){
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


    public static void setWidgetViews(AppWidgetManager appWidgetManager,int appWidgetId,JSONObject jsonObject){
        Log.d("Widget4x2","setWidgetViews");
        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget4x2_layout);
        Calendar calendar = Calendar.getInstance();
        HandleJson handleJson = new HandleJson();
        handleJson.handleJson(jsonObject);
        String city = handleJson.getCity();
        String weather = handleJson.getWeather_txt() + handleJson.getTemp();
        String update_time = handleJson.getLoc_time();
        String aqi = handleJson.getAqi() + handleJson.getQlty()+ "    " + handleJson.getMax_tmp();
        String first_day_weather = handleJson.getSecond_weather();
        String second_day_weather = handleJson.getThird_weather();
        String third_day_weather = handleJson.getFour_weather();
        String four_day_weather = handleJson.getFifth_day_weather_txt();
        String fifth_day_weather = handleJson.getSixth_day_weather_txt();

        String first_day_temp = handleJson.getSecond_night_temp() + "°/" + handleJson.getSecond_day_temp() + "°";
        String second_day_temp = handleJson.getThird_night_temp() + "°/" + handleJson.getThird_day_temp() + "°";
        String third_day_temp = handleJson.getFour_night_temp() + "°/" + handleJson.getFour_day_temp() + "°";
        String four_day_temp = handleJson.getFifth_night_temp() + "°/" + handleJson.getFifth_day_temp() + "°";
        String fifth_day_temp = handleJson.getSixth_night_temp() + "°/" + handleJson.getSixth_day_temp() + "°";

        String _week = "";

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        if (week == 1){
            _week = "周日";
            views.setTextViewText(R.id.third_day,"周三");
            views.setTextViewText(R.id.four_day,"周四");
            views.setTextViewText(R.id.fifth_day,"周五");
        } else if (week == 2){
            _week = "周一";
            views.setTextViewText(R.id.third_day,"周四");
            views.setTextViewText(R.id.four_day,"周五");
            views.setTextViewText(R.id.fifth_day,"周六");
        }else if (week == 3){
            _week = "周二";
            views.setTextViewText(R.id.third_day,"周五");
            views.setTextViewText(R.id.four_day,"周六");
            views.setTextViewText(R.id.fifth_day,"周日");
        }else if (week == 4){
            _week = "周三";
            views.setTextViewText(R.id.third_day,"周六");
            views.setTextViewText(R.id.four_day,"周日");
            views.setTextViewText(R.id.fifth_day,"周一");
        }else if (week == 5){
            _week = "周四";
            views.setTextViewText(R.id.third_day,"周日");
            views.setTextViewText(R.id.four_day,"周一");
            views.setTextViewText(R.id.fifth_day,"周二");
        }else if (week == 6){
            _week = "周五";
            views.setTextViewText(R.id.third_day,"周一");
            views.setTextViewText(R.id.four_day,"周二");
            views.setTextViewText(R.id.fifth_day,"周三");
        }else if (week == 7){
            _week = "周六";
            views.setTextViewText(R.id.third_day,"周二");
            views.setTextViewText(R.id.four_day,"周三");
            views.setTextViewText(R.id.fifth_day,"周四");
        }

        CalendarUtil calendarUtil = new CalendarUtil();
        String chineseDay = calendarUtil.getChineseMonth(year,month,day) + calendarUtil.getChineseDay(year,month,day);
        views.setTextViewText(R.id.date,month + "/" + day + "  " + _week + "   " + chineseDay);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String h = hour + "";
        String m = minute + "";
        if (hour < 10){
            h = "0" + h;
        }
        if (minute < 10){
            m = "0" + m;
        }
        views.setTextViewText(R.id.widget_time,h + ":" + m);
        views.setTextViewText(R.id.city,city);
        views.setTextViewText(R.id.update_time,update_time);
        views.setTextViewText(R.id.aqi,aqi);
        views.setTextViewText(R.id.weather,weather);
        views.setTextViewText(R.id.first_day_weather,first_day_weather);
        views.setTextViewText(R.id.second_day_weather,second_day_weather);
        views.setTextViewText(R.id.third_day_weather,third_day_weather);
        views.setTextViewText(R.id.four_day_weather,four_day_weather);
        views.setTextViewText(R.id.fifth_day_weather,fifth_day_weather);

        views.setTextViewText(R.id.first_day_temp,first_day_temp);
        views.setTextViewText(R.id.second_day_temp,second_day_temp);
        views.setTextViewText(R.id.third_day_temp,third_day_temp);
        views.setTextViewText(R.id.four_day_temp,four_day_temp);
        views.setTextViewText(R.id.fifth_day_temp, fifth_day_temp);

        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("widget_background",false)){
            views.setInt(R.id.widget_layout,"setBackgroundResource",R.drawable.widget_background);
            Log.d("background","widget");
        } else {
            views.setInt(R.id.widget_layout,"setBackgroundResource",R.drawable.touming_background);
            Log.d("background","touming");
        }

        Intent updateIntent = new Intent("com.lha.weather.USER_UPDATE4x2");
        PendingIntent updatePi = PendingIntent.getBroadcast(context, 0, updateIntent, 0);
        views.setOnClickPendingIntent(R.id.refresh_layout, updatePi);

        Intent activityIntent = new Intent(context,MainActivity.class);
        PendingIntent activityPi = PendingIntent.getActivity(context, 0, activityIntent, 0);
        views.setOnClickPendingIntent(R.id.weather_layout, activityPi);

        SharedPreferences sharedPreferences = context.getSharedPreferences("packageName",context.MODE_APPEND);

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
        String weather_pic = handleJson.getWeather_pic();
        String fileName = weather_pic.replace("/","").replace(".","").replace(":", "");
        Bitmap bitmap = FileHandle.getImage(fileName);
        if (bitmap == null){
            getImage(weather_pic, appWidgetId,views);
        }
        else {
            views.setImageViewBitmap(R.id.weather_image,bitmap);
        }



        appWidgetManager.updateAppWidget(appWidgetId,views);

    }


    public static void updateWidgetFromLocal(){
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,Widget4x2.class));
        if (appWidgetIds.length > 0){
            for (int in = 0; in < appWidgetIds.length; in++){
                updateWidget(appWidgetIds[in],appWidgetManager);
            }
        }
    }

    public static void updateWidgetFromInternet(){
        if (isConnected()){
            final String city_id = getCityId();
            if (city_id != null){
                String url = "http://apis.baidu.com/showapi_open_bus/weather_showapi/address?&areaid=" + city_id + "&needMoreDay=1&needIndex=1";
                HttpUtil.makeBaiduHttpRequest(url, new CallBackListener() {
                    @Override
                    public void onFinish(JSONObject jsonObject) {
                        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,Widget4x2.class));
                        if (appWidgetIds.length != 0){
                            for (int in = 0; in < appWidgetIds.length; in++){
                                setWidgetViews(appWidgetManager,appWidgetIds[in],jsonObject);
                            }
                        }
                        if (USER_UPDATE_FLAG == 1){
                            Toast.makeText(context,"更新成功^_^",Toast.LENGTH_SHORT).show();
                            USER_UPDATE_FLAG = 0;
                        }
                        FileHandle.saveJSONObject(jsonObject,city_id);
                    }

                    @Override
                    public void onError(String e) {
                        if (USER_UPDATE_FLAG == 1){
                            Toast.makeText(context,"网络超时⊙﹏⊙‖∣",Toast.LENGTH_SHORT).show();
                            USER_UPDATE_FLAG = 0;
                        }
                    }
                });

            }

        } else {
            if (USER_UPDATE_FLAG == 1){
                Toast.makeText(context,"没有网络@_@",Toast.LENGTH_SHORT).show();
                USER_UPDATE_FLAG = 0;
            }
        }

    }



    public static JSONObject getJSONObjectFromLocal(String city_id){
        return FileHandle.getJSONObject(city_id);

    }

    public static String getCityId(){
        CityDataBase cityDataBase = new CityDataBase(context,"CITY_LIST",null,1);
        SQLiteDatabase db = cityDataBase.getWritableDatabase();

        Cursor cursor = db.query("city",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            return cursor.getString(cursor.getColumnIndex("city_id"));
        }
        cursor.close();
        return null;
    }



    public static boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager)MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }



    public static  void setWidgetTime(){
        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget4x2_layout);
        //AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, Widget4x2.class));
        Calendar calendar = Calendar.getInstance();
        if (appWidgetIds.length != 0){
            for (int in = 0; in < appWidgetIds.length; in++){
                Log.d("TAG","UPDATE_WIDGET_TIME");
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                String h = hour + "";
                String m = minute + "";
                if (hour < 10){
                    h = "0" + h;
                }
                if (minute < 10){
                    m = "0" + m;
                }
                views.setTextViewText(R.id.widget_time,h + ":" + m);
                appWidgetManager.updateAppWidget(appWidgetIds[in], views);
            }
        }
    }

    static void setImageViewImage(Bitmap bitmap,int appWidgetId,String fileName,RemoteViews views){
        views.setImageViewBitmap(R.id.weather_image, bitmap);
        appWidgetManager.updateAppWidget(appWidgetId, views);

        String name = fileName.replace("/","").replace(".","").replace(":","");

        FileHandle.saveImage(bitmap,name);
    }


}
