package com.example.l.myweather;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

/**
 * Created by L on 2016-03-15.
 */
public class SunRiseAndSet extends View {
    private Paint mPaint;
    private float totalTime;
    private float nowTime;
    private String sunRise;
    private String sunSet;

    public SunRiseAndSet(Context context) {
        super(context);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = width -MyApplication.dp2px(100);

        int left = MyApplication.dp2px(50);
        RectF rectF = new RectF(left,20,width - left,height+20);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(MyApplication.dp2px(10));
        mPaint.setColor(Color.WHITE);


        float i;
        if (nowTime > 0){
            if (nowTime < totalTime){
                i = nowTime / totalTime * 180;
            } else {
                i = 180;
            }
        } else {
            i = 0;
        }

        mPaint.setAlpha(200);

        canvas.drawArc(rectF, 180, i, true, mPaint);
        mPaint.setAlpha(50);
        canvas.drawArc(rectF, 180 + i, 180 - i, true, mPaint);
        mPaint.setAlpha(255);

        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(MyApplication.sp2px(15));

        canvas.drawText(sunRise, left, height / 2 + MyApplication.dp2px(30), mPaint);
        canvas.drawText(sunSet, width - left, height / 2 + MyApplication.dp2px(30), mPaint);

        canvas.drawText("日出",left,height / 2 + MyApplication.dp2px(50),mPaint);
        canvas.drawText("日落",width - left,height /2 + MyApplication.dp2px(50),mPaint);

    }

    public void setTime(float totalTime,float nowTime){
        this.totalTime = totalTime;
        this.nowTime = nowTime;
    }

    public void setString(String sunRise,String sunSet){
        this.sunRise = sunRise;
        this.sunSet = sunSet;

    }
}
