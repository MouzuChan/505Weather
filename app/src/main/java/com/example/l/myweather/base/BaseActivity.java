package com.example.l.myweather.base;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.l.myweather.R;

/**
 * Created by L on 2016-07-07.
 */
public class BaseActivity extends AppCompatActivity {

    private SharedPreferences defaultPreferences;
    private static final String DRAWER_NAVIGATION_BAR_KEY = "draw_navigation_bar_color";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
    }

    public void showSnackbar(View v, String content){
        Snackbar snackbar = Snackbar.make(v, content, Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout sl = (Snackbar.SnackbarLayout)snackbar.getView();
        sl.setBackgroundColor(Color.parseColor("#64000000"));
        snackbar.show();
    }

    public void compatNavigationBarColor() {
        if (defaultPreferences.getBoolean(DRAWER_NAVIGATION_BAR_KEY, false)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.setNavigationBarColor(getResources().getColor(R.color.color_primary));
            }
        }
    }
}
