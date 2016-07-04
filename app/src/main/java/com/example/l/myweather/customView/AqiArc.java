package com.example.l.myweather.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.example.l.myweather.MyApplication;

/**
 * Created by L on 2016-03-08.
 */
public class AqiArc extends View{
    private Paint mPaint;
    private int aqi;
    private int height,width;
    private String aqi_quality;

    public AqiArc(Context context,AttributeSet attrs){
        super(context,attrs);
        init();

    }
    public AqiArc(Context context){
        super(context);
        init();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    }

    public void init(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        width = getWidth();
        height = width - MyApplication.dp2px(100);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(MyApplication.dp2px(10));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha(100);
        RectF rectF = new RectF(MyApplication.dp2px(50),20,width -MyApplication.dp2px(50),height + 20);
        canvas.drawArc(rectF, 120, 300, false, mPaint);
        float i = (float)aqi / 500 * 300;
        int color = getColor();
        mPaint.setColor(color);
        mPaint.setAlpha(255);
        canvas.drawArc(rectF, 120, i, false, mPaint);


        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);

        mPaint.setTextSize(MyApplication.sp2px(30));
        if (aqi != 0){
            canvas.drawText("" + aqi, width / 2, height / 2, mPaint);
        } else {
            mPaint.setColor(Color.WHITE);
            canvas.drawText("--", width / 2, height / 2, mPaint);
        }
        mPaint.setTextSize(MyApplication.sp2px(15));
        canvas.drawText(aqi_quality, width / 2, height -10, mPaint);

    }


    public void setAqi(int aqi,String aqi_quality) {
        this.aqi = aqi;
        this.aqi_quality = aqi_quality;
    }

    public int getColor(){
        int color;
        if (aqi <= 50){
            color = Color.GREEN;
        } else if (aqi <= 100){
            color = Color.YELLOW;
        } else if (aqi <= 150) {
            color = Color.parseColor("#E9967A");
        } else if (aqi <= 200){
            color = Color.parseColor("#f5464c");
        } else if (aqi <= 300){
            color = Color.parseColor("#A757A8");
        } else {
            color = Color.parseColor("#610004");
        }
        return color;
    }

}
