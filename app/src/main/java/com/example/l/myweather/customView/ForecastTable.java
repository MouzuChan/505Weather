package com.example.l.myweather.customView;

import android.animation.TypeEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import com.example.l.myweather.MyApplication;
import com.example.l.myweather.util.TimeAndDate;

/**
 * Created by L on 2016-03-05.
 */
public class ForecastTable extends View {
    private Paint mPaint;
    private int height;
    private float[] maxXs;
    private float[] minXs;
    private float[] maxYs;
    private float[] minYs;

    private int[] maxData;
    private int[] minData;
    private float jiange;
    private float pointCount;
    private String[] week_strings;
    private String[] date_strings;
    private String[] weatherData;

    public ForecastTable(Context context) {
        super(context);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(MyApplication.dp2px(1));
        mPaint.setTextSize(MyApplication.sp2px(13));
        mPaint.setTextAlign(Paint.Align.CENTER);
        initData();
    }


    public void initData(){
        TimeAndDate timeAndDate = new TimeAndDate();
        week_strings = timeAndDate.getFullWeek();
        date_strings = timeAndDate.getDateStrings();
        week_strings[0] = "今天";
        week_strings[1] = "明天";
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MyApplication.dp2px((pointCount) * jiange), MyApplication.dp2px(height));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawPoint(canvas);
        drawLine(canvas);
        drawText(canvas);
        drawBitmap(canvas);



    }
    public void drawBitmap(Canvas canvas){

    }

    public void drawPoint(Canvas canvas){
        for (int i = 0; i < maxXs.length; i++){
            canvas.drawCircle(maxXs[i], maxYs[i], MyApplication.dp2px(3), mPaint);
            canvas.drawCircle(minXs[i], minYs[i], MyApplication.dp2px(3), mPaint);
            canvas.drawText(maxData[i] + "°", maxXs[i], maxYs[i] - MyApplication.dp2px(20), mPaint);
            canvas.drawText(minData[i] + "°", minXs[i], minYs[i] + MyApplication.dp2px(30), mPaint);
        }
    }

    public void drawLine(Canvas canvas){
        for (int i = 1; i < maxXs.length; i++) {
            canvas.drawLine(maxXs[i - 1], maxYs[i - 1], maxXs[i], maxYs[i], mPaint);
            canvas.drawLine(minXs[i - 1], minYs[i - 1], minXs[i], minYs[i], mPaint);
        }
    }


    public void drawText(Canvas canvas){
        mPaint.setTextSize(MyApplication.sp2px(15));
        for (int i = 0; i < week_strings.length; i++){
            canvas.drawText(date_strings[i],minXs[i],MyApplication.dp2px(height - 15),mPaint);
            canvas.drawText(week_strings[i],minXs[i],MyApplication.dp2px(15),mPaint);
            canvas.drawText(weatherData[i],minXs[i],MyApplication.dp2px(35),mPaint);
        }
    }

    public void setPointCount(int pointCount){
        this.pointCount = pointCount;
        maxXs = new float[pointCount];
        minXs = new float[pointCount];
        maxYs = new float[pointCount];
        minYs = new float[pointCount];
    }

    public void addMaxPoint(int i,float x,float y){
        maxXs[i] = MyApplication.dp2px(x);
        maxYs[i] = MyApplication.dp2px(y);
    }
    public void addMinPoint(int i,float x, float y){
        minXs[i] = MyApplication.dp2px(x);
        minYs[i] = MyApplication.dp2px(y);
    }
    public void setData(int[] maxData,int[] minData){
        this.maxData = maxData;
        this.minData = minData;
    }

    public void setJiange(float jiange) {
        this.jiange = jiange;
    }


    public void setHeight(int height){
        this.height = height;
    }


    public void setWeatherData(String[] weatherData){
        this.weatherData = weatherData;
    }


}
