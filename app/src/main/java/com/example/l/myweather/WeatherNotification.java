package com.example.l.myweather;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONObject;


/**
 * Created by L on 2016-01-06.
 */
public class WeatherNotification {
    private static String city_id;
    private static Context mContext = MyApplication.getContext();
    private static NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    public static void sendNotification(JSONObject jsonObject){
        Notification.Builder builder = new Notification.Builder(mContext);
        if (jsonObject == null){
            jsonObject = getJSONObject();
        }
        if (jsonObject != null){
            RemoteViews views = new RemoteViews(mContext.getPackageName(),R.layout.notiy_layout);
            HandleJson handleJson = new HandleJson();
            handleJson.handleJson(jsonObject);
            String city = handleJson.getCity();
            String weather = handleJson.getWeather_txt();
            String weather_pic = handleJson.getWeather_pic();
            String temp = handleJson.getTemp();
            String max_min_temp = handleJson.getMax_tmp();
            String aqi = "空气指数  " + handleJson.getAqi() + " " +handleJson.getQlty();
            if(weather_pic != null){
                String fileName = weather_pic.replace("/","").replace(".","").replace(":", "");
                Bitmap bitmap = FileHandle.getImage(fileName);
                if (bitmap != null){
                    views.setImageViewBitmap(R.id.weather_image,bitmap);
                    views.setImageViewBitmap(R.id.weather_image,bitmap);
                } else {
                    getImage(weather_pic,views,builder);
                }

            }
            views.setTextViewText(R.id.aqi,aqi);
            views.setTextViewText(R.id.weather,weather);
            views.setTextViewText(R.id.city,city);
            views.setTextViewText(R.id.temp,temp);
            views.setTextViewText(R.id.max_temp, max_min_temp);
            Intent intent = new Intent(mContext,MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext,0,intent,0);
            builder.setContentIntent(pendingIntent);
            builder.setSmallIcon(R.drawable.ic_wb_cloudy_white_18dp);
            builder.setContent(views);
            Notification notification = builder.build();
            notification.flags = Notification.FLAG_NO_CLEAR;
            notificationManager.notify(1, notification);
        }
    }

    public static void getImage(String url, final RemoteViews views, final Notification.Builder builder){
        HttpUtil.makeImageRequest(url, new ImageCallBack() {
            @Override
            public void onFinish(Bitmap bitmap) {
                views.setImageViewBitmap(R.id.weather_image,bitmap);
                builder.setContent(views);
                Notification notification = builder.build();
                notification.flags = Notification.FLAG_NO_CLEAR;
                notificationManager.notify(1,notification);
            }

            @Override
            public void onError() {

            }
        });
    }

    public static JSONObject getJSONObject(){
        CityDataBase cityDataBase = new CityDataBase(mContext,"CITY_LIST",null,1);
        SQLiteDatabase db = cityDataBase.getWritableDatabase();
        Cursor cursor = db.query("city", null, null, null, null, null, null);
        JSONObject object = null;
        if (cursor.moveToFirst()){
            city_id = cursor.getString(cursor.getColumnIndex("city_id"));
            object = FileHandle.getJSONObject(city_id);
        }
        cursor.close();
        return object;
    }
    public static void cancelNotification(){
        notificationManager.cancelAll();
    }
}
