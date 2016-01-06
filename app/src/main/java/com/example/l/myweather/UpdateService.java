package com.example.l.myweather;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateService extends Service {
    private int i = 0;
    private SharedPreferences sharedPreferences;
    private static Context context = MyApplication.getContext();
    private Timer timer;
    private Timer autoUpdateTimer;
    private int appWidgetIds[];
    private int newWidgetIds[];
    private ComponentName componentName = new ComponentName(context,Widget4x2.class);
    private boolean hasWidget = false;
    private boolean updateSwitch = false;
    //private static RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget4x2_layout);
    //private static AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
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


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (appWidgetIds.length <= 0){
            if (timer != null){
                timer.cancel();
            }
        }

        if (!updateSwitch || (appWidgetIds.length <=0 && newWidgetIds.length <= 0)){
            autoUpdateTimer.cancel();
        }


        Log.d("UpdateService", "onDestroy");
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.d("UpdateService", "onStartCommand");
        String action = intent.getAction();
        if (action != null && action.equals("updateSwitch")){
            updateSwitch = intent.getBooleanExtra("updateSwitch",false);
        }


        if (updateSwitch){
            Log.d("UpdateSwitch","true");
        } else {
            Log.d("UpdateSwitch","false");
        }
        appWidgetIds = Widget4x2.appWidgetManager.getAppWidgetIds(componentName);
        newWidgetIds = Widget4x2.appWidgetManager.getAppWidgetIds(new ComponentName(context, NewAppWidget.class));


        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                appWidgetIds = Widget4x2.appWidgetManager.getAppWidgetIds(componentName);
                newWidgetIds = Widget4x2.appWidgetManager.getAppWidgetIds(new ComponentName(context,NewAppWidget.class));
                if (appWidgetIds.length > 0 || newWidgetIds.length > 0){
                    Intent intent1 = new Intent("com.lha.weather.UPDATE_FROM_INTERNET");
                    sendBroadcast(intent1);
                    Log.d("TAG","UPDATE_WIDGET");
                } else {
                    if (autoUpdateTimer != null){
                        autoUpdateTimer.cancel();
                    }
                    stopSelf();
                }
            }
        };


        if (newWidgetIds.length > 0 || appWidgetIds.length > 0){
            hasWidget = true;
            if (updateSwitch){
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

                if (autoUpdateTimer != null){
                    Log.d("TAG","autoUpdateTimerIsNotNull");
                    autoUpdateTimer.cancel();

                }
                Log.d("TAG","update");
                autoUpdateTimer = new Timer();
                autoUpdateTimer.schedule(timerTask,0,i);
            } else if (autoUpdateTimer != null){
                Log.d("TAG","CLOSE");
                autoUpdateTimer.cancel();
            }

        } else if (autoUpdateTimer != null){
            autoUpdateTimer.cancel();
            stopSelf();
        }

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget4x2_layout);
                //ComponentName componentName = new ComponentName(context,Widget4x2.class);
                appWidgetIds = Widget4x2.appWidgetManager.getAppWidgetIds(componentName);
                //Calendar calendar = Calendar.getInstance();

                if (appWidgetIds.length > 0){
                    Log.d("TAG","UPDATE_WIDGET_TIME");
                    Intent timeIntent = new Intent("com.lha.weather.UPDATE_TIME");
                    sendBroadcast(timeIntent);
                    /*int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    String h = hour + "";
                    String m = minute + "";
                    if (hour < 10){
                        h = "0" + h;
                    }
                    if (minute < 10){
                        m = "0" + m;
                    }
                    Widget4x2.views.setTextViewText(R.id.widget_time, h + ":" + m);
                    Widget4x2.appWidgetManager.updateAppWidget(componentName, Widget4x2.views);*/
                } else {
                    if (timer != null){
                        Log.d("TAG","timer.cancel");
                        timer.cancel();
                    }
                }
            }
        };

        if (appWidgetIds.length > 0) {
            if (timer != null){
                Log.d("TAG","timerIsNotNull");
                timer.cancel();
            }
            timer = new Timer();
            timer.schedule(task,0,15000);

        } else if (timer != null){
            timer.cancel();
        }

        if (!hasWidget){
            stopSelf();
        }
        if (!updateSwitch && appWidgetIds.length == 0){
            stopSelf();
        }

        return super.onStartCommand(intent,flags,startId);

    }

    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }




}
