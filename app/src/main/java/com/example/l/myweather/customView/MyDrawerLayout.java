package com.example.l.myweather.customView;

import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.example.l.myweather.MyApplication;

/**
 * Created by L on 2016-03-23.
 */
public class MyDrawerLayout extends android.support.v4.widget.DrawerLayout{

    public MyDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_MOVE:
                if (this.isDrawerOpen(GravityCompat.START)){
                    if (ev.getY() < MyApplication.dp2px(180)){
                        return false;
                    }
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }


}
