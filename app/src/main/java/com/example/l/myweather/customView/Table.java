package com.example.l.myweather.customView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import com.example.l.myweather.base.MyApplication;
import com.example.l.myweather.util.WeatherToCode;

import java.util.ArrayList;

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
    //private Bitmap[] bitmaps;

    private ArrayList<Integer> pointX = new ArrayList<>();
    private ArrayList<Integer> pointY = new ArrayList<>();

    private ArrayList<String> weatherName = new ArrayList<>();

    private int screenWidth;

    private int scrollX = 0;

    private int[] x_ints;

    private Bitmap[] bitmaps;

    public Table(Context context,int screenWidth){
        super(context);
        this.screenWidth = screenWidth;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(MyApplication.dp2px(1));
        mPaint.setTextSize(MyApplication.sp2px(13));
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(dp2px((pointCount) * jiange), heightMeasureSpec);

    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        drawLine(canvas);
        drawWeather(canvas);

    }

    public void drawLine(Canvas canvas){
        for (int a = 1; a < Xs.length; a++){
            canvas.drawLine(Xs[a - 1],Ys[a - 1],Xs[a],Ys[a],mPaint);
        }
        for (int i = 0; i < Xs.length; i++) {
            canvas.drawCircle(Xs[i], Ys[i], MyApplication.dp2px(3), mPaint);
            if (data[i] != null){
                canvas.drawText(data[i],Xs[i],dp2px(175),mPaint);
            }
            canvas.drawText(secData[i] + "°", Xs[i], Ys[i] - 30, mPaint);
        }

        for (int i = 1; i < pointX.size()- 1; i++){
            canvas.drawLine(pointX.get(i),pointY.get(i),pointX.get(i),getHeight() - dp2px(20),mPaint);
        }
    }


    public void drawWeather(Canvas canvas){
        RectF rectF = new RectF();

        for (int i = 0; i < x_ints.length; i++) {
            if (x_ints[i] > pointX.get(i + 1) - dp2px(30)){
                x_ints[i] = pointX.get(i + 1) - dp2px(30);
            } else if (x_ints[i] < pointX.get(i) + dp2px(30)){
                x_ints[i] = pointX.get(i) + dp2px(30);
            }
            canvas.drawText(weatherName.get(i),x_ints[i],dp2px(140),mPaint);
            rectF.set(x_ints[i] - dp2px(10),dp2px(105),x_ints[i] + dp2px(10),dp2px(125));
            if (bitmaps[i] != null){
                canvas.drawBitmap(bitmaps[i],null,rectF,mPaint);
            }
        }
    }

    public void initBitmap(int f){
        if (bitmaps == null){

            bitmaps = new Bitmap[weatherName.size()];
        }
        WeatherToCode weatherToCode = new WeatherToCode();
        int id;
        for (int i = 0; i < weatherName.size(); i++){
            if (f == 0){
                id = weatherToCode.getDrawableId(weatherName.get(i),12);

            } else {
                id = weatherToCode.getDrawableSmallId(weatherName.get(i),12);
            }
            bitmaps[i] = BitmapFactory.decodeResource(getResources(),id);
        }
    }

    public void initPoint(){
        pointX.clear();
        pointY.clear();
        weatherName.clear();
        String weather = weatherData[0];
        weatherName.add(weather);
        pointX.add(Xs[0]);
        pointY.add(Ys[0]);
        for (int i = 1; i < pointCount - 2; i++){
            if (!weather.equals(weatherData[i])){
                pointX.add(Xs[i]);
                pointY.add(Ys[i]);
                weather = weatherData[i];
                weatherName.add(weatherData[i]);
            }
        }
        pointX.add(Xs[pointCount - 1]);
        pointY.add(Ys[pointCount - 1]);
        x_ints = new int[pointX.size() - 1];
        onScroll();
    }

    public void onScroll(){
        int size = pointX.size();
        for (int i = 0; i < size - 1;i++){
            int x = pointX.get(i + 1);
            if (x > scrollX + screenWidth){
                if (pointX.get(i) > scrollX){
                    x_ints[i] = (scrollX + screenWidth - pointX.get(i)) / 2 + pointX.get(i);
                } else {
                    x_ints[i] = screenWidth / 2 + scrollX;
                }
            } else {
                if (pointX.get(i) > scrollX){
                    x_ints[i] = (x - pointX.get(i)) / 2 + pointX.get(i);
                } else {
                    x_ints[i] = (x - scrollX) / 2 + scrollX;
                }

            }
        }
        invalidate();
    }



    public void setX(int scrollX) {
        this.scrollX = scrollX;
        onScroll();
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
