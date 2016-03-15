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

        import android.graphics.Color;
        import android.preference.PreferenceManager;
        import android.util.Log;
        import android.widget.RemoteViews;
        import android.widget.Toast;

        import org.json.JSONObject;

        import java.util.Calendar;


/**
 * Created by L on 2016-01-06.
 */
public class WeatherNotification {
    private static String city_id;
    private static Context mContext = MyApplication.getContext();
    private static NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    private static String city;
    private static String weatherCode = "{\"晴\":\"100\",\"多云\":\"101\",\"阴\":\"104\",\"阵雨\":\"300\",\"雷阵雨\":\"302\",\"雷阵雨伴有冰雹\":\"304\",\"雨夹雪\":\"404\",\"小雨\":\"305\",\"中雨\":\"306\",\"大雨\":\"307\",\"暴雨\":\"310\",\"大暴雨\":\"311\",\"特大暴雨\":\"312\",\"阵雪\":\"407\",\"小雪\":\"400\",\"中雪\":\"401\",\"大雪\":\"402\",\"暴雪\":\"403\",\"雾\":\"501\",\"冻雨\":\"313\",\"沙尘暴\":\"507\",\"浮尘\":\"504\",\"扬沙\":\"503\",\"霾\":\"502\",\"强沙尘暴\":\"508\"}";

    private static JSONObject weatherObject;

    public static void sendNotification(JSONObject jsonObject,String cityName){
        Notification.Builder builder = new Notification.Builder(mContext);
        if (jsonObject == null){
            jsonObject = getJSONObject();
        }
        if (jsonObject != null){
            if (cityName != null){
                city = cityName;
            }
            RemoteViews views = new RemoteViews(mContext.getPackageName(),R.layout.notiy_layout);
            JSONHandle jsonHandle= new JSONHandle(jsonObject);
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int[] views_id = new int[3];
            views_id[0] = R.id.temp;
            views_id[1] = R.id.weather;
            views_id[2] = R.id.aqi;
            String[] notiy_strings = jsonHandle.getWidget2x1_strings(hour);
            for (int i = 0; i < 3; i++){
                if (notiy_strings[i] != null && !notiy_strings[i].isEmpty()){
                    if (i == 2){
                        views.setTextViewText(views_id[i],"空气指数：" + notiy_strings[i]);
                    } else {
                        views.setTextViewText(views_id[i],notiy_strings[i]);
                    }

                }
            }
            views.setTextViewText(R.id.city, city + "  " + notiy_strings[3]);
            Intent intent = new Intent(mContext,MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext,0,intent,0);
            builder.setContentIntent(pendingIntent);
            builder.setSmallIcon(R.drawable.ic_wb_cloudy_white_18dp);
            builder.setContent(views);
            Notification notification = builder.build();
            notification.flags = Notification.FLAG_NO_CLEAR;
            if (weatherObject == null){
                try {
                    weatherObject = new JSONObject(weatherCode);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            try {
                String code = weatherObject.getString(notiy_strings[1]);
                String url = "http://files.heweather.com/cond_icon/" + code + ".png";
                String fileName = url.replace("/","").replace(".","").replace(":", "");
                Bitmap bitmap = FileHandle.getImage(fileName);
                views.setInt(R.id.weather_image, "setColorFilter", Color.WHITE);
                if (bitmap == null){
                    getImage(url, views,builder);
                }
                else {
                    views.setImageViewBitmap(R.id.weather_image, bitmap);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
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
        CityDataBase cityDataBase = CityDataBase.getInstance();
        SQLiteDatabase db = cityDataBase.getWritableDatabase();
        Cursor cursor = db.query("city", null, null, null, null, null, null);
        JSONObject object = null;
        if (cursor.moveToFirst()){
            city_id = cursor.getString(cursor.getColumnIndex("city_id"));
            city = cursor.getString(cursor.getColumnIndex("city"));
            object = FileHandle.getJSONObject(city_id);
        }
        cursor.close();
        return object;
    }
    public static void cancelNotification(){
        notificationManager.cancelAll();
    }
}
