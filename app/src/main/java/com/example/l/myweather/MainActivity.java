package com.example.l.myweather;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.*;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MenuItem;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;





public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager viewPager;
    private ArrayList<ContentFragment> fragmentArrayList;
    private MyFragmentAdapter mainAdapter;
    private SQLiteDatabase db;
    private List<String> city_list;
    private List<String> cityId_list;
    private int start_count;
    private SharedPreferences startCount;
    private SharedPreferences.Editor editor;
    private TextView tip_text;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;
    private RequestQueue mQueue;
    private ImageLoader imageLoader;
    private List<TextView> cityViewList;
    private List<TextView> weatherViewList;
    private List<LinearLayout> linearLayoutList;
    private SharedPreferences sharedPreferences;
    public static Context context = MyApplication.getContext();
    private Indicator indicator;

    public List<String> picUrl_strings;
    private String currentPicUrl = "FUCK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_main);
        initView();
        initCityList();
        setViewToDrawerLayout();
        initViewPager(0);
        initBroadcast();
        //initBackground(0);
        if (isFirstStart()){
            firstStart();
            editor = startCount.edit();
            start_count++;
            editor.putInt("start_count", start_count);
            editor.apply();
        }
        initIndicator(0);
        if (picUrl_strings.size() > 0){
            initBackground(0);
        }
        startService(new Intent(this, UpdateService.class));
}



    public void initView(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mQueue = Volley.newRequestQueue(this);
        imageLoader = new ImageLoader(mQueue,new BitmapCache());
        relativeLayout = (RelativeLayout)findViewById(R.id.relative_layout);
        fragmentArrayList = new ArrayList<ContentFragment>();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        mainAdapter = new MyFragmentAdapter(getSupportFragmentManager(),fragmentArrayList);
        CityDataBase cityDataBase = CityDataBase.getInstance();
        db = cityDataBase.getWritableDatabase();
        city_list = new ArrayList<String>();
        cityId_list = new ArrayList<String>();
        cityViewList = new ArrayList<TextView>();
        weatherViewList = new ArrayList<TextView>();
        linearLayoutList = new ArrayList<LinearLayout>();
        picUrl_strings = new ArrayList<String>();


        tip_text = (TextView) findViewById(R.id.tip_text);
        tip_text.setOnClickListener(this);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        View view  = navigationView.getHeaderView(0);

        linearLayout = (LinearLayout)view.findViewById(R.id.linear_layout);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);



        navigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(onClickListener);

        viewPager.addOnPageChangeListener(onPageChangeListener);
        viewPager.setOffscreenPageLimit(10);
        indicator = (Indicator)findViewById(R.id.indicator);

        startCount = getPreferences(MODE_APPEND);
        start_count = startCount.getInt("start_count",0);


    }
    public void initCityList(){

        Cursor cursor = db.query("city",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                city_list.add(cursor.getString(cursor.getColumnIndex("city")));
                cityId_list.add(cursor.getString(cursor.getColumnIndex("city_id")));
                picUrl_strings.add("");
                } while (cursor.moveToNext());
        }
        cursor.close();

    }
    public void initViewPager(int flag){
        viewPager.setAdapter(mainAdapter);
        if (city_list.size() > 0){
            for (int i = 0;i < city_list.size();i++){
                Log.d("TAGG", city_list.size() + "");
                ContentFragment fragment = ContentFragment.newInstance(city_list.get(i),cityId_list.get(i),i,flag);
                fragmentArrayList.add(fragment);
            }
            mainAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(0);
            toolbar.setTitle(city_list.get(0));
        } else {
            toolbar.setTitle("请添加城市");
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tip_text:
                startActivity(new Intent(MainActivity.this, AddCityActivity.class));
                break;
        }
    }


    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            toolbar.setTitle(city_list.get(position));
            initIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE){
                initBackground(1);
            }

        }
    };

    public void firstStart(){
        final MyLocation myLocation = new MyLocation();
        myLocation.getUserLocation();

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("自动定位中...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                String city = myLocation.getCity();
                final String district = myLocation.getDistrict();
                if (city == null ){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "定位失败", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else {
                    LocationCityId locationCityId = new LocationCityId();
                    locationCityId.getLocationCityId(city, district, new LocationCallBack() {
                        @Override
                        public void onFinish(String return_id, String city_name) {
                            if (return_id != null) {
                                addCity(city_name,return_id);
                                Toast.makeText(MainActivity.this, "定位成功：" + city_name , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                progressDialog.dismiss();
                }

        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 2000);

    }


    private NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            int i = menuItem.getItemId();
            switch (i){
                case R.id.city_manager:
                    drawerLayout.closeDrawers();
                    drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
                        @Override
                        public void onDrawerSlide(View drawerView, float slideOffset) {

                        }

                        @Override
                        public void onDrawerOpened(View drawerView) {

                        }

                        @Override
                        public void onDrawerClosed(View drawerView) {

                        }

                        @Override
                        public void onDrawerStateChanged(int newState) {
                            if (newState == DrawerLayout.STATE_IDLE){
                                drawerLayout.removeDrawerListener(this);
                                startActivity(new Intent(MainActivity.this, CityManagerActivity.class));

                            }
                        }
                    });

                    break;
                case R.id.add_city:
                    drawerLayout.closeDrawers();
                    drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
                        @Override
                        public void onDrawerSlide(View drawerView, float slideOffset) {

                        }

                        @Override
                        public void onDrawerOpened(View drawerView) {

                        }

                        @Override
                        public void onDrawerClosed(View drawerView) {

                        }

                        @Override
                        public void onDrawerStateChanged(int newState) {
                            if (newState == DrawerLayout.STATE_IDLE) {
                                drawerLayout.removeDrawerListener(this);
                                startActivity(new Intent(MainActivity.this, AddCityActivity.class));

                            }
                        }
                    });
                    break;
                case R.id.settings:
                    drawerLayout.closeDrawers();
                    drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
                        @Override
                        public void onDrawerSlide(View drawerView, float slideOffset) {

                        }

                        @Override
                        public void onDrawerOpened(View drawerView) {

                        }

                        @Override
                        public void onDrawerClosed(View drawerView) {

                        }

                        @Override
                        public void onDrawerStateChanged(int newState) {
                            if (newState == DrawerLayout.STATE_IDLE) {
                                drawerLayout.removeDrawerListener(this);
                                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                            }
                        }
                    });

                    break;
                case R.id.exit:
                    finish();
                    break;
                default:
                    break;
            }
            return true;
        }
    };
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    };


    public void setViewToDrawerLayout(){
        linearLayout.removeAllViews();
        cityViewList.clear();
        weatherViewList.clear();
        linearLayoutList.clear();
        for (int i = 0; i < cityId_list.size(); i++){
            addLayout();  //添加layout
        }
    }

    public void addLayout(){
        final LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.setPadding(20, 0, 20, 0);
        layout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        TextView weather_text = new TextView(MainActivity.this);
        TextView city_text = new TextView(MainActivity.this);
        city_text.setSingleLine(false);
        weather_text.setBackgroundResource(R.drawable.yuanxing);
        weather_text.setLayoutParams(new LinearLayout.LayoutParams(MyApplication.dp2px(50), MyApplication.dp2px(50)));
        weather_text.setTextSize(18);
        city_text.setLayoutParams(new LinearLayout.LayoutParams(MyApplication.dp2px(50), MyApplication.dp2px(50)));
        city_text.setGravity(Gravity.CENTER_HORIZONTAL);
        city_text.setPadding(0,10,0,0);
        weather_text.setGravity(Gravity.CENTER);
        weather_text.setTextColor(Color.WHITE);
        city_text.setTextColor(Color.WHITE);
        layout.addView(weather_text);
        layout.addView(city_text);
        linearLayout.addView(layout);
        cityViewList.add(city_text);
        weatherViewList.add(weather_text);
        linearLayoutList.add(layout);
    }

    public void setView(final int i,String weather){

        cityViewList.get(i).setText(city_list.get(i));
        weatherViewList.get(i).setText(weather);
        linearLayoutList.get(i).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                viewPager.setCurrentItem(i);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(cityBroadCastReceiver);
        unregisterReceiver(cityManagerBroadCastReceiver);
        //unregisterReceiver(removeBroadcastReceiver);
        System.exit(0);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (city_list.size() == 0){
            tip_text.setVisibility(View.VISIBLE);
        } else {
            tip_text.setVisibility(View.GONE);
        }


    }

    public int getBarHeight(){
        int i = toolbar.getHeight();
        return i + indicator.getHeight();
    }


    public void initBroadcast(){

        Log.d("TAG","Broadcast");
        IntentFilter intentFilter = new IntentFilter("com.lha.weather.ADD_CITY");
        registerReceiver(cityBroadCastReceiver,intentFilter);

        IntentFilter intentFilter1 = new IntentFilter("com.lha.weather.CITY_MANAGER");
        registerReceiver(cityManagerBroadCastReceiver,intentFilter1);
    }

    private BroadcastReceiver cityBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String city_name = intent.getStringExtra("city_name");
            String city_id = intent.getStringExtra("city_id");
            addCity(city_name,city_id);
        }
    };

    private BroadcastReceiver cityManagerBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int i = intent.getIntExtra("POSITION",-1);
            switch (intent.getStringExtra("TYPE")){
                case "DELETE":
                    deleteCity(i);
                    break;
                case "CHANGE_DEFAULT":
                    changeDefaultCity(i);
                    break;
            }
        }
    };



    public void deleteCity(int position) {
        if (position >= 0){
            city_list.remove(position);
            cityId_list.remove(position);
            fragmentArrayList.clear();
            picUrl_strings.remove(position);
            initViewPager(1);
            linearLayout.removeViewAt(position);
            cityViewList.remove(position);
            weatherViewList.remove(position);
            linearLayoutList.remove(position);
            if (city_list.size() == 0){
                tip_text.setVisibility(View.VISIBLE);
            }
            viewPager.setCurrentItem(0);
            initIndicator(0);
            if (position == 0){
                WeatherNotification.sendNotification(null,null);
                Intent intent = new Intent("com.lha.weather.UPDATE_FROM_LOCAL");
                sendBroadcast(intent);
            }

        }
    }

    public void changeDefaultCity(int position){
        String city = city_list.get(position);
        String city_id = cityId_list.get(position);
        String url = picUrl_strings.get(position);
        picUrl_strings.remove(position);
        city_list.remove(position);
        cityId_list.remove(position);
        picUrl_strings.add(0,url);
        city_list.add(0, city);
        cityId_list.add(0, city_id);
        fragmentArrayList.clear();
        setViewToDrawerLayout();
        initViewPager(0);
        TextView cityView = cityViewList.get(position);
        TextView weatherView = weatherViewList.get(position);
        LinearLayout layout = linearLayoutList.get(position);
        linearLayout.removeViewAt(position);
        cityViewList.remove(position);
        weatherViewList.remove(position);
        linearLayoutList.remove(position);
        linearLayout.addView(layout, 0);
        linearLayoutList.add(0, layout);
        cityViewList.add(0, cityView);
        weatherViewList.add(0, weatherView);
        startService(new Intent(this,UpdateService.class));
        db.delete("city", null, null);
        ContentValues values = new ContentValues();
        for (int i = 0; i < city_list.size(); i++){
            values.put("city",city_list.get(i));
            values.put("city_id",cityId_list.get(i));
            db.insert("city", null, values);
            values.clear();
        }

        WeatherNotification.sendNotification(null,null);
        Intent intent = new Intent("com.lha.weather.UPDATE_FROM_LOCAL");
        sendBroadcast(intent);
    }

    public void addCity(String city,String id){
        boolean b =true;
        Cursor cursor = db.query("city", new String[]{"city_id"}, null, null, null, null, null);
        if (cursor.moveToFirst()){
            do {
                if (cursor.getString(cursor.getColumnIndex("city_id")).equals(id)){
                    b = false;
                    for (int i = 0;i<city_list.size();i++){
                        if (id.equals(cityId_list.get(i))){
                            Toast.makeText(context,"该城市已存在...",Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        if (b) {
            addLayout();
            ContentValues values = new ContentValues();
            city_list.add(city);
            cityId_list.add(id);
            picUrl_strings.add("");
            values.put("city", city);
            values.put("city_id", id);
            db.insert("city", null, values);
            ContentFragment fragment = ContentFragment.newInstance(city,id,city_list.size()-1,0);
            fragmentArrayList.add(fragment);
            mainAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(city_list.size() - 1);
            initIndicator(city_list.size() - 1);
            toolbar.setTitle(city);
            tip_text.setVisibility(View.GONE);
        }
    }


    public void initIndicator(int pageSelected){
        indicator.setCircleCount(city_list.size());
        indicator.setPageSelected(pageSelected);
        indicator.invalidate();
    }

    public boolean isFirstStart(){
        return start_count == 0;
    }


    public  int getWeatherCode(String weatherName){
        int weatherCode;
        switch (weatherName){
            case "晴":
                weatherCode = 0;
                break;
            case "多云":
                weatherCode = 1;
                break;
            case "少云":
                weatherCode = 1;
                break;
            case "晴间多云":
                weatherCode = 1;
                break;
            case "阴":
                weatherCode = 2;
                break;
            case "阵雨":
                weatherCode = 3;
                break;
            case "强阵雨":
                weatherCode = 3;
                break;
            case "雷阵雨":
                weatherCode = 4;
                break;
            case "强雷阵雨":
                weatherCode = 4;
                break;
            case "雷阵雨伴有冰雹":
                weatherCode = 5;
                break;
            case "雨夹雪":
                weatherCode = 6;
                break;
            case "小雨":
                weatherCode = 7;
                break;
            case "中雨":
                weatherCode = 8;
                break;
            case "大雨":
                weatherCode = 9;
                break;
            case "极端降雨":
                weatherCode = 10;
                break;
            case "毛毛雨":
                weatherCode = 7;
                break;
            case "细雨":
                weatherCode = 7;
                break;
            case "暴雨":
                weatherCode = 10;
                break;
            case "大暴雨":
                weatherCode = 11;
                break;
            case "特大暴雨":
                weatherCode = 12;
                break;
            case "冻雨":
                weatherCode = 19;
                break;
            case "小雪":
                weatherCode = 14;
                break;
            case "中雪":
                weatherCode = 15;
                break;
            case "大雪":
                weatherCode = 16;
                break;
            case "暴雪":
                weatherCode = 17;
                break;
            case "阵雪":
                weatherCode = 13;
                break;
            case "薄雾":
                weatherCode = 18;
                break;
            case "雾":
                weatherCode = 18;
                break;
            case "霾":
                weatherCode = 53;
                break;
            case "扬沙":
                weatherCode = 30;
                break;
            case "浮尘":
                weatherCode = 29;
                break;
            case "火山灰":
                weatherCode = 506;
                break;
            case "沙尘暴":
                weatherCode = 20;
                break;
            case "强沙尘暴":
                weatherCode = 31;
                break;
            default:
                weatherCode = 99;
                break;
        }
        return weatherCode;
    }


    public void initBackground(int flag){


        //relativeLayout.setBackgroundResource(R.drawable.www);
        final int i = viewPager.getCurrentItem();
        if (picUrl_strings.size() >= i && picUrl_strings.size() != 0 && currentPicUrl.equals(picUrl_strings.get(i))){
        } else if (picUrl_strings.size() >= i && picUrl_strings.size() != 0){
            if (picUrl_strings.get(i) != null && !picUrl_strings.get(i).isEmpty()){
                String picUrl = picUrl_strings.get(i);
                setBackgroundPic(picUrl);
            } else {
                String city_id = cityId_list.get(viewPager.getCurrentItem());
                if (city_id != null){
                    final JSONObject object = FileHandle.getJSONObject(city_id);
                    if (object != null){
                        String picUrl = getPicUrl(object);
                        setBackgroundPic(picUrl);
                    }
                }
            }
        }
    }

    public String getPicUrl(JSONObject jsonObject){
        String picUrl;
        try{
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            JSONArray forecast = jsonObject.getJSONArray("forecast");
            JSONObject object = forecast.getJSONObject(1);
            if (hour > 18 || hour < 7){
                picUrl = object.getJSONObject("night").getString("bgPic");
            } else {
                picUrl = object.getJSONObject("day").getString("bgPic");
            }
            return picUrl;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public void setBackgroundPic(String picUrl){

        if (picUrl != null && !picUrl.isEmpty()){
            currentPicUrl = picUrl;
            final String fileName = picUrl.replace("/","").replace(".","").replace(":","");
            Bitmap bitmap = FileHandle.getImage(fileName);
            if (bitmap != null){
                relativeLayout.setBackground(new BitmapDrawable(bitmap));
            } else {
                HttpUtil.makeImageRequest(picUrl, new ImageCallBack() {
                    @Override
                    public void onFinish(Bitmap bitmap) {
                        relativeLayout.setBackground(new BitmapDrawable(bitmap));
                        FileHandle.saveImage(bitmap, fileName);
                    }

                    @Override
                    public void onError() {

                    }
                });
            }
        }
    }

    public void setPicUrl_strings(int i,String picUrl){
        picUrl_strings.set(i,picUrl);
    }

}




