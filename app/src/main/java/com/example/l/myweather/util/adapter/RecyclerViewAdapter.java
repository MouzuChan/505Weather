package com.example.l.myweather.util.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.l.myweather.base.MyApplication;
import com.example.l.myweather.R;

/**
 * Created by L on 2016-06-30.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {


    private String[] data1;
    private String[] data2;

    public RecyclerViewAdapter(String[] data2){
        this.data2 = data2;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(MyApplication.getContext()).
                inflate(R.layout.recycle_view_item,null));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (data1 == null || data1.length < position + 1 || data1[position].isEmpty()){
            holder.indexDetail.setText("--");
        }else {
            holder.indexDetail.setText(data1[position]);
        }
        if (data2 == null || data2[position].isEmpty()){
            holder.indexName.setText("--");
        } else {
            holder.indexName.setText(data2[position]);
        }

    }


    @Override
    public int getItemCount() {
        return data2.length;
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView indexName;
        TextView indexDetail;
        public MyViewHolder(View v){
            super(v);
            indexName = (TextView) v.findViewById(R.id.index_name);
            indexDetail = (TextView) v.findViewById(R.id.index_detail);
        }
    }

    public void setData1(String[] data1) {
        this.data1 = data1;
    }

}
