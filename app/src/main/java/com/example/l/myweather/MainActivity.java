package com.example.l.myweather;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.*;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.l.myweather.customView.Indicator;

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
    public static ArrayList<String> city_list;
    public static ArrayList<String> cityId_list;
    private int start_count;
    private SharedPreferences startCount;
    private SharedPreferences.Editor editor;
    private TextView tip_text;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;
    private List<TextView> cityViewList;
    private List<TextView> weatherViewList;
    private List<LinearLayout> linearLayoutList;
    private SharedPreferences sharedPreferences;
    public static Context context = MyApplication.getContext();
    private Indicator indicator;
    private String currentWeather;
    private String location_city,location_city_id;
    private SharedPreferences preferences;
    private int location_position = -1;
    private ImageView location_icon;



    CoordinatorLayout container;

    private String picDayString = "{\"晴\":\"1452688905.7999\",\"多云\":\"1445588313.6732\",\"阴\":\"1437731894.2717\",\"雷阵雨\":\"1437735721.0812\",\"雾\":\"\",\"沙尘暴\":\"\",\"浮尘\":\"\",\"扬沙\":\"\",\"霾\":\"\",\"强沙尘暴\":\"\"}";
    private JSONObject jsonObject;
    private ArrayList<String> weatherList;
    public static ArrayList<String> tempList;
    private String weatherCode;

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
        initIndicator(0);
        startService(new Intent(this, UpdateService.class));
        if (isFirstStart()){
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},0);

            } else {
                firstStart();
            }
            //firstStart();
            editor = startCount.edit();
            start_count++;
            editor.putInt("start_count", start_count);
            editor.apply();
        }else {


            initLocationCity();
        }

}

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 0:
                if (grantResults.length > 0){
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        firstStart();
                    } else {
                        showSnackbar("授权失败");
                    }
                }

                break;
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    location();
                } else {
                    showSnackbar("授权失败");
                }
                break;
        }

    }

    public void initView(){
        location_icon = (ImageView)findViewById(R.id.location_icon);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
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
        weatherList = new ArrayList<String>();
        tempList = new ArrayList<String>();

        tip_text = (TextView) findViewById(R.id.tip_text);
        tip_text.setOnClickListener(this);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        View view  = navigationView.getHeaderView(0);

        linearLayout = (LinearLayout)view.findViewById(R.id.linear_layout);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);


        navigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);
        viewPager.addOnPageChangeListener(onPageChangeListener);
        viewPager.setOffscreenPageLimit(10);
        indicator = (Indicator)findViewById(R.id.indicator);

        startCount = getPreferences(MODE_APPEND);
        start_count = startCount.getInt("start_count", 0);
        preferences = getSharedPreferences("location_city", MODE_APPEND);

    }
    public void initCityList(){

        Cursor cursor = db.query("city",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                city_list.add(cursor.getString(cursor.getColumnIndex("city")));
                cityId_list.add(cursor.getString(cursor.getColumnIndex("city_id")));
                weatherList.add("");
                tempList.add("");
                } while (cursor.moveToNext());
        }
        cursor.close();

    }
    public void initViewPager(int flag){
        viewPager.setAdapter(mainAdapter);
        if (city_list.size() > 0){
            for (int i = 0;i < city_list.size();i++){
                ContentFragment fragment = ContentFragment.newInstance(city_list.get(i),cityId_list.get(i),i,flag);
                fragmentArrayList.add(fragment);
            }
            mainAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(0);
            toolbar.setTitle(city_list.get(0));
            if (city_list.get(0).equals(location_city)){
                location_icon.setVisibility(View.VISIBLE);
            } else {
                location_icon.setVisibility(View.GONE);
            }
        } else {
            toolbar.setTitle("请添加城市");
            location_icon.setVisibility(View.GONE);
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
            if (city_list.get(position).equals(location_city)){
                location_icon.setVisibility(View.VISIBLE);
            } else {
                location_icon.setVisibility(View.GONE);
            }
            initIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE){
                int position = viewPager.getCurrentItem();
                if (weatherList.size() != 0 && weatherList.size() >= position && weatherList.get(position) != null && !weatherList.get(position).isEmpty()){
                    setWeatherImage(weatherList.get(position));
                }


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
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "定位失败", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else {
                    LocationCityId locationCityId = new LocationCityId();
                    locationCityId.getLocationCityId(city, district, new LocationCallBack() {
                        @Override
                        public void onFinish(String return_id, String city_name) {
                            if (return_id != null) {
                                location_city = city_name;
                                location_city_id = return_id;
                                addCity(city_name, return_id);
                                Toast.makeText(MainActivity.this, "定位成功：" + city_name , Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("location_city", city_name);
                                editor.putString("location_city_id",return_id);
                                editor.apply();
                            }
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(MyApplication.getContext(), "定位失败", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }

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
                    setListener(0,0,"manager");
                    break;
                case R.id.add_city:
                    drawerLayout.closeDrawers();
                    setListener(0, 0, "add");
                    break;
                case R.id.settings:
                    drawerLayout.closeDrawers();
                    setListener(0, 0, "settings");
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
        tempList.set(i, weather);
        linearLayoutList.get(i).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                setListener(1,i,"FUCK");
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
        if (city_list == null || city_list.size() == 0){
            if (tip_text != null){
                tip_text.setVisibility(View.VISIBLE);
            }

        } else {
            if (tip_text != null){
                tip_text.setVisibility(View.GONE);
            }

        }
    }

    public int getBarHeight(){
        int i = toolbar.getHeight();
        return i + indicator.getHeight();
    }


    public void initBroadcast(){
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
            int position = intent.getIntExtra("POSITION",-1);
            switch (intent.getStringExtra("TYPE")){
                case "DELETE":
                    deleteCity(position);
                    break;
                case "CHANGE_DEFAULT":
                    changeDefaultCity();
                    break;
            }
        }
    };



    public void deleteCity(int position) {
        if (position >= 0){
            city_list.remove(position);
            cityId_list.remove(position);
            fragmentArrayList.clear();
            weatherList.remove(position);
            tempList.remove(position);
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
                if (sharedPreferences.getBoolean("show_notification",false)){
                    WeatherNotification.sendNotification(null,null);
                }
                Intent intent = new Intent("com.lha.weather.UPDATE_FROM_LOCAL");
                sendBroadcast(intent);
            }

        }
    }


    public void changeDefaultCity(){
        city_list.clear();
        cityId_list.clear();
        fragmentArrayList.clear();
        linearLayout.removeAllViews();
        cityViewList.clear();
        weatherViewList.clear();
        linearLayoutList.clear();
        initCityList();
        setViewToDrawerLayout();
        initViewPager(1);
        initIndicator(0);
        if (sharedPreferences.getBoolean("show_notification", false)){
            WeatherNotification.sendNotification(null,null);
        }
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
            weatherList.add("");
            tempList.add("");
            values.put("city", city);
            values.put("city_id", id);
            db.insert("city", null, values);
            ContentFragment fragment = ContentFragment.newInstance(city,id,city_list.size()-1,0);
            fragmentArrayList.add(fragment);
            mainAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(city_list.size() - 1);
            if (city.equals(location_city)){
                location_icon.setVisibility(View.VISIBLE);
            }
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

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else{
            super.onBackPressed();
        }
    }

    public void showSnackbar(String text){
        container = (CoordinatorLayout) findViewById(R.id.container);
        Snackbar.make(container,text, Snackbar.LENGTH_LONG).show();
    }

    public void setWeatherList(int i, String weather){
        weatherList.set(i, weather);
    }

    public void setWeatherImage(String weather){
        if (currentWeather == null || !currentWeather.equals(weather)){
            String url;
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            if (weather == null){
                weather = "";
            }
            if (weather.contains("雨") && !weather.contains("雪") && !weather.contains("雷")){
                if (weather.equals("小雨") || weather.equals("阵雨")){
                    weatherCode = "rain";
                } else {
                    weatherCode = "rain_n";
                }
                url = "http://static.etouch.cn/suishen/weather/" + weatherCode + ".jpg";
            } else if (weather.contains("雪")){
                if (hour > 18 || hour < 7){
                    weatherCode = "nighticyrain";
                    url = "http://static.etouch.cn/suishen/weather/" + weatherCode + ".jpg";
                } else if (weather.equals("雨夹雪")){
                    weatherCode = "1446801371.5782";
                    url = "http://static.etouch.cn/imgs/upload/" + weatherCode + ".jpg";
                } else {
                    weatherCode = "1454119940.3162";
                    url = "http://static.etouch.cn/imgs/upload/" + weatherCode + ".jpg";
                }
            }
            else if (weather.equals("浮尘") || weather.equals("雾")){
                if (hour > 18 || hour < 7){
                    weatherCode = "nightfog";
                } else {
                    weatherCode = "fog";
                }
                url = "http://static.etouch.cn/suishen/weather/" + weatherCode + ".jpg";
            } else if (weather.equals("霾")){
                if (hour > 18 || hour < 7){
                    weatherCode = "1442472108.7831";
                } else {
                    weatherCode = "1442471142.3815";
                }
                url = "http://static.etouch.cn/imgs/upload/" + weatherCode + ".jpg";
            }
            else {
                if (weather.equals("阴")){
                    weatherCode = "1437731894.2717";
                } else if (hour > 18 || hour < 7){
                    if (weather.equals("晴") || weather.equals("多云")){
                        weatherCode = "1457433044.263";
                    }
                } else {
                    if (jsonObject == null){
                        try {
                            jsonObject = new JSONObject(picDayString);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    try {
                        weatherCode = jsonObject.getString(weather);
                        if (weatherCode == null || weatherCode.isEmpty()){
                            if (hour > 18 || hour < 7){
                                weatherCode = "1457433044.263";
                            } else {
                                relativeLayout.setBackgroundResource(R.drawable.night_background);
                            }
                        }

                    } catch (Exception e){
                        e.printStackTrace();
                    }

                }
                url = "http://static.etouch.cn/imgs/upload/" + weatherCode + ".jpg";
            }
            makeImageRequest(url);
        }
        currentWeather = weather;
    }

    public void makeImageRequest(final String url){
        final String fileName = url.replace(".","").replace(":", "").replace("/","");
        Bitmap bitmap = FileHandle.getImage(fileName);
        if (bitmap != null){
            relativeLayout.setBackground(new BitmapDrawable(bitmap));
        } else {
            HttpUtil.makeImageRequest(url, new ImageCallBack() {
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
    public int getCurrentItem(){
        return viewPager.getCurrentItem();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case 1:
                int position = data.getIntExtra("position",0);
                viewPager.setCurrentItem(position);
                break;
        }
    }


    public void setListener(final int flag,final int i,final String activity){
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
                drawerLayout.removeDrawerListener(this);
                if (newState == DrawerLayout.STATE_IDLE) {
                    if (flag == 0) {
                        switch (activity) {
                            case "manager":
                                startActivityForResult(new Intent(MainActivity.this, CityManagerActivity.class), 0);
                                break;
                            case "add":
                                startActivity(new Intent(MainActivity.this, AddCityActivity.class));
                                break;
                            case "settings":
                                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                                break;
                            default:
                                break;
                        }
                    } else {
                        viewPager.setCurrentItem(i);
                    }
                }
            }
        });
    }


    public void initLocationCity(){
        location_city = preferences.getString("location_city","");
        location_city_id = preferences.getString("location_city_id","");
        setLocation_position();
        if (sharedPreferences.getBoolean("update_location",true)){
            Log.d("TAG", "KKK");
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},1);
            } else{
                location();
            }
        }

    }

    public void location(){
        final MyLocation myLocation = new MyLocation();
        myLocation.getUserLocation();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                final String city = myLocation.getCity();
                final String district = myLocation.getDistrict();
                if (city != null){
                    final LocationCityId locationCityId = new LocationCityId();
                    locationCityId.getLocationCityId(city, district, new LocationCallBack() {
                        @Override
                        public void onFinish(String return_id,String city_name) {
                            //return_id = "101010200";
                            //city_name = "海淀";
                            if (return_id != null){
                                if (location_position >= 0){
                                    if (!location_city.equals(city_name) && !location_city_id.equals(return_id)){
                                        location_city = city_name;
                                        location_city_id = return_id;
                                        changeLocationCity();
                                        setLocation_position();
                                    }
                                } else {
                                    location_city = city_name;
                                    location_city_id = return_id;
                                    setLocation_position();
                                    if (location_position < 0){
                                        showDialog();
                                    }
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("location_city", location_city);
                                    editor.putString("location_city_id",location_city_id);
                                    editor.apply();
                                }

                            }
                        }

                        @Override
                        public void onError() {

                        }
                    });
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 3000);
    }

    public void setLocation_position(){
        for (int i = 0; i < cityId_list.size(); i++){
            if (location_city_id.equals(cityId_list.get(i))){
                location_position = i;
                if (location_position == viewPager.getCurrentItem()){
                    Log.d("TAG","FUU");
                    toolbar.setTitle(location_city);
                    location_icon.setVisibility(View.VISIBLE);
                } else {
                    location_icon.setVisibility(View.GONE);
                }
                break;
            }
        }
    }


    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示").setMessage("检测到当前位置为：" + location_city + "\n" + "是否添加？");
        builder.setCancelable(false);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showSnackbar(location_city + "    添加成功");
                addCity(location_city, location_city_id);
            }
        });
        builder.show();
    }

    public void changeLocationCity(){
        fragmentArrayList.get(location_position).changeCity(location_city, location_city_id);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("location_city", location_city);
        editor.putString("location_city_id", location_city_id);
        editor.apply();
        String oldCity = city_list.get(location_position);
        String oldId = cityId_list.get(location_position);
        city_list.set(location_position, location_city);
        cityId_list.set(location_position, location_city_id);
        ContentValues values = new ContentValues();
        values.put("city", location_city);
        //values.put("city_id",location_city_id);
        //sqLiteDatabase.update(DEMO_DB_NAME, values, "uid = ? ", new String[]{uid});
        db.update("city",values,"city = ?",new String[]{oldCity});
        values.clear();
        values.put("city_id", location_city_id);
        db.update("city", values, "city_id = ?", new String[]{oldId});
        Log.d("TAG","FUCK");
    }

}




