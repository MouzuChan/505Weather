package com.example.l.myweather.view.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.l.myweather.R;
import com.example.l.myweather.base.MyApplication;
import com.example.l.myweather.view.DragListView;
import com.example.l.myweather.database.CityDataBase;
import com.example.l.myweather.activities.CityManagerActivity;
import com.example.l.myweather.activities.MainActivity;
import com.example.l.myweather.util.City;

import java.util.ArrayList;

/**
 * Created by L on 2016-03-28.
 */
public class MyAdapter extends BaseAdapter {

    private ArrayList<City> cityArrayList;
    private SQLiteDatabase db;
    private DragListView listView;
    private boolean isEditState = false;
    private int delete_position;
    public static int CHANGE_FLAG = 0;
    private City deletedCity;

    public Context context;
    public MyAdapter(DragListView listView){
        this.cityArrayList = MainActivity.cityArrayList;
        this.listView = listView;
        context = MyApplication.getContext();
        CityDataBase cityDataBase = CityDataBase.getInstance();
        db = cityDataBase.getWritableDatabase();

    }

    @Override
    public Object getItem(int position) {
        return cityArrayList.get(position).getCityName();
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        return cityArrayList.size();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            //convertView = LayoutInflater.from(context).inflate(R.layout.item_list,null);
            convertView = View.inflate(context,R.layout.item_list,null);
            viewHolder.itemText = (TextView)convertView.findViewById(R.id.tv_city_name);
            viewHolder.itemButton = (ImageView)convertView.findViewById(R.id.item_button);
            viewHolder.dragImage = (ImageView)convertView.findViewById(R.id.drag_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.itemText.setText(cityArrayList.get(position).getCityName());

        final View finalConvertView = convertView;
        viewHolder.itemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //View view = listView.getChildAt(position);

                final int x = (int) finalConvertView.getX();
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(finalConvertView, "X", x, parent.getWidth());
                objectAnimator.setDuration(300);
                objectAnimator.start();
                objectAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        delete_position = position;
                        deletedCity = cityArrayList.get(position);
                        deleteCity(position);

                        CityManagerActivity.showSnackBar(deletedCity.getCityName() + "  已删除");
                        if (position < cityArrayList.size() + 1) {
                            ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(finalConvertView, "X", parent.getWidth(), x);
                            objectAnimator1.setDuration(0);
                            objectAnimator1.start();
                        }
                    }
                });
            }
        });
        if (!isEditState){
            viewHolder.itemButton.setVisibility(View.GONE);
            viewHolder.dragImage.setVisibility(View.GONE);
        } else {
            viewHolder.itemButton.setVisibility(View.VISIBLE);
            viewHolder.dragImage.setVisibility(View.VISIBLE);
        }
        return convertView;
    }


    public void deleteCity(int position) {
        cityArrayList.remove(position);
        notifyDataSetChanged();
        CHANGE_FLAG = 1;
    }

    public void hideItem(int position){
        View view = listView.getChildAt(position);
        view.setVisibility(View.INVISIBLE);
    }

    public void showItem(int position){
        View view = listView.getChildAt(position);
        view.setVisibility(View.VISIBLE);
    }
    public void changePosition(int position,int p){
        City city1 = cityArrayList.get(position);
        City city2 = cityArrayList.get(p);

        cityArrayList.set(position,city2);
        cityArrayList.set(p,city1);

        notifyDataSetChanged();
        CHANGE_FLAG = 1;
    }

    class ViewHolder {
        public TextView itemText;
        public ImageView itemButton;
        public ImageView dragImage;
    }


    public void setIsEditState(boolean isEditState) {
        this.isEditState = isEditState;
    }

    public void changeData(){
        db.delete("city", null, null);
        ContentValues values = new ContentValues();
        for (int i = 0; i < cityArrayList.size(); i++){
            values.put("city",cityArrayList.get(i).getCityName());
            values.put("city_id",cityArrayList.get(i).getCityId());
            db.insert("city", null, values);
            values.clear();
        }
        Intent intent = new Intent("com.lha.weather.CITY_MANAGER");
        intent.putExtra("TYPE", "CHANGE_DEFAULT");
        context.sendBroadcast(intent);
    }

    public void cancelDelete(){
        cityArrayList.add(delete_position,deletedCity);
        notifyDataSetChanged();
    }

}
