package com.example.l.myweather;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContentFragment extends Fragment{

    private RequestQueue mQueue;
    private ImageLoader imageLoader;

    private Context context = MyApplication.getContext();
    private View view;
    private String city_name,city_id;
    private int position;
    private MainActivity mainActivity;
    private int totalHeight;
    private int totalWidth;
    private int hour,minute;

    private ScrollView scroll_view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AqiArc aqiArc;
    private int flag;
    private JSONObject object,hour_object;
    private SharedPreferences sharedPreferences;

    //now_layout
    private LinearLayout now_layout;
    private TextView[] now_layout_text_views;
    private String[] now_layout_strings;

    private TextView alarm_name;
    private String[] alarm_strings;
    private NetworkImageView alarm_icon;
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
    private TextView[] forecast_night_views;
    private TextView[] forecast_day_views;
    private String[] forecast_night_strings;
    private String[] forecast_day_strings;
    private LinearLayout forecast_table_layout;

    private int[] forecast_high_temp;
    private int[] forecast_low_temp;

    private String[] date_strings;
    private String[] week_strings;

    //aqi_layout
    private LinearLayout aqi_layout;
    private int aqi;
    private String aqi_quality;
    private String[] aqi_strings;
    private TextView[] aqi_layout_text_views;

    //sun_layout
    private SunRiseAndSet sunRiseAndSet;
    private LinearLayout sun_layout;

    //index_layout
    private TextView[] index_layout_text_views;
    private String[] index_strings;
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

        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);


        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        totalHeight = dm.heightPixels;
        totalWidth = dm.widthPixels;
        mainActivity = (MainActivity)getActivity();
        scroll_view = (ScrollView) view.findViewById(R.id.scroll_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        flag = 0;
                        initDate();
                        initDataFromInternet();
                    }
                };
                Timer timer = new Timer();
                timer.schedule(timerTask,2000);

            }
        });



        //view of now_layout
        now_layout = (LinearLayout) view.findViewById(R.id.now_layout);
        now_layout_text_views = new TextView[5];
        now_layout_text_views[0] = (TextView) view.findViewById(R.id.now_weather);
        now_layout_text_views[1] = (TextView) view.findViewById(R.id.now_aqi);
        now_layout_text_views[2] = (TextView) view.findViewById(R.id.now_temp);
        now_layout_text_views[3] = (TextView) view.findViewById(R.id.now_wind);
        now_layout_text_views[4] = (TextView) view.findViewById(R.id.update_time);

        alarm_name = (TextView) view.findViewById(R.id.alarm_name);
        alarm_layout = (LinearLayout) view.findViewById(R.id.alarm_layout);
        alarm_layout.setVisibility(View.GONE);
        mQueue = Volley.newRequestQueue(context);
        imageLoader = new ImageLoader(mQueue,new BitmapCache());
        alarm_icon = (NetworkImageView) view.findViewById(R.id.alarm_icon);
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


        //view of forecast_layout
        forecast_table_layout = (LinearLayout) view.findViewById(R.id.forecast_table_layout);

        date_strings = new String[6];
        week_strings = new String[6];

        forecast_day_views = new TextView[6];
        forecast_night_views = new TextView[6];

        forecast_night_views[0] = (TextView) view.findViewById(R.id.date_1);
        forecast_night_views[1] = (TextView) view.findViewById(R.id.date_2);
        forecast_night_views[2] = (TextView) view.findViewById(R.id.date_3);
        forecast_night_views[3] = (TextView) view.findViewById(R.id.date_4);
        forecast_night_views[4] = (TextView) view.findViewById(R.id.date_5);
        forecast_night_views[5] = (TextView) view.findViewById(R.id.date_6);

        forecast_day_views[0] = (TextView) view.findViewById(R.id.week_1);
        forecast_day_views[1] = (TextView) view.findViewById(R.id.week_2);
        forecast_day_views[2] = (TextView) view.findViewById(R.id.week_3);
        forecast_day_views[3] = (TextView) view.findViewById(R.id.week_4);
        forecast_day_views[4] = (TextView) view.findViewById(R.id.week_5);
        forecast_day_views[5] = (TextView) view.findViewById(R.id.week_6);

        //view of aqi_layout
        aqi_layout = (LinearLayout) view.findViewById(R.id.aqi_layout);
        aqi_strings = new String[7];
        aqi_layout_text_views = new TextView[7];

        aqi_layout_text_views[0] = (TextView) view.findViewById(R.id.pm25);
        aqi_layout_text_views[1] = (TextView) view.findViewById(R.id.pm10);
        aqi_layout_text_views[2] = (TextView) view.findViewById(R.id.so2);
        aqi_layout_text_views[3] = (TextView) view.findViewById(R.id.co);
        aqi_layout_text_views[4] = (TextView) view.findViewById(R.id.no2);
        aqi_layout_text_views[5] = (TextView) view.findViewById(R.id.o3);
        aqi_layout_text_views[6] = (TextView) view.findViewById(R.id.suggest);

        //view of index_layout
        index_layout_text_views = new TextView[9];
        index_layout_text_views[0] = (TextView) view.findViewById(R.id.shushidu);
        index_layout_text_views[1] = (TextView) view.findViewById(R.id.chuanyi);
        index_layout_text_views[2] = (TextView) view.findViewById(R.id.ganmao);
        index_layout_text_views[3] = (TextView) view.findViewById(R.id.xiche);
        index_layout_text_views[4] = (TextView) view.findViewById(R.id.yundong);
        index_layout_text_views[5] = (TextView) view.findViewById(R.id.liangshai);
        index_layout_text_views[6] = (TextView) view.findViewById(R.id.yusan);
        index_layout_text_views[7] = (TextView) view.findViewById(R.id.ziwaixian);
        index_layout_text_views[8] = (TextView) view.findViewById(R.id.huazhuang);

        //view of sun_layout
        sun_layout = (LinearLayout)view.findViewById(R.id.sun_layout);
    }

    public void initDate(){
        week_strings[0] = "昨天";
        week_strings[1] = "今天";
        week_strings[2] = "明天";
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        switch (week){
            case 1:
                week_strings[3] = "周二";
                week_strings[4] = "周三";
                week_strings[5] = "周四";
                break;
            case 2:
                week_strings[3] = "周三";
                week_strings[4] = "周四";
                week_strings[5] = "周五";
                break;
            case 3:
                week_strings[3] = "周四";
                week_strings[4] = "周五";
                week_strings[5] = "周六";
                break;
            case 4:
                week_strings[3] = "周五";
                week_strings[4] = "周六";
                week_strings[5] = "周日";
                break;
            case 5:
                week_strings[3] = "周六";
                week_strings[4] = "周日";
                week_strings[5] = "周一";
                break;
            case 6:
                week_strings[3] = "周日";
                week_strings[4] = "周一";
                week_strings[5] = "周二";
                break;
            case 7:
                week_strings[3] = "周一";
                week_strings[4] = "周二";
                week_strings[5] = "周三";
                break;
        }
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        for (int i = 0; i<6; i++){
            calendar.set(Calendar.DAY_OF_YEAR,day - 1);
            date_strings[i] = calendar.get(Calendar.MONTH) + 1 + "-" + calendar.get(Calendar.DATE);
            day++;
        }
    }

    public void initData(){

        if (initDataFromLocal()){
            initDataFromInternet();
        } else {
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (MyApplication.isConnected()){
                        initDataFromInternet();
                    }
                    else {
                        Toast.makeText(context,"网络错误",Toast.LENGTH_SHORT).show();
                    }
                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask, 2000);
        }
    }

    public boolean initDataFromLocal(){
        JSONObject jsonObject = FileHandle.getJSONObject(city_id);
        if (jsonObject != null){
            jsonHandle(jsonObject);
            this.object = jsonObject;
        }
        initHourTable();
        return jsonObject == null;
    }

    public void initDataFromInternet(){
        String url = "http://zhwnlapi.etouch.cn/Ecalender/api/v2/weather?citykey=" + city_id;
        HttpUtil.makeHttpRequest(url, new CallBackListener() {
            @Override
            public void onFinish(JSONObject jsonObject) {
                if (object != null){
                    if (!jsonObject.toString().equals(object.toString())){
                        jsonHandle(jsonObject);
                        FileHandle.saveJSONObject(jsonObject, city_id);
                        if (position ==0){
                            WeatherNotification.sendNotification(jsonObject,city_name);
                            Intent intent = new Intent("com.lha.weather.UPDATE_FROM_LOCAL");
                            context.sendBroadcast(intent);
                        }
                    } else {
                        Log.d("TAG","NO UPDATE");
                    }
                } else {
                    jsonHandle(jsonObject);
                    FileHandle.saveJSONObject(jsonObject, city_id);
                }
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                    jsonHandle(jsonObject);
                    Toast.makeText(context, "刷新成功", Toast.LENGTH_SHORT).show();
                }
                mainActivity.initBackground(1);

            }

            @Override
            public void onError(String e) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });


        String hourUrl = "http://m.weather.com.cn/mpub/hours/" + city_id + ".html";
        HttpUtil.makeHttpRequest(hourUrl, new CallBackListener() {
            @Override
            public void onFinish(JSONObject jsonObject) {
                try {
                    if (hour_object == null || !hour_object.toString().equals(jsonObject.toString())) {
                        hour_object = jsonObject;
                        hourObjectHandle();
                        FileHandle.saveJSONObject(jsonObject, city_id + "hour");
                    } else {
                        Log.d("TAG", "hour_object -- >  no update");
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
                Log.d("TAG", "initPadding");

                int height = totalHeight - MyApplication.dp2px(205) - mainActivity.getBarHeight();
                now_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
                sharedPreferences.edit().putInt("height",height).apply();

            }
        });

            //hour_layout.setPadding(0, height, 0, 0);
        //}
    }

    public void jsonHandle(JSONObject jsonObject){
        JSONHandle jsonHandle = new JSONHandle(jsonObject);
        jsonHandle.jsonHandle();
        now_layout_strings = jsonHandle.getNow_layout_strings();

        //hour_layout_temp_ints = jsonHandle.getHour_layout_temp_ints();
        //hour_layout_time_strings = jsonHandle.getHour_layout_time_strings();

        forecast_night_strings = jsonHandle.getForecast_night_strings();
        forecast_day_strings = jsonHandle.getForecast_day_strings();

        forecast_high_temp = jsonHandle.getForecast_high_temp();
        forecast_low_temp = jsonHandle.getForecast_low_temp();
        setNow_layout();
        //setTable();
        setForecastTable();
        aqi = jsonHandle.getAqi();
        aqi_quality = jsonHandle.getAqi_quality();

        if (aqi_quality == null){
            aqi_quality = "--";
        }
        aqi_strings = jsonHandle.getAqi_Strings();
        setAqiLayout();
        index_strings = jsonHandle.getIndex_strings();
        setIndexLayout();

        if (flag == 0){
            if (hour > 18 || hour < 7){
                mainActivity.setPicUrl_strings(position,jsonHandle.getPicUrl()[1]);
            } else {
                mainActivity.setPicUrl_strings(position,jsonHandle.getPicUrl()[0]);
            }
            mainActivity.setView(position,now_layout_strings[2]);
        }
        alarm_strings = jsonHandle.getAlarm_strings();
        setAlarm_layout();
        setSunLayout();

    }

    public void setNow_layout(){
        if (hour > 18 || hour < 7){
            if (forecast_night_strings[1] != null && !forecast_night_strings[1].isEmpty()){
                now_layout_strings[0] = forecast_night_strings[1];
            }
        }
        for (int i = 0; i < 5; i++){
            if (now_layout_strings[i] != null && !now_layout_strings[i].isEmpty())
            now_layout_text_views[i].setText(now_layout_strings[i]);
        }
    }

    public void setTable(){
        if (table == null){
            table = new Table(context);
            hour_table_layout.addView(table);
            table.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        }else {
            table.invalidate();
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
                table.addPoint(i, i * 60 + 30, (maxTemp - hour_layout_temp_ints[i]) * y + 50);
            }
        }

    }

    public void setForecastTable(){
        for (int i = 0; i < 6; i++){
            if (forecast_day_strings[i] != null && !forecast_day_strings[i].isEmpty()){
                forecast_day_views[i].setText(week_strings[i] + "\n" + forecast_day_strings[i]);
            } else {
                forecast_day_views[i].setText(week_strings[i]);
            }
            if (forecast_night_strings[i] != null && !forecast_night_strings[i].isEmpty()){
                forecast_night_views[i].setText(forecast_night_strings[i] + "\n" + date_strings[i]);
            } else {
                forecast_night_views[i].setText(date_strings[i]);
            }
            if (i > 0){
                forecast_night_views[i].setTextColor(Color.WHITE);
                forecast_day_views[i].setTextColor(Color.WHITE);
            }

        }
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
        forecastTable.setJiange(60);
        forecastTable.setPointCount(6);
        forecastTable.setData(forecast_high_temp, forecast_low_temp);
        forecastTable.setHeight(250);
        if (maxTemp != minTemp){
            float y = 100 / (maxTemp - minTemp);
            for (int i = 0; i < 6; i++){
                forecastTable.addMaxPoint(i, i * 60 + 30, (maxTemp - forecast_high_temp[i]) * y + 75);
                forecastTable.addMinPoint(i, i * 60 + 30, (maxTemp - forecast_low_temp[i]) * y + 75);
            }
        }

    }


    public void setAqiLayout(){
        if (aqiArc == null){
            aqiArc = new AqiArc(context);
            aqiArc.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,totalWidth - 200));
            aqi_layout.addView(aqiArc);
        } else {
            aqiArc.invalidate();
        }
        aqiArc.setAqi(aqi, aqi_quality);

        for (int i = 0; i < 7; i++){
            if (aqi_strings[i] != null && !aqi_strings[i].isEmpty()){
                aqi_layout_text_views[i].setText(aqi_strings[i]);
            } else if (i == 6){
                aqi_layout_text_views[i].setText("暂无数据");
            }

        }
    }



    public void setSunLayout(){
        if (sunRiseAndSet == null){
            sunRiseAndSet = new SunRiseAndSet(context);
            sunRiseAndSet.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(totalWidth - MyApplication.dp2px(100)) / 2 + MyApplication.dp2px(60)));
            sun_layout.addView(sunRiseAndSet);
        } else {
            sunRiseAndSet.invalidate();
        }
        String sunRiseTime = now_layout_strings[5];
        String sunSetTime = now_layout_strings[6];
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
        }

    }

    public void setIndexLayout(){
        for (int i = 0; i < 9; i++){
            if (index_strings[i] != null && !index_strings[i].isEmpty()){
                index_layout_text_views[i].setText(index_strings[i]);
            }

        }
    }

    public void setAlarm_layout(){
        if (alarm_strings[0] != null && !alarm_strings[0].isEmpty()){
            alarm_name.setText(alarm_strings[0]);
            alarm_layout.setVisibility(View.VISIBLE);
        } else {
            alarm_layout.setVisibility(View.GONE);
        }
        if (alarm_strings[3] != null && !alarm_strings[3].isEmpty()){
            alarm_icon.setImageUrl(alarm_strings[3], imageLoader);
        }
    }

    public static ContentFragment newInstance(String city_name,String city_id,int i,int flag) {
        Bundle args = new Bundle();
        args.putString("city_name",city_name);
        args.putString("city_id",city_id);
        args.putInt("position", i);
        args.putInt("flag",flag);
        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onPause() {
        super.onPause();
        if (swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
