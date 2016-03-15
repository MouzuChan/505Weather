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
import android.preference.PreferenceManager;
//import android.util.Log;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
    public AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    private PackageManager packageManager = context.getPackageManager();
    private String city_id,city;
    private JSONObject jsonObject;
    private SharedPreferences sharedPreferences = context.getSharedPreferences("packageName",context.MODE_APPEND);
    private int[] view_id = new int[12];
    private String[] widget_strings;

    private String weatherCode = "{\"晴\":\"100\",\"多云\":\"101\",\"阴\":\"104\",\"阵雨\":\"300\",\"雷阵雨\":\"302\",\"雷阵雨伴有冰雹\":\"304\",\"雨夹雪\":\"404\",\"小雨\":\"305\",\"中雨\":\"306\",\"大雨\":\"307\",\"暴雨\":\"310\",\"大暴雨\":\"311\",\"特大暴雨\":\"312\",\"阵雪\":\"407\",\"小雪\":\"400\",\"中雪\":\"401\",\"大雪\":\"402\",\"暴雪\":\"403\",\"雾\":\"501\",\"冻雨\":\"313\",\"沙尘暴\":\"507\",\"浮尘\":\"504\",\"扬沙\":\"503\",\"霾\":\"502\",\"强沙尘暴\":\"508\"}";

    private JSONObject weatherObject;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        if (MyApplication.isConnected()){
            updateWidgetFromInternet();
        } else {
            updateWidgetFromLocal();
        }
        //alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 10 * 1000, pendingIntent);
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

    public  void updateWidget(int appWidgetId){
        if (city_id == null){
            initCity();
        }
        if (city_id != null){
            if (jsonObject == null){
                jsonObject = FileHandle.getJSONObject(city_id);
            }
            if (jsonObject != null) {
                setWidgetViews(appWidgetId,jsonObject);
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


    public  void setWidgetViews(int appWidgetId,JSONObject jsonObject){
        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget4x2_layout);
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



        JSONHandle jsonHandle = new JSONHandle(jsonObject);
        widget_strings = jsonHandle.getWidget_strings(hour);
        for (int i = 0; i < 12; i++){
            if (widget_strings[i] != null && !widget_strings[i].isEmpty()){
                if (i == 0){
                    views.setTextViewText(view_id[i],widget_strings[i] + "   " + widget_strings[12]);
                }else {
                    views.setTextViewText(view_id[i],widget_strings[i]);
                }
            }
        }
        views.setTextViewText(R.id.city,city);
        views.setTextViewText(R.id.update_time,widget_strings[13]);

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
        views.setTextViewText(R.id.date,month + "/" + day + " " + _week + " " + chineseDay);

        String h = hour + "";
        String m = minute + "";
        if (hour < 10){
            h = "0" + h;
        }
        if (minute < 10){
            m = "0" + m;
        }
        views.setTextViewText(R.id.widget_time,h + ":" + m);
        SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        switch (defaultPreferences.getString("widget_color","透明")){
            case "蓝色":
                views.setInt(R.id.widget_layout, "setBackgroundResource", R.drawable.widget_background);
                break;
            case "透明":
                views.setInt(R.id.widget_layout,"setBackgroundResource",R.drawable.touming_background);
                break;
        }
        Intent updateIntent = new Intent("com.lha.weather.USER_UPDATE4x2");
        PendingIntent updatePi = PendingIntent.getBroadcast(context, 0, updateIntent, 0);
        views.setOnClickPendingIntent(R.id.update_time, updatePi);

        Intent activityIntent = new Intent(context,MainActivity.class);
        PendingIntent activityPi = PendingIntent.getActivity(context, 0, activityIntent, 0);
        views.setOnClickPendingIntent(R.id.weather_layout, activityPi);


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
            views.setInt(R.id.weather_image, "setColorFilter", Color.WHITE);
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
        Log.d("Widget4x2","-->  updateWidgetFromLocal");
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,Widget4x2.class));
        if (appWidgetIds.length > 0){
            for (int i:appWidgetIds){
                updateWidget(i);
            }
        }
    }

    public void updateWidgetFromInternet(){
        if (MyApplication.isConnected()){
            if (city_id == null){
                initCity();
            }
            if (city_id != null){
                String url = "http://zhwnlapi.etouch.cn/Ecalender/api/v2/weather?citykey=" + city_id;
                HttpUtil.makeBaiduHttpRequest(url, new CallBackListener() {
                    @Override
                    public void onFinish(JSONObject jsonObject) {
                        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,Widget4x2.class));
                        if (appWidgetIds.length != 0){
                            for (int in = 0; in < appWidgetIds.length; in++){
                                setWidgetViews(appWidgetIds[in],jsonObject);
                            }
                        }
                        JSONHandle jsonHandle = new JSONHandle(jsonObject);
                        if (jsonHandle.getStatus_code() == 1000){
                            FileHandle.saveJSONObject(jsonObject,city_id);
                        }
                        if (USER_UPDATE_FLAG == 1){
                            Toast.makeText(context,"更新成功^_^",Toast.LENGTH_SHORT).show();
                            USER_UPDATE_FLAG = 0;
                        }
                        context.sendBroadcast(new Intent("com.lha.weather.UPDATE_FROM_LOCAL"));
                    }

                    @Override
                    public void onError(String e) {
                        if (USER_UPDATE_FLAG == 1){
                            Toast.makeText(context,"网络超时⊙﹏⊙‖∣",Toast.LENGTH_SHORT).show();
                            USER_UPDATE_FLAG = 0;
                        }
                    }
                });

            } else {
                updateWidgetFromLocal();
            }

        } else {
            if (USER_UPDATE_FLAG == 1){
                Toast.makeText(context,"没有网络@_@",Toast.LENGTH_SHORT).show();
                USER_UPDATE_FLAG = 0;
            }
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
