package com.example.l.myweather;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

public class UpdateBroadcast extends BroadcastReceiver {
    private Context mContext = MyApplication.getContext();
    private String city_id;

    public UpdateBroadcast() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        switch (intent.getAction()){
            case "com.lha.weather.UPDATE":
                CityDataBase cityDataBase = new CityDataBase(mContext,"CITY_LIST",null,1);
                SQLiteDatabase db = cityDataBase.getWritableDatabase();
                Cursor cursor = db.query("city", null, null, null, null, null, null);
                if (cursor.moveToFirst()){
                    city_id = cursor.getString(cursor.getColumnIndex("city_id"));
                    getDataFromInternet(city_id);
                }
                cursor.close();
                break;
            case "android.intent.action.BOOT_COMPLETED":
                Log.d("TAG","SYSTEM_BOOT");
                context.startService(new Intent(context,UpdateService.class));
                break;
        }

        //throw new UnsupportedOperationException("Not yet implemented");
    }


    public void getDataFromInternet(final String city_id){
        String url = "http://apis.baidu.com/showapi_open_bus/weather_showapi/address?&areaid=" + city_id + "&needMoreDay=1&needIndex=1";
        HttpUtil.makeBaiduHttpRequest(url, new CallBackListener() {
            @Override
            public void onFinish(JSONObject jsonObject) {
                HandleJson handleJson = new HandleJson();
                handleJson.handleJson(jsonObject);
                if (handleJson.getErr_code().equals("0")){
                    FileHandle.saveJSONObject(jsonObject, city_id);
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                    if (sharedPreferences.getBoolean("show_notification",false)) {
                        WeatherNotification.sendNotification(jsonObject);
                    }
                    Intent intent = new Intent("com.lha.weather.UPDATE_FROM_LOCAL");
                    mContext.sendBroadcast(intent);
                }

            }

            @Override
            public void onError(String e) {
                //JSONObject jsonObject = FileHandle.getJSONObject(city_id);
                //if (jsonObject != null){
                //    WeatherNotification.sendNotification(jsonObject);
                //}
            }
        });
    }
}
