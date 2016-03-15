package com.example.l.myweather;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by L on 2015/11/5.
 */
public class Table extends View {

    private Paint mPaint;
    private int pointCount;

    private float[] Xs;
    private float[] Ys;


    private int jiange = 40;

    private String[] data;
    private int[] secData;
    private String[] weatherData;

    public Table(Context context,AttributeSet attrs){
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setWillNotDraw(false);
    }

    public Table(Context context){
        super(context);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(dp2px((pointCount) * jiange), heightMeasureSpec);
        Log.d("Table", "onMeasure");

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("Table", "onDraw");
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(5);
        for (int i = 0; i < Xs.length; i++) {
            canvas.drawCircle(Xs[i], Ys[i], 10, mPaint);
            mPaint.setTextSize(MyApplication.sp2px(15));
            mPaint.setTextAlign(Paint.Align.CENTER);
            if (data[i] != null){
                canvas.drawText(data[i],Xs[i],getHeight() - dp2px(10),mPaint);
            }
            if (weatherData[i] != null){
                canvas.drawText(weatherData[i],Xs[i],getHeight() - dp2px(30),mPaint);
            }
            canvas.drawText(secData[i] + "°",Xs[i],Ys[i] - 30,mPaint);
        }
        for (int i = 1; i < Xs.length; i++){
            canvas.drawLine(Xs[i - 1],Ys[i - 1],Xs[i],Ys[i],mPaint);
        }

    }



    public void setPointCount(int pointCount){
        this.pointCount = pointCount;
        Xs = new float[pointCount];
        Ys = new float[pointCount];

    }

    public void addPoint(int point,float x,float y){
        Xs[point] = dp2px(x);
        Ys[point] = dp2px(y);
    }

    public void setJiange(int jiange){
        this.jiange = jiange;
    }

    public void setData(String[] data,String[] weatherData,int[] secData){
        this.data = data;
        this.secData = secData;
        this.weatherData = weatherData;
    }


    public int dp2px(float dpValue){
        float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public int sp2px(float spValue) {
        final float fontScale = this.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
