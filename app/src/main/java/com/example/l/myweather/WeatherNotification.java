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
        import android.widget.RemoteViews;

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
    private static String weatherCode = "{\"晴\":\"100\",\"多云\":\"101\",\"阴\":\"104\",\"阵雨\":\"300\",\"雷阵雨\":\"302\",\"雷阵雨伴有冰雹\":\"304\",\"雨夹雪\":\"404\",\"小雨\":\"305\",\"小到中雨\":\"305\",\"中雨\":\"306\",\"中到大雨\":\"306\",\"大雨\":\"307\",\"大到暴雨\":\"307\",\"暴雨\":\"310\",\"大暴雨\":\"311\",\"特大暴雨\":\"312\",\"阵雪\":\"407\",\"小雪\":\"400\",\"中雪\":\"401\",\"大雪\":\"402\",\"暴雪\":\"403\",\"雾\":\"501\",\"冻雨\":\"313\",\"沙尘暴\":\"507\",\"浮尘\":\"504\",\"扬沙\":\"503\",\"霾\":\"502\",\"强沙尘暴\":\"508\"}";

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
            RemoteViews views = new RemoteViews(mContext.getPackageName(),R.layout.notify_layout);
            HandleJSON jsonHandle= new HandleJSON(jsonObject);
            int[] views_id = new int[3];
            views_id[0] = R.id.temp;
            views_id[1] = R.id.weather;
            views_id[2] = R.id.aqi;
            String[] notify_strings = jsonHandle.getWidget2x1_strings();
            for (int i = 0; i < 3; i++){
                if (notify_strings[i] != null && !notify_strings[i].isEmpty()){
                    if (i == 2){
                        views.setTextViewText(views_id[i],"空气指数：" + notify_strings[i]);
                    } else {
                        views.setTextViewText(views_id[i],notify_strings[i]);
                    }

                }
            }
            views.setTextViewText(R.id.city, city + "  " + notify_strings[3]);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            String notify_string = sharedPreferences.getString("notify_background","系统默认底色");
            String notify_text_color = sharedPreferences.getString("notify_text_color","黑色");
            switch (notify_string){
                case "白色":
                    views.setInt(R.id.notify_layout, "setBackgroundColor", Color.WHITE);
                    break;
                case "黑色":
                    views.setInt(R.id.notify_layout, "setBackgroundColor", Color.BLACK);
                    break;
                case "系统默认底色":
                    views.setInt(R.id.notify_layout,"setBackgroundColor",Color.TRANSPARENT);
                    break;
            }
            switch (notify_text_color){
                case "白色":
                    for (int i = 0; i < 3; i++){
                        views.setTextColor(views_id[i],Color.WHITE);
                    }
                    views.setTextColor(R.id.city, Color.WHITE);
                    break;
                case "黑色":
                    for (int i = 0; i < 3; i++){
                        views.setTextColor(views_id[i],Color.BLACK);
                    }
                    views.setTextColor(R.id.city,Color.BLACK);
                    break;
            }

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
                String code = weatherObject.getString(notify_strings[1]);
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
                views.setImageViewBitmap(R.id.weather_image, bitmap);
                builder.setContent(views);
                Notification notification = builder.build();
                notification.flags = Notification.FLAG_NO_CLEAR;
                notificationManager.notify(1, notification);
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
