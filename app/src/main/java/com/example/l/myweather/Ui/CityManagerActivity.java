package com.example.l.myweather.ui;


import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.example.l.myweather.base.BaseActivity;
import com.example.l.myweather.customView.DragListView;
import com.example.l.myweather.util.adapter.MyAdapter;
import com.example.l.myweather.R;

public class CityManagerActivity extends BaseActivity {

    private DragListView listView;
    private static MyAdapter adapter;
    public static Toolbar toolbar;
    private boolean isEditState = false;
    private static CoordinatorLayout coordinatorLayout;
    private Menu menu;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_manager);
        initView();
    }

    public void initView() {
        //fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        toolbar = (Toolbar) findViewById(R.id.manager_toolbar);
        setSupportActionBar(toolbar);
        listView = (DragListView) findViewById(R.id.city_list);
        adapter = new MyAdapter(listView);
        adapter.setIsEditState(isEditState);
        listView.setAdapter(adapter);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.container);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isEditState){
                    Intent intent = new Intent();
                    intent.putExtra("position",position);
                    setResult(RESULT_OK,intent);
                    finish();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_city_manager, menu);
        this.menu = menu;
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

    public static int getToolbarHeight(){
        return toolbar.getHeight();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (isEditState){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            MenuItem item = menu.getItem(0);
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
            isEditState = !isEditState;
            adapter.setIsEditState(isEditState);
            adapter.notifyDataSetChanged();
            listView.setIsEditState(isEditState);
        } else {
            super.onBackPressed();
        }
    }
}
