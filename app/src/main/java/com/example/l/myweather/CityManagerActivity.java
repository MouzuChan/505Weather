package com.example.l.myweather;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.l.myweather.R;

import java.util.ArrayList;

public class CityManagerActivity extends AppCompatActivity {

    private Context context = MyApplication.getContext();
    private SQLiteDatabase db;
    private ListView listView;
    private ArrayList<String> city_list;
    private MyAdapter adapter;
    private ArrayList<String> cityId_list;
    private int FLAG = 0;
    public static int DELETE_FLAG = 0;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_city_manager);
        initView();
        initCityList();
    }

    public void initView(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        CityDataBase cityDataBase = CityDataBase.getInstance();
        db = cityDataBase.getWritableDatabase();
        listView = (ListView)findViewById(R.id.city_list);
        city_list = new ArrayList<String>();
        cityId_list = new ArrayList<String>();
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void initCityList(){
        Cursor cursor = db.query("city", null, null, null, null, null, null);
        if (cursor.moveToFirst()){
            do {
                city_list.add(cursor.getString(cursor.getColumnIndex("city")));
                cityId_list.add(cursor.getString(cursor.getColumnIndex("city_id")));
            } while (cursor.moveToNext());
        }
        adapter.notifyDataSetChanged();
        cursor.close();
    }

    public void deleteCity(int position){
        if (position < city_list.size() + 1){
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(listView.getChildAt(position),"translationX",listView.getWidth(),0f);
            objectAnimator.setDuration(0);
            objectAnimator.start();
        }
        adapter.notifyDataSetChanged();
        Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent("com.lha.weather.CITY_MANAGER");
        intent.putExtra("TYPE","DELETE");
        intent.putExtra("POSITION",position);
        sendBroadcast(intent);
    }


    class MyAdapter extends BaseAdapter {

        public MyAdapter(){
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


            viewHolder.itemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.getContext().deleteFile(cityId_list.get(position));
                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(listView.getChildAt(position),"translationX",0f,listView.getWidth());
                    objectAnimator.setDuration(300);
                    objectAnimator.start();
                    db.delete("city", "city=?", new String[]{city_list.get(position)});
                    city_list.remove(position);
                    cityId_list.remove(position);
                    objectAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            deleteCity(position);
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
                    Intent intent = new Intent("com.lha.weather.CITY_MANAGER");
                    intent.putExtra("TYPE", "CHANGE_DEFAULT");
                    intent.putExtra("POSITION", position);
                    sendBroadcast(intent);
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
