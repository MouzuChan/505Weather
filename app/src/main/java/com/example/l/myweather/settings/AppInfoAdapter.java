package com.example.l.myweather.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.l.myweather.base.MyApplication;
import com.example.l.myweather.R;

import java.util.ArrayList;

/**
 * Created by L on 2016-04-27.
 */
public class AppInfoAdapter extends BaseAdapter{
    private ArrayList<AppInfo> appInfoList;

    public AppInfoAdapter(ArrayList<AppInfo> appInfoList){
        this.appInfoList = appInfoList;
    }

    @Override
    public int getCount() {
        return appInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.app_info_layout,null);
            viewHolder.app_name = (TextView)convertView.findViewById(R.id.app_name);
            viewHolder.app_icon = (ImageView)convertView.findViewById(R.id.app_icon);
            //viewHolder.dragImage = (ImageView)convertView.findViewById(R.id.drag_image);
            //viewHolder.temp_text = (TextView)convertView.findViewById(R.id.temp_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.app_icon.setImageDrawable(appInfoList.get(position).getApp_icon());
        viewHolder.app_name.setText(appInfoList.get(position).getApp_name());
        return convertView;
    }


    public class ViewHolder{
        public ImageView app_icon;
        public TextView app_name;
    }
}
