package com.example.l.myweather;

import android.graphics.Bitmap;

/**
 * Created by L on 2015/10/25.
 */
public interface ImageCallBack {
    void onFinish(Bitmap bitmap);
    void onError();
}
