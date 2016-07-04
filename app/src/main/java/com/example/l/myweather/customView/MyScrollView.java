package com.example.l.myweather.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by L on 2016-06-30.
 */
public class MyScrollView extends ScrollView {

    private float downY;
    private float downX;

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                downY = ev.getY();
                downX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float y = ev.getY();
                float x = ev.getX();
                int absX = (int)Math.abs(x - downX);
                int absY = (int)Math.abs(y - downY);
                return absX < absY;

        }


        return super.onInterceptTouchEvent(ev);
    }
}
