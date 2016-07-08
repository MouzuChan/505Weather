package com.example.l.myweather.customView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by L on 2016-07-07.
 */
public class MyDrawerLayout extends android.support.v4.widget.DrawerLayout {

    private int top;
    private int bottom;
    private int left;
    private int right;
    public MyDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            //在这里处理滑动冲突
            case MotionEvent.ACTION_MOVE:
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                if (x > left && x < right && y > top && y < bottom){
                    return false;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setRecyclerView(int left,int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }
}
