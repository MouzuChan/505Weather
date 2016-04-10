package com.example.l.myweather;


import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;

import java.util.ArrayList;

public class CityManagerActivity extends AppCompatActivity {

    private DragListView listView;
    private static com.example.l.myweather.MyAdapter adapter;
    private Toolbar toolbar;
    private boolean isEditState = false;
    private static CoordinatorLayout coordinatorLayout;

    //private SharedPreferences sharedPreferences;
    //public static String location_city;
    //public static String location_city_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_city_manager);

        //initCityList();
        initView();
        setListViewItemClick();
    }

    public void initView(){
        toolbar = (Toolbar)findViewById(R.id.manager_toolbar);
        setSupportActionBar(toolbar);
        listView = (DragListView)findViewById(R.id.city_list);
        //city_list = new ArrayList<String>();
        //cityId_list = new ArrayList<String>();
        adapter = new com.example.l.myweather.MyAdapter(listView);
        adapter.setIsEditState(isEditState);
        listView.setAdapter(adapter);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.container);

        //sharedPreferences = getSharedPreferences("location_city", MODE_APPEND);
        //location_city = sharedPreferences.getString("location_city","");
        //location_city_id = sharedPreferences.getString("location_city_id","");
    }
    /*public void initCityList(){
        this.city_list = MainActivity.city_list;
        this.cityId_list = MainActivity.cityId_list;
        this.temp_list = MainActivity.tempList;
        /*Cursor cursor = db.query("city", null, null, null, null, null, null);
        if (cursor.moveToFirst()){
            do {
                city_list.add(cursor.getString(cursor.getColumnIndex("city")));
                cityId_list.add(cursor.getString(cursor.getColumnIndex("city_id")));
            } while (cursor.moveToNext());
        }
        adapter.notifyDataSetChanged();
        cursor.close();

    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_city_manager, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_edit:
                isEditState = !isEditState;
                if (isEditState){
                    item.setTitle("确认");
                    item.setIcon(R.drawable.ic_done_white_48dp);
                    toolbar.setNavigationIcon(null);

                } else {
                    toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
                    item.setTitle("编辑");
                    item.setIcon(R.drawable.ic_create_white_48dp);
                    if (MyAdapter.CHANGE_FLAG == 1){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.changeData();
                            }
                        }).start();
                        MyAdapter.CHANGE_FLAG = 0;
                    }
                }
                adapter.setIsEditState(isEditState);
                adapter.notifyDataSetChanged();
                listView.setIsEditState(isEditState);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void setListViewItemClick(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isEditState) {
                    Intent intent = new Intent();
                    intent.putExtra("position", position);
                    setResult(1, intent);
                    finish();
                    //Toast.makeText(CityManagerActivity.this, (String) adapter.getItem(position), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public static void showSnackBar(String text){
        Snackbar.make(coordinatorLayout,text,Snackbar.LENGTH_LONG).setAction("撤销", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.cancelDelete();
            }
        }).show();
    }

}
