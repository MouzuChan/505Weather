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
    private float[] maxXaxis;
    private float[] maxYaxis;

    private float[] minXaxis;
    private float[] minYaxis;
    private int jiange;
    private int lineCount;

    private int[] maxTemp;
    private int[] minTemp;

    public Table(Context context,AttributeSet attrs){
        super(context, attrs);
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
        if (lineCount == 2){
            for (int i = 0; i < maxXaxis.length; i++){
                canvas.drawCircle(maxXaxis[i], maxYaxis[i], 10, mPaint);
                canvas.drawCircle(minXaxis[i], minYaxis[i], 10, mPaint);
                mPaint.setTextSize(30);
                if (maxTemp[i] != 0 || minTemp[i] != 0){
                    canvas.drawText(maxTemp[i] + "°", maxXaxis[i] - dp2px(5),maxYaxis[i] - dp2px(15),mPaint);
                    canvas.drawText(minTemp[i] + "°", minXaxis[i] - dp2px(5), minYaxis[i] + dp2px(25),mPaint);
                }

            }
            for (int i = 1; i < maxXaxis.length; i++){
                canvas.drawLine(maxXaxis[i - 1],maxYaxis[i - 1],maxXaxis[i],maxYaxis[i],mPaint);
                canvas.drawLine(minXaxis[i - 1],minYaxis[i - 1],minXaxis[i],minYaxis[i],mPaint);
            }
        } else if (lineCount == 1){
            for (int i = 0; i < maxXaxis.length; i++){
                canvas.drawCircle(maxXaxis[i],maxYaxis[i],10,mPaint);
                mPaint.setTextSize(sp2px(15));
                canvas.drawText(maxTemp[i] + "°", maxXaxis[i] - dp2px(5), maxYaxis[i] - dp2px(15), mPaint);
            }
            for (int i = 1; i < maxXaxis.length; i++){
                canvas.drawLine(maxXaxis[i - 1],maxYaxis[i - 1],maxXaxis[i],maxYaxis[i],mPaint);
            }

        }

    }

    public void setPointCount(int pointCount){
        this.pointCount = pointCount;
        maxXaxis = new float[pointCount];
        maxYaxis = new float[pointCount];
        minXaxis = new float[pointCount];
        minYaxis = new float[pointCount];

        maxTemp = new int[pointCount];
        minTemp = new int[pointCount];
    }

    public void addMaxPoint(int point,float x,float y){
        maxXaxis[point] = dp2px(x);
        maxYaxis[point] = dp2px(y);
    }
    public void addMinPoint(int point,float x,float y){
        minXaxis[point] = dp2px(x);
        minYaxis[point] = dp2px(y);
    }

    public void setJiange(int jiange){
        this.jiange = jiange;
    }

    public void setMaxTemp(int point,int temp){
        maxTemp[point] = temp;
    }

    public void setMinTemp(int point,int temp){
        minTemp[point] = temp;
    }

    public int dp2px(float dpValue){
        float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    public void setLineCount(int lineCount){
        this.lineCount = lineCount;
    }

    public int sp2px(float spValue) {
        final float fontScale = this.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
