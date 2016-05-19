package com.example.l.myweather.customView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import com.example.l.myweather.MyApplication;
import com.example.l.myweather.R;
import com.example.l.myweather.WeatherToCode;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by L on 2015/11/5.
 */
public class Table extends View {

    private Paint mPaint;
    private int pointCount;

    //ValueAnimator valueAnimator;

    private int[] Xs;
    private int[] Ys;

    //private int[] drawable_ids;
    //private Bitmap[] bitmaps;

    private int jiange = 40;

    private String[] data;
    private int[] secData;
    private String[] weatherData;

    //private int[] drawable_ids;
    private Bitmap[] bitmaps;




    public Table(Context context){
        super(context);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(dp2px((pointCount) * jiange), heightMeasureSpec);
      //  Log.d("Table", "onMeasure");

    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(MyApplication.dp2px(1));
        for (int a = 1; a < Xs.length; a++){
            canvas.drawLine(Xs[a - 1],Ys[a - 1],Xs[a],Ys[a],mPaint);
        }
        for (int i = 0; i < Xs.length; i++) {
            canvas.drawCircle(Xs[i], Ys[i], MyApplication.dp2px(3), mPaint);
            mPaint.setTextSize(MyApplication.sp2px(13));
            mPaint.setTextAlign(Paint.Align.CENTER);
            if (data[i] != null){
                canvas.drawText(data[i],Xs[i],dp2px(175),mPaint);
            }
            if (weatherData[i] != null){
                canvas.drawText(weatherData[i],Xs[i],dp2px(120),mPaint);
            }
            canvas.drawText(secData[i] + "°", Xs[i], Ys[i] - 30, mPaint);
            RectF rectF;
            if (bitmaps[i] != null){
                rectF = new RectF(Xs[i] - dp2px(13),dp2px(130),Xs[i] + dp2px(13),dp2px(156));
                canvas.drawBitmap(bitmaps[i],null,rectF,mPaint);
            }


            //int time = Integer.parseInt(data[i].substring(0,2));
            //Log.d("TABLE",time + "");
            /*if (i > 0 && i != 7 && i != 18){
                if (weatherData[i].equals(weatherData[i - 1])){
                    if (bitmap != null){
                        RectF rectF = new RectF(Xs[i] - dp2px(13),dp2px(130),Xs[i] + dp2px(13),dp2px(156));
                        canvas.drawBitmap(bitmap,null,rectF,mPaint);
                    }
                } else {
                    drawable_id = weatherToCode.getDrawableId(weatherData[i],time);
                    bitmap = BitmapFactory.decodeResource(getResources(),drawable_id);
                    RectF rectF = new RectF(Xs[i] - dp2px(13),dp2px(130),Xs[i] + dp2px(13),dp2px(156));
                    canvas.drawBitmap(bitmap,null,rectF,mPaint);
                }
            }else {
                drawable_id = weatherToCode.getDrawableId(weatherData[i],time);
                bitmap = BitmapFactory.decodeResource(getResources(),drawable_id);
                RectF rectF = new RectF(Xs[i] - dp2px(13),dp2px(130),Xs[i] + dp2px(13),dp2px(156));
                canvas.drawBitmap(bitmap,null,rectF,mPaint);
            }*/

        }


    }

    public void initBitmap(int flag){
        WeatherToCode weatherToCode = WeatherToCode.newInstance();
        int drawable_id;
        if (flag == 0) {
            for (int i = 0; i < 24; i++){
                int time = Integer.parseInt(data[i].substring(0,2));
                drawable_id = weatherToCode.getDrawableId(weatherData[i],time);
                bitmaps[i] = BitmapFactory.decodeResource(getResources(),drawable_id);
            }
        } else {
            for (int i = 0; i < 24; i++){
                int time = Integer.parseInt(data[i].substring(0,2));
                drawable_id = weatherToCode.getDrawableSmallId(weatherData[i],time);
                if (drawable_id != 0){
                    bitmaps[i] = BitmapFactory.decodeResource(getResources(),drawable_id);
                }

            }
        }
    }


    /*public void startAnim(final int i){
        this.i = i;
        Point sp = new Point(Xs[i],Ys[i]);
        Point ep = new Point(Xs[i + 1],Ys[i + 1]);
        valueAnimator = ValueAnimator.ofObject(new PointEvaluator(),sp,ep);
        valueAnimator.setDuration(100);
        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentPoint = (Point) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (i < 22){
                    startAnim(i + 1);
                }
            }
        });
    }*/

    /*public void startAnimation(final int i){
        this.i = i;
        Point sp = new Point(Xs[i],Ys[i]);
        Point ep = new Point(Xs[i + 1],Ys[i + 1]);
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new PointEvaluator(),sp,ep);
        valueAnimator.setDuration(100);
        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentPoint = (Point) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (i < 22){
                    startAnimation(i + 1);
                }
            }
        });
    }*/




    public void setPointCount(int pointCount){
        this.pointCount = pointCount;
        Xs = new int[pointCount];
        Ys = new int[pointCount];
//        drawable_ids = new int[pointCount];
        bitmaps = new Bitmap[pointCount];
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


    /*  public class Point{
        private  float x;
        private  float y;

        public Point(float x,float y){
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }
    }*/

   /* public class PointEvaluator implements TypeEvaluator {
        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            Point startPoint = (Point) startValue;
            Point endPoint = (Point) endValue;
            float x = startPoint.getX() + fraction * (endPoint.getX() - startPoint.getX());
            float y = startPoint.getY() + fraction * (endPoint.getY() - startPoint.getY());
            return new Point(x,y);
        }
    }*/

}
