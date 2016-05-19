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
        import android.os.Build;
        import android.preference.PreferenceManager;
        import android.widget.RemoteViews;

        import org.json.JSONObject;

        import java.util.Calendar;
        import java.util.HashMap;
        import java.util.Map;


/**
 * Created by L on 2016-01-06.
 */
public class WeatherNotification {
    private static String city_id;
    private static Context mContext = MyApplication.getContext();
    private static NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    private static String city;
    //private static Map<String,Integer> map_day = new HashMap<>();
    //private static Map<String,Integer> map_night = new HashMap<>();
    //private static Map<String,Integer> map_day_small = new HashMap<>();
    //private static Map<String,Integer> map_night_small = new HashMap<>();
    //private static String weatherCode = "{\"晴\":\"100\",\"多云\":\"101\",\"阴\":\"104\",\"阵雨\":\"300\",\"雷阵雨\":\"302\",\"雷阵雨伴有冰雹\":\"304\",\"雨夹雪\":\"404\",\"小雨\":\"305\",\"小到中雨\":\"305\",\"中雨\":\"306\",\"中到大雨\":\"306\",\"大雨\":\"307\",\"大到暴雨\":\"307\",\"暴雨\":\"310\",\"大暴雨\":\"311\",\"特大暴雨\":\"312\",\"阵雪\":\"407\",\"小雪\":\"400\",\"中雪\":\"401\",\"大雪\":\"402\",\"暴雪\":\"403\",\"雾\":\"501\",\"冻雨\":\"313\",\"沙尘暴\":\"507\",\"浮尘\":\"504\",\"扬沙\":\"503\",\"霾\":\"502\",\"强沙尘暴\":\"508\"}";

    //private static JSONObject weatherObject;

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
                        views.setTextViewText(views_id[i]," " + notify_strings[i] + " " + notify_strings[6]);
                    } else if (i == 1) {
                        views.setTextViewText(views_id[i],notify_strings[i] + "   " +  notify_strings[4] + "/" + notify_strings[5]);
                    }else {
                        views.setTextViewText(views_id[i],notify_strings[i]);
                    }

                }
            }
            switch (notify_strings[6]){
                case "优":
                    views.setImageViewResource(R.id.aqi_icon,R.drawable.tree_leaf_1);
                    break;
                case "良":
                    views.setImageViewResource(R.id.aqi_icon,R.drawable.tree_leaf_2);
                    break;
                case "轻度污染":
                    views.setImageViewResource(R.id.aqi_icon,R.drawable.tree_leaf_3);
                    break;
                case "中度污染":
                    views.setImageViewResource(R.id.aqi_icon,R.drawable.tree_leaf_4);
                    break;
                case "重度污染":
                    views.setImageViewResource(R.id.aqi_icon,R.drawable.tree_leaf_5);
                    break;
                case "严重污染":
                    views.setImageViewResource(R.id.aqi_icon,R.drawable.tree_leaf_6);
                    break;
                default:
                    views.setImageViewResource(R.id.aqi_icon,R.drawable.tree_leaf_1);
                    break;
            }
            views.setTextViewText(R.id.city, city + "  " + notify_strings[3]);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            String icon_style = sharedPreferences.getString("icon_style","单色");
            String notify_background = sharedPreferences.getString("notify_background","系统默认底色");
            String notify_text_color = sharedPreferences.getString("notify_text_color","黑色");
            switch (notify_background){
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
                    if (icon_style.equals("单色")){
                        views.setInt(R.id.weather_image,"setColorFilter",Color.WHITE);
                    }else {
                        views.setInt(R.id.weather_image,"setColorFilter",Color.TRANSPARENT);
                    }
                    break;
                case "黑色":
                    for (int i = 0; i < 3; i++){
                        views.setTextColor(views_id[i],Color.BLACK);
                    }
                    views.setTextColor(R.id.city,Color.BLACK);
                    if (icon_style.equals("单色")){
                        views.setInt(R.id.weather_image,"setColorFilter",Color.DKGRAY);
                    }else {
                        views.setInt(R.id.weather_image,"setColorFilter",Color.TRANSPARENT);
                    }
                    break;
                default:
                    if (icon_style.equals("单色")){
                        views.setInt(R.id.weather_image,"setColorFilter",Color.DKGRAY);
                    } else {
                        views.setInt(R.id.weather_image,"setColorFilter",Color.TRANSPARENT);
                    }
                    break;
            }

            Intent intent = new Intent(mContext,MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
            builder.setContentIntent(pendingIntent);
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            WeatherToCode weatherToCode = WeatherToCode.newInstance();
            int drawable_id = weatherToCode.getDrawableId(notify_strings[1],hour);
            int drawable_small_id = weatherToCode.getDrawableSmallId(notify_strings[1],hour);

            if (icon_style.equals("单色")){
                if (drawable_small_id != 0){
                    views.setImageViewResource(R.id.weather_image,drawable_small_id);
                }
            } else {
                if (drawable_id != 0){
                    views.setImageViewResource(R.id.weather_image,drawable_id);
                }
            }
            if (drawable_small_id != 0){
                builder.setSmallIcon(drawable_small_id);
            }
            builder.setContent(views);
            Notification notification = builder.build();
            notification.flags = Notification.FLAG_NO_CLEAR;
            notificationManager.notify(1, notification);
        }

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



    public static void sendNotification(String title,String content,int i){
        Notification.Builder builder = new Notification.Builder(mContext);
        builder.setContentText(content);
        builder.setContentTitle(title);
        builder.setSmallIcon(R.drawable.warning);
        Intent intent = new Intent(mContext,MainActivity.class);
        intent.putExtra("position",i);
        intent.setAction("notification");
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,i,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_SOUND;
        notificationManager.notify(i,notification);
    }

}
