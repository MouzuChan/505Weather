package com.example.l.myweather.util.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.l.myweather.MyApplication;
import com.example.l.myweather.R;
import com.example.l.myweather.callback.OnRecyclerViewItemClickListener;
import com.example.l.myweather.util.City;

import java.util.ArrayList;

/**
 * Created by L on 2016-07-07.
 */
public class HeaderRecyclerViewAdapter extends  RecyclerView.Adapter<HeaderRecyclerViewAdapter.MyViewHolder>{

    private ArrayList<City> cityArrayList;
    private Context context;
    private OnRecyclerViewItemClickListener listener;


    public HeaderRecyclerViewAdapter(ArrayList<City> cityArrayList){
        this.cityArrayList = cityArrayList;
        context = MyApplication.getContext();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_header_recycler_view,null));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {


        holder.tv_temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener != null){
                    //获得item最新的位置
                    int p = holder.getAdapterPosition();
                    listener.onClick(v,p);
                }
            }
        });
        holder.tv_city_name.setText(cityArrayList.get(position).getCityName());
        holder.tv_temp.setText(cityArrayList.get(position).getCityTemp());
    }

    @Override
    public int getItemCount() {
        return cityArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_city_name;
        private TextView tv_temp;
        public MyViewHolder(View v){
            super(v);
            tv_city_name = (TextView) v.findViewById(R.id.tv_city_name);
            tv_temp = (TextView) v.findViewById(R.id.tv_temp);
        }
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.listener = listener;
    }
}
