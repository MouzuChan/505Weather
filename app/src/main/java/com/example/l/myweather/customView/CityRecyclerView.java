package com.example.l.myweather.customView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by L on 2016-07-03.
 */
public class CityRecyclerView extends RecyclerView {
    public CityRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {




        return super.onInterceptTouchEvent(e);
    }
}
