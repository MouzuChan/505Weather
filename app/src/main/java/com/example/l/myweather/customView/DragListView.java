package com.example.l.myweather.customView;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import com.example.l.myweather.MyAdapter;
import com.example.l.myweather.MyApplication;
import com.example.l.myweather.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by L on 2016-03-19.
 */
public class DragListView extends ListView{



    private WindowManager.LayoutParams layoutParams;
    private View itemView;
    private WindowManager windowManager;
    private int dragPosition;

    private MyAdapter adapter;
    private int height;
    private int position;

    private int downX,downY;
    private Timer timer;
    private TimerTask timerTask;
    private Handler handler;
    private boolean isEditState = false;
    private Timer downTimer;
    private TimerTask downTimerTask;

    public DragListView(Context context, AttributeSet attrs) {

        super(context, attrs);
        handler = new Handler(){
            public void handleMessage(Message message){
                switch (message.what){
                    case 1:
                        setSelection(dragPosition - 1);
                        break;
                    case 2:
                        setSelectionFromTop(dragPosition + 1,getHeight() - height);
                        break;
                    default:
                        break;
                }
                super.handleMessage(message);
            }
        };

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isEditState){
                    downX = (int) ev.getX();
                    downY = (int) ev.getY();
                    dragPosition = pointToPosition(downX, downY);
                    position = dragPosition - getFirstVisiblePosition();
                    int rawX = (int) ev.getRawX();
                    int rawY = (int) ev.getRawY();
                    if (dragPosition != INVALID_POSITION) {
                        View view = getChildAt(position);
                        ImageView imageView = (ImageView) view.findViewById(R.id.drag_image);
                        if (downX > imageView.getLeft() + view.getLeft() && downX < imageView.getRight()) {
                            adapter = (MyAdapter) getAdapter();
                            itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_list, null);
                            TextView textView = (TextView) itemView.findViewById(R.id.item_text);
                            TextView tempView = (TextView) itemView.findViewById(R.id.temp_view);
                            textView.setText((String) adapter.getItem(dragPosition));
                            tempView.setText(adapter.getTemp(dragPosition));
                            hideItem(position);
                            height = MyApplication.dp2px(50);
                            startDrag(rawX, rawY - (height / 2));
                        }
                    }
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (itemView != null && dragPosition != INVALID_POSITION && isEditState){
            switch (ev.getAction()){
                case MotionEvent.ACTION_MOVE:
                    if (height == 0){
                        height = itemView.getHeight();
                    }
                    position = dragPosition - getFirstVisiblePosition();
                    int rawX1 = (int)ev.getRawX();
                    int rawY1 = (int)ev.getRawY() - height / 2;
                    int x1 = (int)ev.getX();
                    int y1 = (int)ev.getY();
                    int p = pointToPosition(x1,y1);
                    View view = (View)getParent();
                    if (rawY1 >= view.getTop() && rawY1 <= view.getTop() + getHeight() - height && position != INVALID_POSITION){
                        onDrag(rawX1, rawY1);
                        if (p != INVALID_POSITION){
                            changePosition(p,y1);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    stopDrag();
                    if (position != INVALID_POSITION){
                        showItem(position);
                    }

                    break;
            }
            return true;
        }

        return super.onTouchEvent(ev);
    }

    public void startDrag(int x,int y){
        if (layoutParams == null){
            layoutParams = new WindowManager.LayoutParams();
        }
        if (itemView != null){
            layoutParams.alpha = 0.7f;
            layoutParams.x = x;
            layoutParams.y = y;
            layoutParams.gravity = Gravity.TOP | Gravity.START;
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            layoutParams.windowAnimations = 0;
            if (windowManager == null){
                windowManager = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
            }
            windowManager.addView(itemView, layoutParams);
        }
    }

    public void onDrag(int x,int y){
        if (layoutParams == null){
            layoutParams = new WindowManager.LayoutParams();
        }
        layoutParams.x = x;
        layoutParams.y = y;
        windowManager.updateViewLayout(itemView, layoutParams);

    }

    public void hideItem(int position){
        adapter.hideItem(position);
    }

    public void showItem(int position){
        adapter.showItem(position);
    }

    public void stopDrag(){
        if (itemView != null){
            if (windowManager == null){
                windowManager = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
            }
            windowManager.removeView(itemView);
            itemView = null;
        }
        if (timer != null || timerTask != null){
            timer = null;
            timerTask = null;
        }
        if (downTimer != null || downTimerTask != null){
            downTimer = null;
            downTimerTask = null;
        }
    }

    public void changePosition(int p,int y){
        if (dragPosition != p){
            adapter.changePosition(dragPosition, p);
            showItem(position);
            hideItem(p - getFirstVisiblePosition());
            position = p - getFirstVisiblePosition();
            View view = getChildAt(p - getFirstVisiblePosition());
            int i = getHeight() / 2;
            if (dragPosition > p){
                dragPosition = p;
                if (position <= 1 && getFirstVisiblePosition() > 0){
                    timerTask(p);
                } else if (y < i){
                    setSelectionFromTop(p,view.getTop() + view.getHeight() / 2);
                }
            } else {
                dragPosition = p;
                if (position >= getChildCount() - 1 && getLastVisiblePosition() > 0){
                    downTimerTask();
                }
                else if (y > i){
                    //View view = getChildAt(p - getFirstVisiblePosition());
                    setSelectionFromTop(p,view.getTop() - view.getHeight() - 20);
                }
            }



        }
    }



    public void timerTask(final int p){
        if (timer == null || timerTask == null){
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (position <= 1 && getFirstVisiblePosition() > 0){

                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                        //setSelection(dragPosition - 1);
                    }
                }
            };
            timer.schedule(timerTask, 0, 800);
        }

    }

    public void downTimerTask(){
        if (downTimer == null || downTimerTask == null){
            downTimer = new Timer();
            downTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (position >= getChildCount() - 1 && getLastVisiblePosition() > 0){
                        Message message = new Message();
                        message.what = 2;
                        handler.sendMessage(message);
                    }

                }
            };
            downTimer.schedule(downTimerTask,0,800);
        }
    }


    public void setIsEditState(boolean isEditState) {
        this.isEditState = isEditState;
    }
}
