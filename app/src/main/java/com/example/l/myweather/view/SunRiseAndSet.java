package com.example.l.myweather.view;

import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import com.example.l.myweather.base.MyApplication;

/**
 * Created by L on 2016-03-15.
 */
public class SunRiseAndSet extends View {
    private Paint mPaint;
    private float totalTime;
    private float nowTime;
    private String sunRise;
    private String sunSet;
    //private Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.sunrise);
    public SunRiseAndSet(Context context) {
        super(context);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = width - MyApplication.dp2px(80);
        int left = MyApplication.dp2px(40);
        RectF rectF = new RectF(left,20,width - left,height + 20);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(MyApplication.dp2px(10));
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        float i;
        if (nowTime > 0){
            if (nowTime < totalTime){
                i = nowTime / totalTime * height;
            } else {
                i = height;
            }
        } else {
            i = 0;
        }
        canvas.drawArc(rectF, 180, 180, false, mPaint);
        //mPaint.setAlpha(50);
        //canvas.drawArc(rectF, 180 + i, 180 - i, true, mPaint);
        //mPaint.setAlpha(255);
        mPaint.setStrokeWidth(2);
        if (i < height / 2){
            float x = height / 2 - i;
            int r = height / 2;
            int ii = (int)Math.sqrt((r * r) - (x * x));
            int y = height / 2 + 20 - ii;
            canvas.drawCircle(i + left,y,MyApplication.dp2px(4),mPaint);
            //Log.d("SunRise","i = " + i);
            //Log.d("SunRise","x = " + x);
            //Log.d("SunRise","ii = " + ii);
            //Log.d("SunRise","y = " + y);

        } else {
            float x = i - height / 2;
            int r = height / 2;
            int ii = (int)Math.sqrt((r * r) - (x * x));
            int y = height / 2 + 20 - ii;
            canvas.drawCircle(i + left,y,MyApplication.dp2px(4),mPaint);
        }
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(MyApplication.sp2px(12));

        canvas.drawText(sunRise, left, height / 2 + MyApplication.dp2px(30), mPaint);
        canvas.drawText("日出",left,height / 2 + MyApplication.dp2px(46),mPaint);

        canvas.drawText(sunSet, width - left, height / 2 + MyApplication.dp2px(30), mPaint);


        canvas.drawText("日落",width - left,height /2 + MyApplication.dp2px(46),mPaint);
        //canvas.drawBitmap(bitmap,0,height / 2 + MyApplication.dp2px(50),mPaint);

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
