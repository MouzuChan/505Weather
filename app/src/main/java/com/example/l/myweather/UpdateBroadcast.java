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
                context.sendBroadcast(new Intent("com.lha.weather.UPDATE_FROM_LOCAL"));
                break;
            case "android.intent.action.DATE_CHANGED":
                context.sendBroadcast(new Intent("com.lha.weather.UPDATE_FROM_LOCAL"));
                break;

        }

        //throw new UnsupportedOperationException("Not yet implemented");
    }

}
