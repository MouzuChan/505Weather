package com.example.l.myweather;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;


public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        if (intent.getAction().equals("com.lha.weather.KILL_SERVICE")){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context,Widget4x2.class));
            int newWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context,NewAppWidget.class));


        } else {
            context.sendBroadcast(new Intent("com.lha.weather.UPDATE_FROM_LOCAL"));
        }

    }
}
