package com.example.l.myweather.activities;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.l.myweather.view.adapter.RecyclerViewAdapter;
import com.example.l.myweather.callback.CallBackListener;
import com.example.l.myweather.callback.OnScrollChangedListener;
import com.example.l.myweather.view.HourTableHorizontalScrollView;
import com.example.l.myweather.view.MyScrollView;
import com.example.l.myweather.util.Divider;
import com.example.l.myweather.FileHandle;
import com.example.l.myweather.HandleJSON;
import com.example.l.myweather.HttpUtil;
import com.example.l.myweather.base.MyApplication;
import com.example.l.myweather.R;
import com.example.l.myweather.TimeAndDate;
import com.example.l.myweather.base.WeatherNotification;
import com.example.l.myweather.view.AqiArc;
import com.example.l.myweather.view.ForecastTable;
import com.example.l.myweather.view.SunRiseAndSet;
import com.example.l.myweather.view.Table;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContentFragment extends Fragment{

    private Context context = MyApplication.getContext();
    private View view;
    private String city_name,city_id;
    private int position,flag;
    private MainActivity mainActivity;
    private int totalHeight;
    private int totalWidth;
    private int hour,minute;

    private SwipeRefreshLayout swipeRefreshLayout;
    private AqiArc aqiArc;
    private JSONObject object,hour_object;
    private SharedPreferences sharedPreferences;
    private SharedPreferences defaultPreferences;

    //private ScrollView scrollView;
    //private FloatingActionButton fab_refresh;
    //private ScrollView scrollView;
    //now_layout
    private LinearLayout now_layout;
    private TextView[] now_layout_text_views;
    private String[] now_layout_strings;

    private TextView alarm_name;
    private String[] alarm_strings;
    private LinearLayout alarm_layout;
    //hour_layout
    private String[] hour_layout_time_strings;
    private int[] hour_layout_temp_ints;
    private Table table;
    private LinearLayout hour_table_layout;
    private String[] hour_layout_weather_strings;
    private JSONObject codeToWeather;
    //forecast_layout

    private ForecastTable forecastTable;

    private String[] forecast_day_strings;
    private LinearLayout forecast_table_layout;

    private int[] forecast_high_temp;
    private int[] forecast_low_temp;

    //aqi_layout
    private LinearLayout aqi_layout;
    private int aqi;
    private String aqi_quality;
    private String[] aqi_strings;
    private String[] aqi_name = new String[]{"PM2.5","PM10","SO2","CO","NO2","O3"};
    private RecyclerView aqiRecyclerView;
    private RecyclerViewAdapter aqiRecyclerViewAdapter;

    //sun_layout
    private SunRiseAndSet sunRiseAndSet;
    private LinearLayout sun_layout;
    private String[] sun_rise_down_time;

    //index_layout
    private String[] index_strings;
    private RecyclerView indexRecyclerView;
    private RecyclerViewAdapter indexRecyclerViewAdapter;
    private String[] index_name = new String[]{"舒适度","穿衣","感冒","洗车","运动","晾晒","雨伞","紫外线","化妆"};

    private MyScrollView myScrollView;


    public ContentFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_content, container, false);
        Bundle bundle = getArguments();
        city_name = bundle.getString("city_name");
        city_id = bundle.getString("city_id");
        position = bundle.getInt("position");
        flag = bundle.getInt("flag");
        initView();
        initDate();
        initData();
        sharedPreferences = context.getSharedPreferences("height",Context.MODE_APPEND);
        int height = sharedPreferences.getInt("height",0);
        if (height == 0){
            initPadding();
        } else {
            now_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
        }



        return view;
    }




    public void initView(){
        myScrollView = (MyScrollView) view.findViewById(R.id.scroll_view);

        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        totalHeight = dm.heightPixels;
        totalWidth = dm.widthPixels;
        mainActivity = (MainActivity)getActivity();
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //scrollView = (ScrollView) view.findViewById(R.id.scroll_view);

        final SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {

                        initDate();
                        initDataFromInternet();
                    }
                };
                Timer timer = new Timer();
                timer.schedule(timerTask,2000);
            }
        };
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);



        //view of now_layout
        now_layout = (LinearLayout) view.findViewById(R.id.now_layout);
        now_layout_text_views = new TextView[4];
        now_layout_text_views[0] = (TextView) view.findViewById(R.id.now_weather);
        now_layout_text_views[1] = (TextView) view.findViewById(R.id.now_aqi);
        now_layout_text_views[2] = (TextView) view.findViewById(R.id.now_temp);
        now_layout_text_views[3] = (TextView) view.findViewById(R.id.update_time);

        alarm_name = (TextView) view.findViewById(R.id.alarm_name);
        alarm_layout = (LinearLayout) view.findViewById(R.id.alarm_layout);
        alarm_layout.setVisibility(View.GONE);
        alarm_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alarm_strings[1] != null && !alarm_strings[1].isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                    builder.setTitle(alarm_strings[1]);
                    if (alarm_strings[2] != null){
                        builder.setMessage(alarm_strings[2]);
                    }
                    builder.create().show();
                }

            }
        });

        //view of hour_layout
        hour_table_layout = (LinearLayout) view.findViewById(R.id.hour_table_layout);
        String jsonMessage = "{\"00\":\"晴\",\"01\":\"多云\",\"02\":\"阴\",\"03\":\"阵雨\",\"04\":\"雷阵雨\",\"05\":\"雷阵雨伴有冰雹\",\"06\":\"雨夹雪\",\"07\":\"小雨\",\"08\":\"中雨\",\"09\":\"大雨\",\"10\":\"暴雨\",\"11\":\"大暴雨\",\"12\":\"特大暴雨\",\"13\":\"阵雪\",\"14\":\"小雪\",\"15\":\"中雪\",\"16\":\"大雪\",\"17\":\"暴雪\",\"18\":\"雾\",\"19\":\"冻雨\",\"20\":\"沙尘暴\",\"21\":\"小到中雨\",\"22\":\"中到大雨\",\"23\":\"大到暴雨\",\"24\":\"暴雨到大暴雨\",\"25\":\"大暴雨到特大暴雨\",\"26\":\"小到中雪\",\"27\":\"中到大雪\",\"28\":\"大到暴雪\",\"29\":\"浮尘\",\"30\":\"扬沙\",\"31\":\"强沙尘暴\",\"53\":\"霾\",\"99\":\"\"}";
        try {
            codeToWeather = new JSONObject(jsonMessage);
        } catch (Exception e){
            e.printStackTrace();
        }


        forecast_table_layout = (LinearLayout) view.findViewById(R.id.forecast_table_layout);




        //view of sun_layout
        sun_layout = (LinearLayout)view.findViewById(R.id.sun_layout);
        aqi_layout = (LinearLayout) view.findViewById(R.id.aqi_layout);
        aqiRecyclerView = (RecyclerView) view.findViewById(R.id.aqi_recycler_view);
        aqiRecyclerView.setLayoutManager(new GridLayoutManager(mainActivity,3));
        Divider divider = new Divider(mainActivity,3);
        aqiRecyclerView.addItemDecoration(divider);
        aqiRecyclerViewAdapter = new RecyclerViewAdapter(aqi_name);
        aqiRecyclerView.setAdapter(aqiRecyclerViewAdapter);


        indexRecyclerView = (RecyclerView) view.findViewById(R.id.index_recycle_view);
        indexRecyclerView.setLayoutManager(new GridLayoutManager(mainActivity,3));
        indexRecyclerView.addItemDecoration(divider);
        indexRecyclerViewAdapter = new RecyclerViewAdapter(index_name);
        indexRecyclerView.setAdapter(indexRecyclerViewAdapter);


        final LinearLayout aqi_linear_layout = (LinearLayout) view.findViewById(R.id.aqi_linear_layout);
        aqi_linear_layout.post(new Runnable() {
            @Override
            public void run() {
                aqi_linear_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout layout = (LinearLayout)view.findViewById(R.id.layout_aqi);
                        layout.getParent().requestChildFocus(layout,layout);
                    }
                });
            }
        });

        HourTableHorizontalScrollView hourTableHorizontalScrollView = (HourTableHorizontalScrollView) view.findViewById(R.id.hour_scroll_view);
        hourTableHorizontalScrollView.setOnScrollChangedListener(new OnScrollChangedListener() {
            @Override
            public void onChange(int x, int oldX) {
                if (table != null){
                    table.setX(x);
                }
            }
        });

    }



    public void initDate() {
        TimeAndDate timeAndDate = new TimeAndDate();
        hour = timeAndDate.getHour();
        minute = timeAndDate.getMinute();
    }

    public void initData(){
        if (flag == 1){
            initDataFromLocal();
        } else {
            initDataFromLocal();
            initDataFromInternet();
        }
    }

    public void initDataFromLocal(){
        object = FileHandle.getJSONObject(city_id);
        if (object != null){
            jsonHandle(object);
        }
        initHourTable();
    }

    public void initDataFromInternet(){
        String url = "http://aider.meizu.com/app/weather/listWeather?cityIds=" + city_id;
        HttpUtil.makeHttpRequest(url, new CallBackListener() {
            @Override
            public void onFinish(JSONObject object1) {
                try {
                    if (object1.getString("code").equals("200")){
                        JSONObject jsonObject = object1.getJSONArray("value").getJSONObject(0);
                        if (object == null || !jsonObject.toString().equals(object.toString())){
                            jsonHandle(jsonObject);
                            object = jsonObject;
                            if (position == 0){
                                updateWidget(jsonObject);
                            }
                            if (swipeRefreshLayout.isRefreshing()){
                                mainActivity.showSnackbar(null,"刷新成功");
                            }
                            object = jsonObject;
                        } else {
                            if (swipeRefreshLayout.isRefreshing()){
                                mainActivity.showSnackbar(null,"数据已最新");
                            }
                        }
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        FileHandle.saveJSONObject(jsonObject, city_id);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(String e) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                mainActivity.showSnackbar(null,"网络错误");
            }
        });

        getHourDataFromInternet();
    }




    public void getHourDataFromInternet(){
        String hourUrl = "http://m.weather.com.cn/mpub/hours/" + city_id + ".html";
        HttpUtil.makeHttpRequest(hourUrl, new CallBackListener() {
            @Override
            public void onFinish(JSONObject jsonObject) {
                try {
                    if (hour_object == null || !hour_object.toString().equals(jsonObject.toString())) {
                        hour_object = jsonObject;
                        hourObjectHandle();
                        FileHandle.saveJSONObject(jsonObject, city_id + "hour");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String e) {

            }
        });
    }

    public void updateWidget(JSONObject jsonObject){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.getBoolean("show_notification",false)){
            WeatherNotification.sendNotification(jsonObject,city_name);
        }
        Intent intent = new Intent("com.lha.weather.UPDATE_FROM_LOCAL");
        intent.putExtra("data",jsonObject.toString());
        intent.putExtra("city",city_name);
        context.sendBroadcast(intent);
    }

    public void initHourTable(){
        hour_object = FileHandle.getJSONObject(city_id + "hour");
        if (hour_object != null){
            hourObjectHandle();
        }
    }

    public void hourObjectHandle(){
        try {
            JSONArray jh = hour_object.getJSONArray("jh");
            int length = jh.length();
            if (hour_layout_temp_ints == null){
                hour_layout_temp_ints = new int[length];
            }
            if (hour_layout_time_strings == null){
                hour_layout_time_strings = new String[length];
            }
            if (hour_layout_weather_strings == null){
                hour_layout_weather_strings = new String[length];
            }
            for (int i = 0; i < length; i++){
                hour_layout_temp_ints[i] = jh.getJSONObject(i).getInt("jb");
                String time = jh.getJSONObject(i).getString("jf");
                int timeLength = time.length();
                hour_layout_time_strings[i] = time.substring(timeLength - 4, timeLength - 2) + ":" + time.substring(timeLength - 2, timeLength);
                hour_layout_weather_strings[i] = codeToWeather.getString(jh.getJSONObject(i).getString("ja"));
            }
            setTable();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void initPadding(){
        now_layout.post(new Runnable() {
            @Override
            public void run() {
                int height = totalHeight - MyApplication.dp2px(205) - mainActivity.getBarHeight();
                now_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
                sharedPreferences.edit().putInt("height",height).apply();

            }
        });
    }

    public void jsonHandle(JSONObject jsonObject){
        HandleJSON jsonHandle = new HandleJSON(jsonObject);
        jsonHandle.handleForecastData();
        now_layout_strings = jsonHandle.getNow_layout_strings();
        setNow_layout();

        forecast_day_strings = jsonHandle.getForecast_day_strings();
        forecast_high_temp = jsonHandle.getForecast_high_temp();
        forecast_low_temp = jsonHandle.getForecast_low_temp();
        setForecastTable();

        aqi_strings = jsonHandle.getAqi_Strings();
        aqi = jsonHandle.getAqi();
        aqi_quality = jsonHandle.getAqiQuality();
        if (aqi_quality == null){
            aqi_quality = "--";
        }
        alarm_strings = jsonHandle.getAlarm_strings();
        setAlarm_layout();
        setAqiLayout();
        sun_rise_down_time = jsonHandle.getSun_rise_down_time();
        setSunLayout();
        index_strings = jsonHandle.getIndex_strings();

        indexRecyclerViewAdapter.setData1(index_strings);
        aqiRecyclerViewAdapter.setData1(aqi_strings);

        aqiRecyclerViewAdapter.notifyDataSetChanged();
        indexRecyclerViewAdapter.notifyDataSetChanged();

        mainActivity.setTempList(position,now_layout_strings[2]);
        mainActivity.setWeatherList(position,now_layout_strings[0]);
        if (position == mainActivity.getCurrentItem()){
            mainActivity.setWeatherImage(now_layout_strings[0]);
        }
    }

    public void setNow_layout(){
        for (int i = 0; i < 4; i++){
            if (now_layout_strings[i] != null && !now_layout_strings[i].isEmpty())
            now_layout_text_views[i].setText(now_layout_strings[i]);
        }
    }

    public void setTable(){
        if (table == null){
            table = new Table(context,totalWidth);
            hour_table_layout.addView(table);
            table.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        }
        int maxTemp = -100;
        int minTemp = 100;
        table.setJiange(60);
        table.setPointCount(hour_layout_temp_ints.length);
        table.setData(hour_layout_time_strings, hour_layout_weather_strings,hour_layout_temp_ints);

        for (int i : hour_layout_temp_ints){

            if (i > maxTemp){
                maxTemp = i;
            }
            if (i < minTemp){
                minTemp = i;
            }
        }

        if (!(maxTemp - minTemp == 0)){
            float y = 60 / (maxTemp - minTemp);
            for (int i = 0; i < hour_layout_temp_ints.length; i++){
                table.addPoint(i, i * 60 + 30, (maxTemp - hour_layout_temp_ints[i]) * y + 30);
            }
        }
        table.initPoint();

        switch (defaultPreferences.getString("icon_style","单色")){
            case "单色":
                table.initBitmap(1);
                break;
            case "彩色":
                table.initBitmap(0);
                break;
            default:
                table.initBitmap(1);
                break;
        }

        table.invalidate();

    }

    public void setForecastTable(){
        int maxTemp = -100;
        int minTemp = 100;
        for (int i : forecast_high_temp){
            if (i > maxTemp) {
                maxTemp = i;
            }
        }
        for (int i : forecast_low_temp){
            if (i < minTemp) {
                minTemp = i;
            }
        }

        if (forecastTable == null){
            forecastTable = new ForecastTable(context);
            forecast_table_layout.addView(forecastTable);
        } else {
            forecastTable.invalidate();
        }
        forecastTable.setWeatherData(forecast_day_strings);
        forecastTable.setJiange(70);
        forecastTable.setPointCount(7);
        forecastTable.setData(forecast_high_temp, forecast_low_temp);
        forecastTable.setHeight(300);

        switch (defaultPreferences.getString("icon_style","单色")){
            case "单色":
                forecastTable.initBitmap(1);
                break;
            case "彩色":
                forecastTable.initBitmap(0);
                break;
            default:
                forecastTable.initBitmap(1);
                break;
        }

        if (maxTemp != minTemp){
            float y = 80 / (maxTemp - minTemp);
            for (int i = 0; i < 7; i++){
                forecastTable.addMaxPoint(i, i * 70 + 35, (maxTemp - forecast_high_temp[i]) * y + 110);
                forecastTable.addMinPoint(i, i * 70 + 35, (maxTemp - forecast_low_temp[i]) * y + 110);
            }
        }

    }



    public void setAqiLayout(){
        if (aqiArc == null){
            aqiArc = new AqiArc(context);
            aqiArc.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,totalWidth - MyApplication.dp2px(80)));
            aqi_layout.addView(aqiArc);
        } else {
            aqiArc.invalidate();
        }
        aqiArc.setAqi(aqi, aqi_quality);
        ImageView imageView = (ImageView) view.findViewById(R.id.aqi_icon);
        switch (aqi_quality){
            case "优":
                imageView.setImageResource(R.drawable.tree_leaf_1);
                break;
            case "良":
                imageView.setImageResource(R.drawable.tree_leaf_2);
                break;
            case "轻度污染":
                imageView.setImageResource(R.drawable.tree_leaf_3);
                break;
            case "中度污染":
                imageView.setImageResource(R.drawable.tree_leaf_4);
                break;
            case "重度污染":
                imageView.setImageResource(R.drawable.tree_leaf_5);
                break;
            case "严重污染":
                imageView.setImageResource(R.drawable.tree_leaf_6);
                break;
            default:
                imageView.setImageResource(R.drawable.tree_leaf_1);
                break;
        }


    }



    public void setSunLayout(){
        if (sunRiseAndSet == null){
            sunRiseAndSet = new SunRiseAndSet(context);
            sunRiseAndSet.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,totalWidth / 4 + MyApplication.dp2px(10)));
            sun_layout.addView(sunRiseAndSet);
        } else {
            sunRiseAndSet.invalidate();
        }
        String sunRiseTime = sun_rise_down_time[0];
        String sunSetTime = sun_rise_down_time[1];
        if (sunRiseTime != null && sunSetTime != null){
            sunRiseAndSet.setString(sunRiseTime, sunSetTime);
            int sunRiseHour = Integer.valueOf(sunRiseTime.substring(0,2));
            int sunRiseMinute = Integer.valueOf(sunRiseTime.substring(sunRiseTime.length() - 2,sunRiseTime.length()));
            int sunSetHour = Integer.valueOf(sunSetTime.substring(0,2));
            int sunSetMinute = Integer.valueOf(sunSetTime.substring(sunSetTime.length() - 2,sunSetTime.length()));
            float sunRise = (float)sunRiseHour + (float)sunRiseMinute / 60;
            float sunSet = (float) sunSetHour + (float)sunSetMinute / 60;
            float totalTime = sunSet - sunRise;
            float nowTime = hour + (float)minute / 60 - sunRise;
            sunRiseAndSet.setTime(totalTime,nowTime);
        } else{
            sunRiseAndSet.setString("--", "--");
            sunRiseAndSet.setTime(0,0);
        }
        TextView body_temp = (TextView) view.findViewById(R.id.body_temp);
        TextView wind = (TextView) view.findViewById(R.id.wind);
        TextView shidu = (TextView) view.findViewById(R.id.shidu);

        body_temp.setText(sun_rise_down_time[2]);
        wind.setText(sun_rise_down_time[3]);
        shidu.setText(sun_rise_down_time[4]);
    }

    public void setAlarm_layout(){
        if (alarm_strings[0] != null && !alarm_strings[0].isEmpty()){
            alarm_name.setText(alarm_strings[0]);
            alarm_layout.setVisibility(View.VISIBLE);
        } else {
            alarm_layout.setVisibility(View.GONE);
        }
    }


    public static ContentFragment newInstance(String city_name,String city_id,int i,int flag) {
        Bundle args = new Bundle();
        args.putString("city_name",city_name);
        args.putString("city_id", city_id);
        args.putInt("position", i);
        args.putInt("flag", flag);
        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(args);
        return fragment;
    }



    public void changeCity(String city_name,String city_id){
        this.city_id = city_id;
        this.city_name = city_name;
        initDataFromInternet();
    }


    public MyScrollView getMyScrollView() {
        return myScrollView;
    }
}
