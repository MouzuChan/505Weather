package com.example.l.myweather.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by L on 2016-07-07.
 */
public class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    public void showSnackbar(View v, String content){
        Snackbar snackbar = Snackbar.make(v, content, Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout sl = (Snackbar.SnackbarLayout)snackbar.getView();
        sl.setBackgroundColor(Color.parseColor("#64000000"));
        snackbar.show();
    }
}
