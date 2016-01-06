package com.example.l.myweather;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CityManager extends AppCompatActivity {
    private Toolbar toolbar;
    private SQLiteDatabase db;
    private ListView listView;
    private ArrayList<String> city_list;
    private MyAdapter adapter;
    private ArrayList<String> cityId_list;
    private MyAdapter myAdapter;
    private int FLAG = 0;
    public static int DELETE_FLAG = 0;
    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_city_manager);
        initView();
        initCityList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent data = new Intent();
                data.putExtra("pageSelected",position);
                setResult(1,data);
                finish();
            }
        });

    }
    public void initView(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        CityDataBase cityDataBase = new CityDataBase(this,"CITY_LIST",null,1);
        db = cityDataBase.getWritableDatabase();
        listView = (ListView)findViewById(R.id.city_list);
        city_list = new ArrayList<String>();
        cityId_list = new ArrayList<String>();
        adapter = new MyAdapter(0);
        myAdapter = new MyAdapter(1);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setTitleTextColor(Color.WHITE);
        listView.setAdapter(adapter);


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
    }

    public void deleteCity(int position,int i){

        if (i == 0){
            adapter.notifyDataSetChanged();
        } else if (i == 1){
            myAdapter.notifyDataSetChanged();
        }
        if (position < city_list.size() + 1){
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(listView.getChildAt(position),"translationX",listView.getWidth(),0f);
            objectAnimator.setDuration(0);
            objectAnimator.start();
        }
        Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_city_manager, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit){
            menuItem = item;
            //Intent intent = new Intent(CityManager.this,SearchActivity.class);
            //startActivityForResult(intent, 1);
            if (FLAG == 0){
                listView.setAdapter(myAdapter);
                item.setTitle("完成");
                item.setIcon(R.drawable.ic_done_white_48dp);
                DELETE_FLAG = 0;
            } else if (FLAG == 1){
                listView.setAdapter(adapter);
                item.setTitle("编辑");
                item.setIcon(R.drawable.ic_create_white_48dp);
                if (DELETE_FLAG == 1){
                    Intent intent = new Intent("com.lha.weather.DONE");
                    sendBroadcast(intent);
                }
                DELETE_FLAG = 0;
            }
        } else if (id == R.id.action_add){
            Intent intent = new Intent(this,SearchActivity.class);
            startActivityForResult(intent,2);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2){
            switch (resultCode){
                case 1:
                    Boolean b = true;
                    String id = data.getStringExtra("return_id");
                    String district = data.getStringExtra("district");
                    for (int i = 0;i < city_list.size();i++){
                        if (district.equals(city_list.get(i))){
                            b = false;
                            Toast.makeText(this,"城市已存在",Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    if (b){
                        addCity(district, id);
                    }
                    break;
                case 2:
                    Boolean bb = true;
                    String city_id = data.getStringExtra("return_id");
                    String city = data.getStringExtra("district");
                    Toast.makeText(this,"定位成功：" + city,Toast.LENGTH_SHORT).show();
                    for (int i = 0;i < city_list.size();i++){
                        if (city.equals(city_list.get(i))){
                            bb = false;
                            Toast.makeText(this,"城市已存在",Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    if (bb){
                        addCity(city,city_id);

                    }
                    break;
            }

        }

    }

    public void addCity(String city,String id){
        city_list.add(city);
        cityId_list.add(id);
        adapter.notifyDataSetChanged();
        Intent intent = new Intent("com.lha.weather.ADD");
        intent.putExtra("city",city);
        intent.putExtra("city_id",id);
        sendBroadcast(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        String color = sharedPreferences.getString("style_color", "青色");
        switch (color){
            case "蓝色":
                toolbar.setBackgroundColor(Color.parseColor("#104d8e"));
                setTheme(R.style.lanseTheme);
                break;
            case "灰色":
                toolbar.setBackgroundColor(Color.GRAY);
                setTheme(R.style.huiseTheme);
                break;
            case "青色":
                toolbar.setBackgroundColor(Color.parseColor("#FF00786F"));
                setTheme(R.style.qingseTheme);
                break;
            case "绿色":
                toolbar.setBackgroundColor(Color.parseColor("#2e8b57"));
                setTheme(R.style.lvseTheme);
                break;
            case "黑色":
                toolbar.setBackgroundColor(Color.BLACK);
                setTheme(R.style.heiseTheme);
                break;
            case "咖啡色":
                toolbar.setBackgroundColor(Color.parseColor("#5f4421"));
                setTheme(R.style.kafeiseTheme);
                break;
        }
    }

    class MyAdapter extends BaseAdapter{
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
                convertView = LayoutInflater.from(CityManager.this).inflate(R.layout.item_list,null);
                viewHolder.itemText = (TextView)convertView.findViewById(R.id.item_text);
                viewHolder.itemButton = (Button)convertView.findViewById(R.id.item_button);
                viewHolder.changeButton = (Button)convertView.findViewById(R.id.change_button);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.itemText.setText(city_list.get(position));

            if (i == 0){
                viewHolder.itemButton.setVisibility(View.GONE);
                viewHolder.changeButton.setVisibility(View.GONE);
                FLAG = 0;
            } else if (i == 1){
                viewHolder.itemButton.setVisibility(View.VISIBLE);
                if (position == 0){
                    viewHolder.changeButton.setVisibility(View.GONE);
                } else {
                    viewHolder.changeButton.setVisibility(View.VISIBLE);
                }
                FLAG = 1;
            }


            viewHolder.itemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DELETE_FLAG = 1;
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
                    changeDefault(position);
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

    @Override
    protected void onStop() {
        super.onStop();
        if (DELETE_FLAG == 1){
            Intent intent = new Intent("com.lha.weather.DONE");
            sendBroadcast(intent);
        }
    }

    public void changeDefault(int position){
        String city = city_list.get(position);
        String city_id = cityId_list.get(position);
        city_list.remove(position);
        cityId_list.remove(position);
        city_list.add(0, city);
        cityId_list.add(0, city_id);
        listView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();

        db.delete("city", null, null);
        ContentValues values = new ContentValues();
        for (int i = 0; i < city_list.size(); i++){
            values.put("city",city_list.get(i));
            values.put("city_id",cityId_list.get(i));
            db.insert("city", null, values);
            values.clear();
        }


        listView.setAdapter(adapter);

        //ActionMenuItemView item = (ActionMenuItemView)findViewById(R.id.action_edit);

        listView.setAdapter(adapter);
        menuItem.setTitle("编辑");
        menuItem.setIcon(R.drawable.ic_create_white_48dp);
        if (DELETE_FLAG == 1){
            Intent intent = new Intent("com.lha.weather.DONE");
            sendBroadcast(intent);
        }
        DELETE_FLAG = 0;

    }
}
