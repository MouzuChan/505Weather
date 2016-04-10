package com.example.l.myweather;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateService extends Service {
    private int i = 0;
    private SharedPreferences sharedPreferences;
    private static Context context = MyApplication.getContext();
    //private Timer timer;
    private Timer autoUpdateTimer;
    private int appWidgetIds[];
    private int newWidgetIds[];
    private ComponentName componentName = new ComponentName(context,Widget4x2.class);
    private boolean updateSwitch = false;
    private boolean showNotification = false;
    private  AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    private Timer updateTimeTimer;

    private TimerTask timerTask;
    private TimerTask updateTimeTimerTask;

    private int minute = -1;
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
        updateSwitch = sharedPreferences.getBoolean("update_switch", false);
        showNotification = sharedPreferences.getBoolean("show_notification", false);
        initTimerTask();
        if (showNotification){
            WeatherNotification.sendNotification(null,null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("UpdateService", "onDestroy");
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.d("UpdateService", "onStartCommand");
        if(intent != null){
            String action = intent.getAction();
            if (action != null){
                switch (action){
                    case "updateSwitch":
                        updateSwitch = intent.getBooleanExtra("updateSwitch",false);
                        initTimerTask();
                        break;
                    case "Widget":
                        initTimerTask();
                        break;
                    case "ChangeUpdateRate":
                        initTimerTask();
                        break;
                    default:
                        break;
                }
            }
        }
        return START_STICKY;
    }


    public void initTimerTask(){

        appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
        newWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, NewAppWidget.class));


        String update_rate = sharedPreferences.getString("update_rate","1个小时");
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


        if (timerTask != null){
            timerTask.cancel();
        }
        if (updateTimeTimerTask != null){
            updateTimeTimerTask.cancel();
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
                newWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,NewAppWidget.class));
                if (appWidgetIds.length > 0 || newWidgetIds.length > 0 || (updateSwitch && showNotification)){
                    Intent intent1 = new Intent("com.lha.weather.UPDATE_FROM_INTERNET");
                    sendBroadcast(intent1);
                } else {
                    if (autoUpdateTimer != null){
                        autoUpdateTimer.cancel();
                    }
                }
            }
        };
        updateTimeTimerTask = new TimerTask() {
            @Override
            public void run() {

                appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
                if (appWidgetIds.length > 0){
                    Calendar calendar = Calendar.getInstance();
                    int m = calendar.get(Calendar.MINUTE);
                    if (m != minute){
                        minute = m;
                        context.sendBroadcast(new Intent("com.lha.weather.UPDATE_FROM_LOCAL"));
                    }
                } else {
                    if (updateTimeTimer != null){
                        updateTimeTimer.cancel();
                        updateTimeTimer = null;
                    }
                }
            }
        };
        if (updateSwitch){
            if (showNotification || appWidgetIds.length > 0 || newWidgetIds.length > 0){
                if (autoUpdateTimer != null){
                    autoUpdateTimer.cancel();
                    autoUpdateTimer = null;
                }
                autoUpdateTimer = new Timer();
                autoUpdateTimer.schedule(timerTask,0,i);
            } else {
                if (autoUpdateTimer != null){
                    autoUpdateTimer.cancel();
                    autoUpdateTimer = null;
                }
            }
        } else {
            if (autoUpdateTimer != null){
                autoUpdateTimer.cancel();
                autoUpdateTimer = null;
            }
        }
        if (appWidgetIds.length > 0){
            if (updateTimeTimer != null){
                updateTimeTimer.cancel();
                updateTimeTimer = null;
            }
            updateTimeTimer = new Timer();
            updateTimeTimer.schedule(updateTimeTimerTask,0,3000);
        } else {
            if (updateTimeTimer != null){
                updateTimeTimer.cancel();
                updateTimeTimer = null;
            }
        }

    }
}
