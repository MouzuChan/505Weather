package com.example.l.myweather;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class UpdateService extends Service {
    private int i;
    private Context context = MyApplication.getContext();
    private int appWidgetIds[];
    private boolean updateSwitch = false;
    private boolean showNotification = false;
    private  AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    private Widget4x2 widget4x2;
    private SharedPreferences sharedPreferences;
    private String update_rate;
    private int m = 0;
    private int screen_off_m;
    private Date screen_off_date;
    private boolean alarm_notification = true;

    private ArrayList<String> city_list;
    private ArrayList<String> city_id_list;
    private SharedPreferences alarm_ids_shared;

    public UpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("UpdateService", "onCreate");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        alarm_ids_shared = context.getSharedPreferences("alarm_ids",MODE_APPEND);
        updateSwitch = sharedPreferences.getBoolean("update_switch", false);
        showNotification = sharedPreferences.getBoolean("show_notification", false);
        alarm_notification = sharedPreferences.getBoolean("alarm_notification",true);
        update_rate = sharedPreferences.getString("update_rate","1个小时");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(broadcastReceiver,intentFilter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("UpdateService", "onDestroy");
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.d("UpdateService", "onStartCommand");
        if(intent != null){
            String action = intent.getAction();
            if (action != null){
                switch (action){
                    case "user_update":
                        updateWidget();
                        break;
                    case "show_notification":
                        showNotification = intent.getBooleanExtra(action,false);
                        sharedPreferences.edit().putBoolean(action,showNotification).apply();
                        break;
                    case "notify_background":
                        sharedPreferences.edit().putString(action,intent.getStringExtra(action)).apply();
                        break;
                    case "notify_text_color":
                        sharedPreferences.edit().putString(action,intent.getStringExtra(action)).apply();
                        break;
                    case "widget_color":
                       sharedPreferences.edit().putString(action,intent.getStringExtra(action)).apply();
                        sendBroadcast(new Intent("com.lha.weather.UPDATE_FROM_LOCAL"));
                        break;
                    case "widget_text_color":
                        sharedPreferences.edit().putString(action,intent.getStringExtra(action)).apply();
                        sendBroadcast(new Intent("com.lha.weather.UPDATE_FROM_LOCAL"));
                        break;
                    case "update_rate":
                        update_rate = intent.getStringExtra(action);
                        sharedPreferences.edit().putString(action,update_rate).apply();

                        break;
                    case "update_switch":
                        updateSwitch = intent.getBooleanExtra(action,false);
                        sharedPreferences.edit().putBoolean(action,updateSwitch).apply();
                        break;
                    case "click_time_event":
                        sharedPreferences.edit().putString(action,intent.getStringExtra(action)).apply();
                        context.sendBroadcast(new Intent("com.lha.weather.UPDATE_FROM_LOCAL"));
                        break;
                    case "click_weather_event":
                        sharedPreferences.edit().putString(action,intent.getStringExtra(action)).apply();
                        context.sendBroadcast(new Intent("com.lha.weather.UPDATE_FROM_LOCAL"));
                        break;
                    case "click_date_event":
                        sharedPreferences.edit().putString(action,intent.getStringExtra(action)).apply();
                        context.sendBroadcast(new Intent("com.lha.weather.UPDATE_FROM_LOCAL"));
                        break;
                    case "icon_style":
                        sharedPreferences.edit().putString(action,intent.getStringExtra(action)).apply();
                        context.sendBroadcast(new Intent("com.lha.weather.UPDATE_FROM_LOCAL"));
                        break;
                    case "alarm_notification":
                        alarm_notification = intent.getBooleanExtra(action,true);
                        sharedPreferences.edit().putBoolean(action,alarm_notification).apply();
                        break;
                    default:
                        break;
                }
            }
        }
        if (showNotification){
            WeatherNotification.sendNotification(null,null);
        } else {
            WeatherNotification.cancelNotification();
        }
        initTimerTask();
        return START_STICKY;
    }


    public void initTimerTask(){
        appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,Widget4x2.class));
        int[] newWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, NewAppWidget.class));
        int[] appWidget2x1Ids = appWidgetManager.getAppWidgetIds(new ComponentName(context,AppWidget2x1.class));
        //showNotification = sharedPreferences.getBoolean("show_notification",false);
        switch (update_rate){
            case "30分钟":
                i = 30; // * 60 * 1000;
                break;
            case "1个小时":
                i = 60; // * 60 * 1000;
                break;
            case "2个小时":
                i = 120; //* 60 * 1000;
                break;
            case "4个小时":
                i = 240; // * 60 * 1000;
                break;
            case "6个小时":
                i = 360;// * 60 * 1000;
                break;
            default:
                i = 60;
                break;
        }
        if (updateSwitch || appWidgetIds.length > 0){
            //Log.d("updateService","yes");
            if (!(showNotification || newWidgetIds.length > 0 || appWidget2x1Ids.length > 0 || appWidgetIds.length > 0 || alarm_notification)){
                Log.d("updateService","stopSelf");
                stopSelf();
            }
        } else {
            stopSelf();
        }
    }

    public void updateWidget(){
        //String city = "";
        //String city_id = "";
        if (city_list == null){
            city_list = new ArrayList<>();
        }
        if (city_id_list == null){
            city_id_list = new ArrayList<>();
        }
        city_list.clear();
        city_id_list.clear();
        CityDataBase cityDataBase = CityDataBase.getInstance();
        SQLiteDatabase db = cityDataBase.getWritableDatabase();
        Cursor cursor = db.query("city",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                city_list.add(cursor.getString(cursor.getColumnIndex("city")));
                city_id_list.add(cursor.getString(cursor.getColumnIndex("city_id")));
            } while (cursor.moveToNext());
        }
       // if (city_list.size() > 0 && city_id_list.size() > 0){
            //city = city_list.get(0);
            //city_id = city_id_list.get(0);
       // }
        cursor.close();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < city_id_list.size(); i++){
            if (i == city_id_list.size() - 1){
                stringBuilder.append("cityIds=" + city_id_list.get(i));
            } else {
                stringBuilder.append("cityIds=" + city_id_list.get(i) + "&");
            }
        }
        if (city_id_list.size() > 0){
            String url = "http://aider.meizu.com/app/weather/listWeather?" + stringBuilder.toString();
            //final String finalCity_id = city_id;
           // final String finalCity = city;
            HttpUtil.makeHttpRequest(url, new CallBackListener() {
                @Override
                public void onFinish(JSONObject jsonObject) {
                    try {
                        if (jsonObject.getString("code").equals("200")){
                            m = 0;
                           // Log.d("BroadcastReceiver -->","m置0");
                            JSONArray value = jsonObject.getJSONArray("value");
                            JSONObject object;
                            JSONArray alarms;
                            for (int i = 0; i < city_id_list.size(); i++){
                                object = value.getJSONObject(i);
                                if (i == 0){
                                    if (showNotification){
                                        WeatherNotification.sendNotification(object,city_list.get(i));
                                    }
                                    Intent intent = new Intent("com.lha.weather.USER_UPDATE");
                                    intent.putExtra("data",object.toString());
                                    intent.putExtra("city",city_list.get(i));
                                    context.sendBroadcast(intent);
                                }
                                if (alarm_notification){
                                    if (object.has("alarms")){
                                        alarms = object.getJSONArray("alarms");
                                        if (alarms.length() > 0){
                                            JSONObject object1 = alarms.getJSONObject(0);
                                            String alarm_id = object1.getString("alarmId");
                                            if (!alarm_ids_shared.contains(alarm_id)){
                                                String content = object1.getString("alarmContent");
                                                String title = object1.getString("alarmTypeDesc");
                                                String level = object1.getString("alarmLevelNoDesc");
                                                WeatherNotification.sendNotification(level + title,content,i + 2);
                                                alarm_ids_shared.edit().putString(alarm_id,"F").apply();
                                            }
                                        }
                                    }
                                }
                                FileHandle.saveJSONObject(object, city_id_list.get(i));
                            }



                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }


                }
                @Override
                public void onError(String e) {

                }
            });
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,Widget4x2.class));
            switch (intent.getAction()){
                case Intent.ACTION_TIME_TICK:
                    if (appWidgetIds == null){
                        appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,Widget4x2.class));
                    }
                    if (appWidgetIds.length > 0){
                        if (widget4x2 == null){
                            widget4x2 = new Widget4x2();
                        }
                        widget4x2.updateWidget();
                    }
                    m++;
                    if (i == 0){
                        i = 30;
                    }
                    if (m >= i){
                        if (isConnected() && updateSwitch){
                            updateWidget();
                        }
                    }
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    screen_off_date = new Date();
                    screen_off_m = m;
                   // Log.d("screen_off","date -->" + screen_off_date.getTime());
                    break;
                case Intent.ACTION_SCREEN_ON:

                    //screen_on_time = screen_on_date.getTime();

                    if (screen_off_date != null){
                        Date screen_on_date = new Date();
                        long time = screen_on_date.getTime() - screen_off_date.getTime();
                        int minutes = (int)time / 1000 / 60;
                        m = minutes + screen_off_m;
                     //   Log.d("screen_on","compare -->" + minutes);
                    }
                  //  Log.d("screen_on","screen_on");
                    break;
            }

        }
    };






    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager)MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

}
