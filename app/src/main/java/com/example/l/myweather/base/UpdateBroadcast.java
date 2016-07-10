package com.example.l.myweather.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UpdateBroadcast extends BroadcastReceiver {

    public UpdateBroadcast() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        switch (intent.getAction()){
            case "android.intent.action.BOOT_COMPLETED":
                Log.d("TAG","SYSTEM_BOOT");
                context.startService(new Intent(context,UpdateService.class));
                break;
            case "android.intent.action.TIME_SET":
                Intent intent1 = new Intent("com.lha.weather.UPDATE_FROM_LOCAL");
                context.sendBroadcast(intent1);
                break;
            case "android.intent.action.DATE_CHANGED":
                Intent intent2 = new Intent("com.lha.weather.UPDATE_FROM_LOCAL");
                context.sendBroadcast(intent2);
                break;
            /*case "com.lha.weather.UPDATE":
                Toast.makeText(context,"FFFFFFF",Toast.LENGTH_SHORT).show();
                //updateWidget();
                break; */

        }

        //throw new UnsupportedOperationException("Not yet implemented");
    }

    /*public void updateWidget(){
        Log.d("updateService","updateWidget");
        String city = "";
        String city_id = "";
        CityDataBase cityDataBase = CityDataBase.getInstance();
        SQLiteDatabase db = cityDataBase.getWritableDatabase();
        Cursor cursor = db.query("city",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            city_id =  cursor.getString(cursor.getColumnIndex("city_id"));
            city =  cursor.getString(cursor.getColumnIndex("city"));
        }
        cursor.close();
        if (city_id.length() > 0){
            String url = "http://aider.meizu.com/app/weather/listWeather?cityIds=" + city_id;
            final String finalCity_id = city_id;
            final String finalCity = city;
            HttpUtil.makeHttpRequest(url, new CallBackListener() {
                @Override
                public void onFinish(JSONObject jsonObject) {
                    //int[] widget4x2Ids = appWidgetManager.getAppWidgetIds(new ComponentName(context,Widget4x2.class));
                    //Widget4x2.setWidgetViews(finalCity,jsonObject);
                    //AppWidget2x1.setWidgetView(finalCity,jsonObject);
                    FileHandle.saveJSONObject(jsonObject, finalCity_id);
                    Context context = MyApplication.getContext();
                    SharedPreferences sharedPreferences = context.getSharedPreferences("settings",Context.MODE_MULTI_PROCESS);
                    if (sharedPreferences.getBoolean("show_notification",false)){
                        WeatherNotification.sendNotification(jsonObject,finalCity);
                    }
                    context.sendBroadcast(new Intent("com.lha.weather.UPDATE_FROM_LOCAL"));
                }

                @Override
                public void onError(String e) {

                }
            });
        }
    }*/

}
