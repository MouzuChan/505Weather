package com.example.l.myweather.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.l.myweather.base.MyApplication;

/**
 * Created by L on 2016-02-05.
 */
public class Indicator extends View {
    private Paint mPaint;
    private int circleCount;
    private int pageSelected;
    public Indicator(Context context,AttributeSet attributeSet) {
        super(context,attributeSet);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(5);
        int x = getWidth() / 2;
        int y = getHeight() / 2;
        int firstCircle;
        if (circleCount % 2 == 0){
            firstCircle = x - ((circleCount / 2 - 1) * 30 + 15);
        } else {
            firstCircle = x - ((circleCount / 2) * 30);
        }
        for (int i = 0; i < circleCount; i++){
            if (i == pageSelected){
                mPaint.setAlpha(255);
                canvas.drawCircle(firstCircle + (30 * i),y, MyApplication.dp2px(3),mPaint);
            } else {
                mPaint.setAlpha(100);
                canvas.drawCircle(firstCircle + (30 * i),y,MyApplication.dp2px(3),mPaint);
            }

        }
        //canvas.drawCircle(x, y, 5, mPaint);
        //canvas.drawCircle(x + 30,y,5,mPaint);

    }
    public void setCircleCount(int circleCount){
        this.circleCount = circleCount;
    }
    public void setPageSelected(int pageSelected){
        this.pageSelected = pageSelected;
    }
}
