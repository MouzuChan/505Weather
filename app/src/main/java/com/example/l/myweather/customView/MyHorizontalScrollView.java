package com.example.l.myweather.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;

import com.example.l.myweather.callback.OnScrollChangedListener;

/**
 * Created by L on 2016-06-30.
 */
public class MyHorizontalScrollView extends HorizontalScrollView {

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private OnScrollChangedListener onScrollChangedListener;

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollChangedListener != null){
            onScrollChangedListener.onChange(l,oldl);
        }
    }

    public void setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        this.onScrollChangedListener = onScrollChangedListener;
    }
}
