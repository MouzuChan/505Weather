package com.example.l.myweather;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
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
    FloatingActionButton fab_add;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_city_manager);
        initView();
        setListViewItemClick();
    }

    public void initView() {
        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        toolbar = (Toolbar) findViewById(R.id.manager_toolbar);
        setSupportActionBar(toolbar);
        listView = (DragListView) findViewById(R.id.city_list);
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
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.container);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(CityManagerActivity.this, AddCityActivity.class), 1);
            }
        });
    }

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
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);

                } else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        Snackbar snackbar = Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_LONG).setAction("撤销", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.cancelDelete();
            }
        });
        Snackbar.SnackbarLayout sl = (Snackbar.SnackbarLayout)snackbar.getView();
        sl.setBackgroundColor(Color.parseColor("#B4000000"));
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if (resultCode == 1){
                    adapter.addCity();
                }
                break;
        }
    }
}
