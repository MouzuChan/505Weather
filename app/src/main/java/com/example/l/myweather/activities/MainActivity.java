package com.example.l.myweather.activities;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
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
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.*;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.l.myweather.base.BaseActivity;
import com.example.l.myweather.callback.CheckUpdateCallBack;
import com.example.l.myweather.callback.OnRecyclerViewItemClickListener;
import com.example.l.myweather.view.MyDrawerLayout;
import com.example.l.myweather.util.CheckUpdate;
import com.example.l.myweather.util.City;
import com.example.l.myweather.util.LocationCityId;
import com.example.l.myweather.util.MyLocation;
import com.example.l.myweather.util.WeatherImageUrl;
import com.example.l.myweather.database.CityDataBase;
import com.example.l.myweather.callback.ImageCallBack;
import com.example.l.myweather.callback.LocationCallBack;
import com.example.l.myweather.base.MyApplication;
import com.example.l.myweather.view.adapter.HeaderRecyclerViewAdapter;
import com.example.l.myweather.view.adapter.MyFragmentAdapter;
import com.example.l.myweather.R;
import com.example.l.myweather.base.UpdateService;
import com.example.l.myweather.util.FileHandle;
import com.example.l.myweather.util.HttpUtil;
import com.example.l.myweather.base.WeatherNotification;
import com.example.l.myweather.view.Indicator;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;





public class MainActivity extends BaseActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener{

    private int DRAWER_LAYOUT_TYPE = -1;

    final private int DRAWER_LAYOUT_TYPE_0 = 0;
    final private int DRAWER_LAYOUT_TYPE_1 = 1;
    final private int DRAWER_LAYOUT_TYPE_2 = 2;
    final private int DRAWER_LAYOUT_TYPE_3 = 3;
    final private int DRAWER_LAYOUT_TYPE_4 = 4;
    final private int DRAWER_LAYOUT_TYPE_5 = 5;

    private int CLICK_POSITION;


    private ViewPager viewPager;
    private ArrayList<ContentFragment> fragmentArrayList;
    private MyFragmentAdapter mainAdapter;
    private SQLiteDatabase db;
    private int start_count;
    private SharedPreferences startCount;
    private TextView tip_text;
    private MyDrawerLayout drawerLayout;
    private Toolbar toolbar;
    private LinearLayout contentLayout;
    private SharedPreferences sharedPreferences;
    public Context context = MyApplication.getContext();
    private Indicator indicator;
    private String currentWeather;
    private String location_city,location_city_id;
    private SharedPreferences preferences;
    private int location_position = -1;
    private View headerView;
    private CoordinatorLayout container;

    private Uri imageUri;
    private WeatherImageUrl weatherImageUrl;
    private HeaderRecyclerViewAdapter headerRecyclerViewAdapter;
    private RecyclerView recyclerView;

    public static ArrayList<City> cityArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_main);
        initView();
        initCityList();
        initViewPager(0);
        initBroadcast();
        initIndicator(0);
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

    }

    public void initView(){
        container = (CoordinatorLayout) findViewById(R.id.container);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        contentLayout = (LinearLayout) findViewById(R.id.content_layout);
        fragmentArrayList = new ArrayList<>();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        mainAdapter = new MyFragmentAdapter(getSupportFragmentManager(),fragmentArrayList);
        CityDataBase cityDataBase = CityDataBase.getInstance();
        db = cityDataBase.getWritableDatabase();


        cityArrayList = new ArrayList<>();

        tip_text = (TextView) findViewById(R.id.tip_text);
        tip_text.setOnClickListener(this);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        headerView  = navigationView.getHeaderView(0);

        recyclerView = (RecyclerView)headerView.findViewById(R.id.recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(llm);
        headerRecyclerViewAdapter = new HeaderRecyclerViewAdapter(cityArrayList);
        recyclerView.setAdapter(headerRecyclerViewAdapter);
        headerRecyclerViewAdapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                CLICK_POSITION = position;
                DRAWER_LAYOUT_TYPE = DRAWER_LAYOUT_TYPE_5;
                drawerLayout.closeDrawers();
            }

            @Override
            public void onLongClick(View v, int position) {

            }
        });


        drawerLayout = (MyDrawerLayout)findViewById(R.id.drawer_layout);
        headerView.findViewById(R.id.change_background).setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerToggle.syncState();
        drawerLayout.addDrawerListener(drawerToggle);
        viewPager.addOnPageChangeListener(onPageChangeListener);
        viewPager.setOffscreenPageLimit(10);
        indicator = (Indicator)findViewById(R.id.indicator);
        startCount = getPreferences(MODE_APPEND);
        start_count = startCount.getInt("start_count", 0);
        preferences = getSharedPreferences("location_city", MODE_APPEND);

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                switch (DRAWER_LAYOUT_TYPE){
                    case DRAWER_LAYOUT_TYPE_0:
                        startActivity(new Intent(MainActivity.this,AddCityActivity.class));
                        break;
                    case DRAWER_LAYOUT_TYPE_1:
                        startActivityForResult(new Intent(MainActivity.this,CityManagerActivity.class),0);
                        break;
                    case DRAWER_LAYOUT_TYPE_2:
                        startActivity(new Intent(MainActivity.this,SettingsActivity.class));
                        break;
                    case DRAWER_LAYOUT_TYPE_3:
                        startActivity(new Intent(MainActivity.this,AboutActivity.class));
                        break;
                    case DRAWER_LAYOUT_TYPE_4:
                        finish();
                        break;
                    case DRAWER_LAYOUT_TYPE_5:
                        viewPager.setCurrentItem(CLICK_POSITION);
                        break;
                }
                DRAWER_LAYOUT_TYPE = -1;
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    public void initCityList(){
        Cursor cursor = db.query("city",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                City city = new City();
                city.setCityName(cursor.getString(cursor.getColumnIndex("city")));
                city.setCityId(cursor.getString(cursor.getColumnIndex("city_id")));
                city.setCityTemp("");
                city.setCityWeather("");
                cityArrayList.add(city);
                } while (cursor.moveToNext());
        }
        cursor.close();
        headerRecyclerViewAdapter.notifyDataSetChanged();
    }


    public void initViewPager(final int flag){
                viewPager.setAdapter(mainAdapter);
                if (cityArrayList.size() > 0){
                    for (int i = 0;i < cityArrayList.size();i++){
                        ContentFragment fragment = ContentFragment.newInstance(cityArrayList.get(i).getCityName(),
                                cityArrayList.get(i).getCityId(),i,flag);
                        fragmentArrayList.add(fragment);
                    }
                    mainAdapter.notifyDataSetChanged();
                    viewPager.setCurrentItem(0);
                    toolbar.setTitle(cityArrayList.get(0).getCityName());
                } else {
                    toolbar.setTitle("请添加城市");
                }
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
                if (resultCode == RESULT_OK){
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
            toolbar.setTitle(cityArrayList.get(position).getCityName());
            initIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE){
                int position = viewPager.getCurrentItem();
                if (cityArrayList.size() >= position + 1){
                    String weather = cityArrayList.get(position).getCityWeather();
                    setWeatherImage(weather);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(cityBroadCastReceiver);
        unregisterReceiver(cityManagerBroadCastReceiver);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (cityArrayList == null || cityArrayList.size() == 0){
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
        return toolbar.getHeight() + indicator.getHeight();
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
            //city_list.remove(position);
            //cityId_list.remove(position);
            //weatherList.remove(position);
            //tempList.remove(position);
            cityArrayList.remove(position);
            fragmentArrayList.clear();
            initViewPager(1);
            if (cityArrayList.size() == 0){
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
        //city_list.clear();
        //cityId_list.clear();
        //tempList.clear();
        //weatherList.clear();
        cityArrayList.clear();
        fragmentArrayList.clear();

        initCityList();
        initViewPager(1);
        initIndicator(0);
        if (sharedPreferences.getBoolean("show_notification", false)){
            WeatherNotification.sendNotification(null,null);
        }
        context.sendBroadcast(new Intent("com.lha.weather.UPDATE_FROM_LOCAL"));
    }

    public void addCity(String cityName,String cityId){
        boolean b =true;
        for (int i = 0;i < cityArrayList.size();i++){
            if (cityId.equals(cityArrayList.get(i).getCityId())){
                b = false;
                Toast.makeText(MainActivity.this,"该城市已存在...",Toast.LENGTH_SHORT).show();
                break;
            }
        }
        if (b) {
            ContentValues values = new ContentValues();
            City city = new City();
            city.setCityName(cityName);
            city.setCityId(cityId);
            city.setCityTemp("");
            city.setCityWeather("");
            cityArrayList.add(city);
            //city_list.add(cityName);
            //cityId_list.add(cityId);
            //weatherList.add("");
            //tempList.add("");
            values.put("city", cityName);
            values.put("city_id", cityId);
            db.insert("city", null, values);
            ContentFragment fragment = ContentFragment.newInstance(cityName,cityId,cityArrayList.size()-1,3);
            fragmentArrayList.add(fragment);
            mainAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(cityArrayList.size() - 1);
            initIndicator(cityArrayList.size() - 1);
            toolbar.setTitle(cityName);
            tip_text.setVisibility(View.GONE);
        }
    }


    public void initIndicator(int pageSelected){
        indicator.setCircleCount(cityArrayList.size());
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


    @Override
    public void showSnackbar(View v, String content) {
        if (v == null){
            v = container;
        }
        super.showSnackbar(v, content);
    }

    public void setTempList(int i, String temp){
        if (cityArrayList.size() >= i + 1){
            cityArrayList.get(i).setCityTemp(temp);
        }
        headerRecyclerViewAdapter.notifyDataSetChanged();
    }

    public void setWeatherList(int i,String weather){
        if (cityArrayList.size() >= i + 1){
            cityArrayList.get(i).setCityWeather(weather);
        }
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
                    if (url != null && !url.isEmpty()){
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
        for (int i = 0; i < cityArrayList.size(); i++){
            if (location_city_id.equals(cityArrayList.get(i).getCityId())){
                location_position = i;
                if (location_position == viewPager.getCurrentItem()){
                    toolbar.setTitle(location_city);
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
                showSnackbar(container,location_city + "    添加成功");
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
        String oldCity = cityArrayList.get(location_position).getCityName();
        String oldId = cityArrayList.get(location_position).getCityName();
        cityArrayList.get(location_position).setCityName(location_city);
        cityArrayList.get(location_position).setCityId(location_city_id);
        ContentValues values = new ContentValues();
        values.put("city", location_city);
        db.update("city",values,"city = ?",new String[]{oldCity});
        values.clear();
        values.put("city_id", location_city_id);
        db.update("city", values, "city_id = ?", new String[]{oldId});
    }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 0:
                if (grantResults.length > 0){
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        firstStart();
                    } else {
                        showSnackbar(container,"授权失败");

                    }
                }

                break;
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    location();
                } else {
                    showSnackbar(container,"授权失败");
                }
                break;
        }

    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        /*
        *
        * 获得抽屉中RecyclerView的位置
        * 处理滑动冲突
        *
        */

        if (hasFocus){
            int left = recyclerView.getLeft();
            int top = recyclerView.getTop();
            int right = recyclerView.getRight();
            int bottom = recyclerView.getBottom();
            drawerLayout.setRecyclerView(left,top,right,bottom);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int i = item.getItemId();
        switch (i){
            case R.id.add_city:
                DRAWER_LAYOUT_TYPE = DRAWER_LAYOUT_TYPE_0;
                drawerLayout.closeDrawers();
                break;
            case R.id.city_manager:
                DRAWER_LAYOUT_TYPE = DRAWER_LAYOUT_TYPE_1;
                drawerLayout.closeDrawers();
                break;
            case R.id.settings:
                DRAWER_LAYOUT_TYPE = DRAWER_LAYOUT_TYPE_2;
                drawerLayout.closeDrawers();
                break;
            case R.id.about:
                DRAWER_LAYOUT_TYPE = DRAWER_LAYOUT_TYPE_3;
                drawerLayout.closeDrawers();
                break;
            case R.id.exit:
                DRAWER_LAYOUT_TYPE = DRAWER_LAYOUT_TYPE_4;
                drawerLayout.closeDrawers();
                break;
            default:
                break;
        }
        return true;
    }

    public void checkUpdate(){
        //Toast.makeText(MainActivity.this, "正在检查更新...", Toast.LENGTH_SHORT).show();
        CheckUpdate cu = new CheckUpdate();
        cu.sendHttpRequest(new CheckUpdateCallBack() {
            @Override
            public void hasUpdate(String newVersionName, String changelog, final String url) {
                android.support.v7.app.AlertDialog.Builder builder =
                        new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                builder.setTitle("发现新版本:" + newVersionName);
                builder.setMessage(changelog);
                builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                        Uri uri = Uri.parse(url);
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        dm.enqueue(request);
                    }
                });
                builder.setNegativeButton("取消", null);
            }

            @Override
            public void noUpdate() {
                Toast.makeText(MainActivity.this,"已经是最新版本了",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int errorCode) {
                if (errorCode == 1){
                    Toast.makeText(MainActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,"未知错误",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}

