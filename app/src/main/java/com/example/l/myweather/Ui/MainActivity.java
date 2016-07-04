package com.example.l.myweather.ui;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.l.myweather.WeatherImageUrl;
import com.example.l.myweather.customView.MyScrollView;
import com.example.l.myweather.database.CityDataBase;
import com.example.l.myweather.callback.ImageCallBack;
import com.example.l.myweather.callback.LocationCallBack;
import com.example.l.myweather.LocationCityId;
import com.example.l.myweather.MyApplication;
import com.example.l.myweather.MyFragmentAdapter;
import com.example.l.myweather.MyLocation;
import com.example.l.myweather.R;
import com.example.l.myweather.UpdateService;
import com.example.l.myweather.util.FileHandle;
import com.example.l.myweather.util.HttpUtil;
import com.example.l.myweather.WeatherNotification;
import com.example.l.myweather.customView.Indicator;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
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
    private TextView tip_text;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private LinearLayout linearLayout;
    private LinearLayout contentLayout;
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
    private View headerView;
    private CoordinatorLayout container;
    private ArrayList<String> weatherList;
    public static ArrayList<String> tempList;
    private Uri imageUri;
    private WeatherImageUrl weatherImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_main);
        initView();
        initCityList();
        setViewToDrawerLayout();
        initViewPager(0);
        initBroadcast();
        initIndicator(0);
        if (start_count == 0){
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},0);

            } else {
                firstStart();
            }
         startCount.edit().putInt("start_count", 2).apply();
        }else {
            initLocationCity();
        }
        initHeaderViewBackground();
        startService(new Intent(context,UpdateService.class));
        Intent intent = getIntent();
        if (intent != null){
            if (intent.getAction() != null && intent.getAction().equals("notification")){
                int i = intent.getIntExtra("position",-1);
                if (i >= 2){
                    viewPager.setCurrentItem(i - 2);
                }
            }
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
        container = (CoordinatorLayout) findViewById(R.id.container);
        //location_icon = (ImageView)findViewById(R.id.location_icon);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        contentLayout = (LinearLayout) findViewById(R.id.content_layout);
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

        headerView  = navigationView.getHeaderView(0);
        linearLayout = (LinearLayout)headerView.findViewById(R.id.linear_layout);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        headerView.findViewById(R.id.change_background).setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerToggle.syncState();
        //drawerLayout.addDrawerListener(drawerToggle);
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


    public void initViewPager(final int flag){
        //new Thread(new Runnable() {
            //@Override
            //public void run() {
                viewPager.setAdapter(mainAdapter);
                if (city_list.size() > 0){
                    for (int i = 0;i < city_list.size();i++){
                        ContentFragment fragment = ContentFragment.newInstance(city_list.get(i),cityId_list.get(i),i,flag);
                        fragmentArrayList.add(fragment);
                    }
                    mainAdapter.notifyDataSetChanged();
                    viewPager.setCurrentItem(0);
                    toolbar.setTitle(city_list.get(0));
                } else {
                    toolbar.setTitle("请添加城市");
                }
            //}
        //}).start();

    }

    public void initHeaderViewBackground(){
        Bitmap bitmap = FileHandle.getImage("header_view_background.png");
        if (bitmap != null){
            Resources resources = context.getResources();
            headerView.setBackground(new BitmapDrawable(resources,bitmap));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tip_text:
                startActivity(new Intent(MainActivity.this, AddCityActivity.class));
                break;
            case R.id.change_background:
                //uri = Uri.parse("file:///storage/sdcard0/Pictures/Wallpapers/aaa.jpg");
                //Toast.makeText(context,"FFFF",Toast.LENGTH_SHORT).show();

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);

                builder.setItems(new String[]{"选择图片","恢复默认"},new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:

                                //File fordl = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.lha.weather/files");
                               // File image = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.lha.weather/files","header_view_background.png");
                                File image = new File(MainActivity.this.getExternalFilesDir(null),"header_view_background.png");
                                try {
                                    if (!image.exists()){
                                        image.createNewFile();
                                    }

                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                                imageUri = Uri.fromFile(image);
                                choosePictureForHeardViewBackground();
                                break;
                            case 1:
                                headerView.setBackgroundResource(R.drawable.default_header_background);
                                FileHandle.deleteFile("header_view_background.png");
                                break;
                        }
                    }
                });
                builder.show();
                break;
        }
    }

    public void choosePictureForHeardViewBackground(){
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                if (resultCode == 1){
                    int position = data.getIntExtra("position",0);
                    viewPager.setCurrentItem(position);
                }
                break;
            case 10:
                if (resultCode == RESULT_OK){
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(data.getData(),"image/*");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    intent.putExtra("aspectX", headerView.getWidth());
                    intent.putExtra("aspectY", MyApplication.dp2px(180));
                    intent.putExtra("outputX", headerView.getWidth());
                    intent.putExtra("outputY", MyApplication.dp2px(180));
                    startActivityForResult(intent, 2);
                }
                break;
            case 2:
                if (resultCode == RESULT_OK){
                    Bitmap bitmap = FileHandle.getImage("header_view_background.png");
                    if (bitmap != null){
                        headerView.setBackground(new BitmapDrawable(getResources(),bitmap));
                    }


                }
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
                setListener(i,"");
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(cityBroadCastReceiver);
        unregisterReceiver(cityManagerBroadCastReceiver);
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
        tempList.clear();
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
        //Widget4x2.updateWidgetFromLocal();
        //NewAppWidget.updateFromLocal();
        //AppWidget2x1.updateFromLocal();
        context.sendBroadcast(new Intent("com.lha.weather.UPDATE_FROM_LOCAL"));
        //getWeatherDataFromInternet();
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
            ContentFragment fragment = ContentFragment.newInstance(city,id,city_list.size()-1,3);
            fragmentArrayList.add(fragment);
            mainAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(city_list.size() - 1);
            initIndicator(city_list.size() - 1);
            toolbar.setTitle(city);
            tip_text.setVisibility(View.GONE);
            //getWeatherDataFromInternet();
        }
    }


    public void initIndicator(int pageSelected){
        indicator.setCircleCount(city_list.size());
        indicator.setPageSelected(pageSelected);
        indicator.invalidate();
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
        Snackbar snackbar = Snackbar.make(container, text, Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout sl = (Snackbar.SnackbarLayout)snackbar.getView();
        sl.setBackgroundColor(Color.parseColor("#64000000"));
        snackbar.show();
    }

    public void setWeatherList(int i, String weather){
        weatherList.set(i, weather);
    }

    public void setWeatherImage(final String weather){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (currentWeather == null || !currentWeather.equals(weather)){
                    Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    if (weatherImageUrl == null){
                        weatherImageUrl = new WeatherImageUrl();
                    }
                    String url = weatherImageUrl.get_url(weather,hour);
                    if (!url.isEmpty()){
                        makeImageRequest(url);
                    }
                }
                currentWeather = weather;
            }
        }).start();

    }

    public void makeImageRequest(final String url){
        final String fileName = url.replace(".","").replace(":", "").replace("/","");
        final Bitmap bitmap = FileHandle.getImage(fileName);
        if (bitmap != null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Resources resources = context.getResources();
                    contentLayout.setBackground(new BitmapDrawable(resources,bitmap));
                }
            });

        } else {
            HttpUtil.makeImageRequest(url, new ImageCallBack() {
                @Override
                public void onFinish(Bitmap bitmap) {
                    if (bitmap != null) {
                        Resources resources = context.getResources();
                        contentLayout.setBackground(new BitmapDrawable(resources,bitmap));
                        FileHandle.saveImage(bitmap, fileName);
                    }
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


    public void setListener(final int i,final String activity_name){
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
                if (activity_name.isEmpty()){
                    if (newState == DrawerLayout.STATE_IDLE) {
                        viewPager.setCurrentItem(i);
                    }
                } else {
                    switch (activity_name){
                        case "add":
                            startActivity(new Intent(MainActivity.this, AddCityActivity.class));
                            break;
                        case "manager":
                            startActivityForResult(new Intent(MainActivity.this, CityManagerActivity.class), 0);
                            break;
                        case "settings":
                            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                            break;
                        case "about":
                            startActivity(new Intent(MainActivity.this, AboutActivity.class));
                            break;
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
                    toolbar.setTitle(location_city);
                } else {
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
    }

    private NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            int i = menuItem.getItemId();
            switch (i){
                case R.id.city_manager:
                    drawerLayout.closeDrawers();
                    setListener(0, "manager");
                    //startActivityForResult(new Intent(MainActivity.this, CityManagerActivity.class), 0);
                    break;
                case R.id.add_city:
                    drawerLayout.closeDrawers();
                    setListener(0, "add");
                    //startActivity(new Intent(MainActivity.this, AddCityActivity.class));
                    break;
                case R.id.settings:
                    drawerLayout.closeDrawers();
                    setListener(0, "settings");
                    //startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                    break;
                case R.id.about:
                    drawerLayout.closeDrawers();
                    setListener(0, "about");
                    //startActivity(new Intent(MainActivity.this, AboutActivity.class));
                    break;
                case R.id.exit:
                    finish();
                    System.exit(0);
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.share_menu:
                takeScreenPicture();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void takeScreenPicture(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("请稍等");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File imageFile = new File(MainActivity.this.getExternalCacheDir(), "share.png");
                    if (!imageFile.exists()){
                        imageFile.createNewFile();
                    }
                    View v1 = getWindow().getDecorView();
                    //View v1 = fragmentArrayList.get(getCurrentItem()).getMyScrollView();
                    v1.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                    v1.setDrawingCacheEnabled(false);
                    FileOutputStream outputStream = new FileOutputStream(imageFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 70, outputStream);
                    outputStream.flush();
                    outputStream.close();

                    /*MyScrollView scrollView = fragmentArrayList.get(getCurrentItem()).getMyScrollView();
                    int h = 0;
                    int w = scrollView.getWidth();
                    for (int i = 0; i < scrollView.getChildCount(); i++){
                        h += scrollView.getChildAt(i).getHeight();
                    }

                    //Bitmap b = Bitmap.createBitmap(w,h, Bitmap.Config.RGB_565);
                    bitmap.setHeight(h);
                    bitmap.setWidth(w);
                    Canvas canvas = new Canvas(bitmap);
                    scrollView.draw(canvas);
                    FileOutputStream outputStream = new FileOutputStream(imageFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();*/
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    Uri uri = Uri.fromFile(imageFile);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    progressDialog.cancel();
                    startActivity(Intent.createChooser(intent,"分享到："));
                } catch (Exception e){
                    progressDialog.cancel();
                    e.printStackTrace();
                }
            }
        }).start();
    }


  /* public void getWeatherDataFromInternet(){
       StringBuilder stringBuffer = new StringBuilder();
       for (int i = 0; i < cityId_list.size(); i++){
           if (i == cityId_list.size() - 1){
               stringBuffer.append("cityIds=" + cityId_list.get(i));
           } else {
               stringBuffer.append("cityIds=" + cityId_list.get(i) + "&");
           }
       }
       String url = "http://aider.meizu.com/app/weather/listWeather?" + stringBuffer.toString();

       HttpUtil.makeHttpRequest(url, new CallBackListener() {
           @Override
           public void onFinish(JSONObject jsonObject) {
               try {
                   if (jsonObject.getString("code").equals("200")){
                       Log.d("fuck","fuck");
                       JSONArray value = jsonObject.getJSONArray("value");
                       JSONObject jsonObject1;
                       for (int i = 0; i < value.length(); i++){
                           jsonObject1 = value.getJSONObject(i);
                           fragmentArrayList.get(i).setData(jsonObject1);
                       }
                       FileHandle.saveJSONObject(jsonObject,"weather_data");
                   }
               } catch (Exception e){
                   e.printStackTrace();
               }
           }

           @Override
           public void onError(String e) {
                showSnackbar("网络错误");
           }
       });

   }


    public void getWeatherDataFromLocal(){
        JSONObject jsonObject = FileHandle.getJSONObject("weather_data");
        if (jsonObject != null){
            try {
                Log.d("fuck","fuck");
                if (jsonObject.getString("code").equals("200")){
                    JSONArray value = jsonObject.getJSONArray("value");
                    for (int i = 0; i < city_list.size(); i++){
                        JSONObject jsonObject1 = value.getJSONObject(i);
                        fragmentArrayList.get(i).setData(jsonObject1);
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }


        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                getWeatherDataFromInternet();
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask,2000);
    }

    public int getCityCount(){
        return city_list.size();
    }*/

}

