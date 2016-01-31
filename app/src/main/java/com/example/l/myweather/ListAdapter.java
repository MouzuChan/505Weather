package com.example.l.myweather;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by L on 2016-01-30.
 */
class ListAdapter extends BaseAdapter {

    private List<String> city_list;

    public ListAdapter(List<String> city_list){
        this.city_list = city_list;
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
    public int getCount() {
        return city_list.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //final ViewHolder holder;
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.item_list,null);
            viewHolder.itemText = (TextView)convertView.findViewById(R.id.item_text);
            viewHolder.itemButton = (Button)convertView.findViewById(R.id.item_button);
            viewHolder.changeButton = (Button)convertView.findViewById(R.id.change_button);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.itemButton.setVisibility(View.GONE);
        viewHolder.changeButton.setVisibility(View.GONE);
        viewHolder.itemText.setText(city_list.get(position));
        viewHolder.itemText.setTextColor(Color.BLACK);


        return convertView;
    }

    class ViewHolder {
        public TextView itemText;
        public Button itemButton;
        public Button changeButton;
    }
}
