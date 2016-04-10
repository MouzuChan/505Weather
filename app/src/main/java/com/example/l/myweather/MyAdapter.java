package com.example.l.myweather;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by L on 2016-03-28.
 */
public class MyAdapter extends BaseAdapter {

    private ArrayList<String> city_list;
    private ArrayList<String> cityId_list,temp_list;
    private SQLiteDatabase db;
    private ListView listView;
    private boolean isEditState = false;
    private int delete_position;
    private String delete_city;
    private String delete_cityId;
    private String delete_temp;
    public static int CHANGE_FLAG = 0;

    public Context context = MyApplication.getContext();
    public MyAdapter(ListView listView){
        this.city_list = (ArrayList)MainActivity.city_list.clone();
        this.cityId_list = (ArrayList)MainActivity.cityId_list.clone();
        this.temp_list = (ArrayList)MainActivity.tempList.clone();
        this.listView = listView;
        CityDataBase cityDataBase = CityDataBase.getInstance();
        db = cityDataBase.getWritableDatabase();

    }

    @Override
    public Object getItem(int position) {
        return city_list.get(position);
    }


    public String getTemp(int position){
        return temp_list.get(position);
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list,null);
            viewHolder.itemText = (TextView)convertView.findViewById(R.id.item_text);
            viewHolder.itemButton = (ImageView)convertView.findViewById(R.id.item_button);
            viewHolder.dragImage = (ImageView)convertView.findViewById(R.id.drag_image);
            viewHolder.temp_text = (TextView)convertView.findViewById(R.id.temp_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.itemText.setText(city_list.get(position));
        if (temp_list.get(position) != null && !temp_list.get(position).isEmpty()){
            viewHolder.temp_text.setText(temp_list.get(position));
        }

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
                        //Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                        //deleteFile(position);
                        delete_position = position;
                        delete_city = city_list.get(position);
                        delete_cityId = cityId_list.get(position);
                        delete_temp = temp_list.get(position);
                        deleteCity(position);

                        CityManagerActivity.showSnackBar(delete_city + "  已删除");
                        if (position < city_list.size() + 1) {
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
        //db.delete("city", "city=?", new String[]{city_list.get(position)});
        /*if (city_list.get(position).equals(CityManagerActivity.location_city)){
            SharedPreferences sharedPreferences = context.getSharedPreferences("location_city", Context.MODE_APPEND);
            SharedPreferences.Editor editor= sharedPreferences.edit();
            editor.remove("location_city");
            editor.remove("location_city_id");
            editor.apply();
        }*/
        city_list.remove(position);
        cityId_list.remove(position);
        temp_list.remove(position);
        notifyDataSetChanged();
        CHANGE_FLAG = 1;
        //Intent intent = new Intent("com.lha.weather.CITY_MANAGER");
        //intent.putExtra("TYPE", "DELETE");
        //intent.putExtra("POSITION", position);
        //context.sendBroadcast(intent);
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
        String firstCity = city_list.get(position);
        String firstCityId = cityId_list.get(position);
        String secondCity = city_list.get(p);
        String secondCityId = cityId_list.get(p);
        //city_list.remove(position);
        //city_list.add(position, secondCity);
        //city_list.remove(p);
        //city_list.add(p, firstCity);
        city_list.set(position,secondCity);
        city_list.set(p,firstCity);
        cityId_list.set(position,secondCityId);
        cityId_list.set(p,firstCityId);
        //cityId_list.remove(position);
        //cityId_list.add(position, secondCityId);
        //cityId_list.remove(p);
        //cityId_list.add(p, firstCityId);

        String temp = temp_list.get(position);
        temp_list.set(position,temp_list.get(p));
        temp_list.set(p,temp);
        notifyDataSetChanged();
        CHANGE_FLAG = 1;
    }

    class ViewHolder {
        public TextView itemText;
        public ImageView itemButton;
        public ImageView dragImage;
        public TextView temp_text;
    }


    public void setIsEditState(boolean isEditState) {
        this.isEditState = isEditState;
    }

    public void changeData(){
        db.delete("city", null, null);
        ContentValues values = new ContentValues();
        for (int i = 0; i < city_list.size(); i++){
            Log.d("TAG",city_list.get(i));
            values.put("city",city_list.get(i));
            values.put("city_id",cityId_list.get(i));
            db.insert("city", null, values);
            values.clear();
        }
        Intent intent = new Intent("com.lha.weather.CITY_MANAGER");
        intent.putExtra("TYPE", "CHANGE_DEFAULT");
        context.sendBroadcast(intent);
    }

    public void cancelDelete(){
        city_list.add(delete_position,delete_city);
        cityId_list.add(delete_position,delete_cityId);
        temp_list.add(delete_position,delete_temp);
        notifyDataSetChanged();
    }


}
