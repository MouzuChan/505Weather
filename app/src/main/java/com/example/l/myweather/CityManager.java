package com.example.l.myweather;

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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class CityManager extends AppCompatActivity {
    private Toolbar toolbar;
    private SQLiteDatabase db;
    private ListView listView;
    private ArrayList<String> city_list;
    private ArrayAdapter adapter;
    private ArrayList<String> cityId_list;

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



    }
    public void initView(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        CityDataBase cityDataBase = new CityDataBase(this,"CITY_LIST",null,1);
        db = cityDataBase.getWritableDatabase();
        listView = (ListView)findViewById(R.id.city_list);
        city_list = new ArrayList<String>();
        cityId_list = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,city_list);
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
        listView.setOnItemClickListener(onItemClickListener);
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

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CityManager.this);
            builder.setTitle("");

            builder.setItems(new String[]{"设为默认", "删除"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            if (position != 0){
                                String city = city_list.get(position);
                                String city_id = cityId_list.get(position);
                                Intent intent = new Intent("com.lha.weather.CHANGE_DEFAULT");
                                intent.putExtra("city",city);
                                intent.putExtra("city_id",city_id);
                                sendBroadcast(intent);
                                city_list.remove(city);
                                cityId_list.remove(city_id);
                                city_list.add(0,city);
                                cityId_list.add(0,city_id);
                                adapter.notifyDataSetChanged();
                            }
                            break;
                        case 1:
                            if (city_list.size() == 1){
                                Toast.makeText(MyApplication.getContext(),"不能删除最后一个城市",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Intent intent = new Intent("com.lha.weather.REMOVE");
                                intent.putExtra("city", city_list.get(position));
                                intent.putExtra("position",position);
                                sendBroadcast(intent);
                                deleteCity(position);
                            }
                            break;
                    }
                }
            });
            builder.show();
        }
    };
    public void deleteCity(int position){
        city_list.remove(position);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_city_manager,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add){
            Intent intent = new Intent(CityManager.this,SearchActivity.class);
            startActivityForResult(intent, 1);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1){
            Boolean b = true;
            String id = data.getStringExtra("return_id");
            String district = data.getStringExtra("district");
            for (int i = 0;i < city_list.size();i++){
                if (district.equals(city_list.get(i))){
                    b = false;
                    break;
                }
            }
            if (b){
                city_list.add(district);
                cityId_list.add(id);
                adapter.notifyDataSetChanged();
                Intent intent = new Intent("com.lha.weather.ADD");
                intent.putExtra("city",district);
                intent.putExtra("city_id",id);
                sendBroadcast(intent);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        String color = sharedPreferences.getString("style_color", "");
        switch (color){
            case "蓝色":
                toolbar.setBackgroundColor(Color.parseColor("#104d8e"));
                break;
            case "灰色":
                toolbar.setBackgroundColor(Color.GRAY);
                break;
            case "青色":
                toolbar.setBackgroundColor(Color.parseColor("#FF00786F"));
                break;
            case "绿色":
                toolbar.setBackgroundColor(Color.parseColor("#2e8b57"));
                break;
            case "黑色":
                toolbar.setBackgroundColor(Color.BLACK);
                break;
            case "咖啡色":
                toolbar.setBackgroundColor(Color.parseColor("#5f4421"));
                break;
        }
    }
}
