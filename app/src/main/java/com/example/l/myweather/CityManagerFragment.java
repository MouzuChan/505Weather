package com.example.l.myweather;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.l.myweather.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CityManagerFragment extends android.support.v4.app.Fragment {

    private View view;
    private Context context = MyApplication.getContext();
    private SQLiteDatabase db;
    private ListView listView;
    private ArrayList<String> city_list;
    private MyAdapter adapter;
    private ArrayList<String> cityId_list;
    private int FLAG = 0;
    public static int DELETE_FLAG = 0;
    private MainActivity mainActivity;

    public CityManagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_city_manager,container,false);
        initView();
        initCityList();

        return view;
    }


    public void initView(){
        CityDataBase cityDataBase = new CityDataBase(context,"CITY_LIST",null,1);
        db = cityDataBase.getWritableDatabase();
        listView = (ListView)view.findViewById(R.id.city_list);
        city_list = new ArrayList<String>();
        cityId_list = new ArrayList<String>();
        adapter = new MyAdapter(0);
        listView.setAdapter(adapter);
        mainActivity = (MainActivity)getActivity();

    }
    public void initCityList(){
        Cursor cursor = db.query("city",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                city_list.add(cursor.getString(cursor.getColumnIndex("city")));
                cityId_list.add(cursor.getString(cursor.getColumnIndex("city_id")));
            } while (cursor.moveToNext());
        }
        adapter.notifyDataSetChanged();
        cursor.close();
    }

    public void deleteCity(int position,int i){

        mainActivity.deleteCity(position);
        adapter.notifyDataSetChanged();
        if (position < city_list.size() + 1){
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(listView.getChildAt(position),"translationX",listView.getWidth(),0f);
            objectAnimator.setDuration(0);
            objectAnimator.start();
        }
        Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();

    }

    public static CityManagerFragment newInstance() {

        Bundle args = new Bundle();
        CityManagerFragment fragment = new CityManagerFragment();
        fragment.setArguments(args);
        return fragment;
    }



    class MyAdapter extends BaseAdapter {
        private int i;
        public MyAdapter(int i){
            this.i = i;
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
                convertView = LayoutInflater.from(context).inflate(R.layout.item_list,null);
                viewHolder.itemText = (TextView)convertView.findViewById(R.id.item_text);
                viewHolder.itemButton = (Button)convertView.findViewById(R.id.item_button);
                viewHolder.changeButton = (Button)convertView.findViewById(R.id.change_button);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.itemText.setText(city_list.get(position));
            if (position == 0){
                viewHolder.changeButton.setVisibility(View.GONE);
            }

            /*if (i == 0){
                viewHolder.itemButton.setVisibility(View.GONE);
                viewHolder.changeButton.setVisibility(View.GONE);
                FLAG = 0;
            } else if (i == 1){
                viewHolder.itemButton.setVisibility(View.VISIBLE);
                 else {
                    viewHolder.changeButton.setVisibility(View.VISIBLE);
                }
                FLAG = 1;
            }*/


            viewHolder.itemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //DELETE_FLAG = 1;
                    MyApplication.getContext().deleteFile(cityId_list.get(position));
                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(listView.getChildAt(position),"translationX",0f,listView.getWidth());
                    objectAnimator.setDuration(300);
                    objectAnimator.start();
                    db.delete("city", "city=?", new String[]{city_list.get(position)});
                    BlankFragment.updatePreferences.edit().remove("update_time" + city_list.get(position)).apply();
                    city_list.remove(position);
                    objectAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            deleteCity(position, i);
                        }
                    });

                }
            });
            viewHolder.changeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DELETE_FLAG = 1;
                    String city = city_list.get(position);
                    String city_id = cityId_list.get(position);
                    city_list.remove(position);
                    cityId_list.remove(position);
                    city_list.add(0, city);
                    cityId_list.add(0, city_id);
                    adapter.notifyDataSetChanged();
                    mainActivity.changeDefaultCity(position);
                }
            });

            return convertView;
        }

        class ViewHolder {
            public TextView itemText;
            public Button itemButton;
            public Button changeButton;
        }
    }

}
