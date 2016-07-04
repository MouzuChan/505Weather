package com.example.l.myweather.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * Created by L on 2016-06-23.
 */
public class Divider extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private int spanCount = -1;

    public Divider(Context context,int spanCount){
        this.spanCount = spanCount;
        TypedArray a = context.obtainStyledAttributes(new int[]{android.R.attr.listDivider});
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        drawDividerHor(c,parent);
        drawDividerVer(c,parent);

    }

    public void drawDividerHor(Canvas c, RecyclerView parent){
        final int left = parent.getPaddingLeft();
        final int right =  parent.getWidth() - parent.getPaddingRight();
        if (spanCount > 1){
            for (int i = 0; i < parent.getChildCount() - spanCount; i++){

                if (i % spanCount == 0){
                    final int top = parent.getChildAt(i).getBottom();
                    final int bottom = top + mDivider.getIntrinsicHeight();
                    mDivider.setBounds(left,top,right,bottom);
                    mDivider.draw(c);
                }
            }
        }else {
            for (int i = 0; i < parent.getChildCount() - 1; i++) {
                final int top = parent.getChildAt(i).getBottom();
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    public void drawDividerVer(Canvas c,RecyclerView parent){
        if (spanCount > 1){
            View view;
            for (int i = 0; i < parent.getChildCount(); i++){
                if ((i + 1) % spanCount != 0){
                    view = parent.getChildAt(i);
                    int top = view.getTop() + view.getPaddingTop();
                    int bottom = view.getBottom() - view.getPaddingBottom();
                    int left = view.getRight() - view.getPaddingRight();
                    int right = left + mDivider.getIntrinsicHeight();
                    mDivider.setBounds(left,top,right,bottom);
                    mDivider.draw(c);
                }
            }

        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0,0,0,mDivider.getIntrinsicHeight());
    }

}
