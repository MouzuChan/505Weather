package com.example.l.myweather;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateService extends Service {
    private int i = 0;
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
        Log.d("UpdateService","onCreate");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("UpdateService","onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("UpdateService", "onStartCommand");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String update_rate = sharedPreferences.getString("update_rate","");
        switch (update_rate){
            case "30分钟":
                i = 30 * 60 * 1000;
                break;
            case "1个小时":
                i = 60 * 60 * 1000;
                break;
            case "2个小时":
                i = 120* 60 * 1000;
                break;
            case "4个小时":
                i = 240 * 60 * 1000;
                break;
            case "6个小时":
                i = 360 * 60 * 1000;
                break;
        }
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.d("UpdateService","update");
                if (isConnected()){
                    Context context = MyApplication.getContext();
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(MyApplication.getContext());
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,NewAppWidget.class));
                    Log.d("Length",appWidgetIds.length +"");
                    if (appWidgetIds.length == 0){
                        UpdateService.this.stopSelf();
                    }
                    for (int in = 0; in < appWidgetIds.length; in++){
                        NewAppWidget.updateAppWidget(context,appWidgetManager,appWidgetIds[in]);
                    }
                }
            }
        };
        Timer timer = new Timer();
        if (i != 0){
            timer.schedule(timerTask,0,i);
        } else {
            stopSelf();
        }

        return super.onStartCommand(intent, flags, startId);
    }
    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
