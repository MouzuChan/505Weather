package com.example.l.myweather.callback;

import android.view.View;

/**
 * Created by L on 2016-07-07.
 */
public interface OnRecyclerViewItemClickListener {

    void onClick(View v,int position);
    void onLongClick(View v,int position);

}
