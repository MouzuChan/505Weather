package com.example.l.myweather;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.l.myweather.customView.AqiArc;
import com.example.l.myweather.customView.ForecastTable;
import com.example.l.myweather.customView.SunRiseAndSet;
import com.example.l.myweather.customView.Table;

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
    private TextView[] forecast_night_views;
    private TextView[] forecast_day_views;
    private ImageView[] imageViews;
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
    private String[] sun_rise_down_time;

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


        //view of forecast_layout
        forecast_table_layout = (LinearLayout) view.findViewById(R.id.forecast_table_layout);

        date_strings = new String[7];
        week_strings = new String[7];

        forecast_day_views = new TextView[7];
        forecast_night_views = new TextView[7];
        imageViews = new ImageView[7];

        forecast_night_views[0] = (TextView) view.findViewById(R.id.date_1);
        forecast_night_views[1] = (TextView) view.findViewById(R.id.date_2);
        forecast_night_views[2] = (TextView) view.findViewById(R.id.date_3);
        forecast_night_views[3] = (TextView) view.findViewById(R.id.date_4);
        forecast_night_views[4] = (TextView) view.findViewById(R.id.date_5);
        forecast_night_views[5] = (TextView) view.findViewById(R.id.date_6);
        forecast_night_views[6] = (TextView) view.findViewById(R.id.date_7);

        forecast_day_views[0] = (TextView) view.findViewById(R.id.week_1);
        forecast_day_views[1] = (TextView) view.findViewById(R.id.week_2);
        forecast_day_views[2] = (TextView) view.findViewById(R.id.week_3);
        forecast_day_views[3] = (TextView) view.findViewById(R.id.week_4);
        forecast_day_views[4] = (TextView) view.findViewById(R.id.week_5);
        forecast_day_views[5] = (TextView) view.findViewById(R.id.week_6);
        forecast_day_views[6] = (TextView) view.findViewById(R.id.week_7);

        imageViews[0] = (ImageView) view.findViewById(R.id.image_1);
        imageViews[1] = (ImageView) view.findViewById(R.id.image_2);
        imageViews[2] = (ImageView) view.findViewById(R.id.image_3);
        imageViews[3] = (ImageView) view.findViewById(R.id.image_4);
        imageViews[4] = (ImageView) view.findViewById(R.id.image_5);
        imageViews[5] = (ImageView) view.findViewById(R.id.image_6);
        imageViews[6] = (ImageView) view.findViewById(R.id.image_7);


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


        /*scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        fab_refresh = (FloatingActionButton) view.findViewById(R.id.fab_refresh);
        fab_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!swipeRefreshLayout.isRefreshing()) {
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                    swipeRefreshLayout.setRefreshing(true);
                    onRefreshListener.onRefresh();
                }
            }
        });*/

        /*
        final float translationY = fab_refresh.getTranslationY();
        OnMyScrollChangeListener onMyScrollChangeListener = new OnMyScrollChangeListener() {
            @Override
            public void onChange(int y, int oldY) {
                Log.d("scrollY", "--- >  " + y);
                int i = y - oldY;
                float newTranslationY = fab_refresh.getTranslationY();
                if (i > 0){
                    if (fab_refresh.getY() < totalHeight - mainActivity.getBarHeight()){
                        fab_refresh.setTranslationY(newTranslationY + i);
                    }

                } else {
                    if (newTranslationY > translationY){
                        if (newTranslationY + i < translationY){
                            fab_refresh.setTranslationY(translationY);
                        } else {
                            fab_refresh.setTranslationY(newTranslationY + i);
                        }

                    }
                }
            }
        };
        MyScrollView myScrollView = (MyScrollView) view.findViewById(R.id.scroll_view);
        myScrollView.setOnMyScrollChangeListener(onMyScrollChangeListener);*/
        final LinearLayout aqi_linear_layout = (LinearLayout) view.findViewById(R.id.aqi_linear_layout);
        aqi_linear_layout.post(new Runnable() {
            @Override
            public void run() {
                aqi_linear_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //int i = now_layout.getHeight() + hour_table_layout.getHeight() + forecast_table_layout.getHeight() +
                        //        mainActivity.getBarHeight() + MyApplication.dp2px(100);
                        //scrollView.scrollTo(0,i);
                        LinearLayout layout = (LinearLayout)view.findViewById(R.id.layout_aqi);
                        layout.getParent().requestChildFocus(layout,layout);
                    }
                });
            }
        });
    }

    public void initDate() {
        TimeAndDate timeAndDate = new TimeAndDate();
        week_strings = timeAndDate.getFullWeek();
        hour = timeAndDate.getHour();
        minute = timeAndDate.getMinute();
        date_strings = timeAndDate.getDateStrings();
        week_strings[0] = "今天";
        week_strings[1] = "明天";
    }

    public void initData(){

        if (flag == 1){
            initDataFromLocal();
        } else {
            initDataFromLocal();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    initDataFromInternet();
                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask, 1500);
        }
    }

    public void initDataFromLocal(){

       // mainActivity.runOnUiThread(new Runnable() {
       //     @Override
        //    public void run() {
            object = FileHandle.getJSONObject(city_id);
            if (object != null){
                jsonHandle(object);
            }
            initHourTable();
      //      }
      //  });
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
                                mainActivity.showSnackbar("刷新成功");
                            }
                            object = jsonObject;
                        } else {
                            if (swipeRefreshLayout.isRefreshing()){
                                mainActivity.showSnackbar("数据已最新");
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
                mainActivity.showSnackbar("网络错误");
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
        forecast_day_strings = jsonHandle.getForecast_day_strings();
        forecast_high_temp = jsonHandle.getForecast_high_temp();
        forecast_low_temp = jsonHandle.getForecast_low_temp();
        setNow_layout();
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
        setIndexLayout();
        mainActivity.setView(position,now_layout_strings[2]);
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

    }

    public void setForecastTable(){

        WeatherToCode weatherToCode = WeatherToCode.newInstance();
        String icon_style = defaultPreferences.getString("icon_style","单色");
        int drawable_id;
        for (int i = 0; i < 7; i++){
            if (forecast_day_strings[i] != null && !forecast_day_strings[i].isEmpty()){
                forecast_day_views[i].setText(week_strings[i] + "\n" + "\n" + forecast_day_strings[i]);
            } else {
                forecast_day_views[i].setText(week_strings[i]);
            }
            forecast_night_views[i].setText(date_strings[i]);
            switch (icon_style){
                case "单色":
                    drawable_id = weatherToCode.getDrawableSmallId(forecast_day_strings[i],12);
                    break;
                case "彩色":
                    drawable_id = weatherToCode.getDrawableId(forecast_day_strings[i],12);
                    break;
                default:
                    drawable_id = weatherToCode.getDrawableSmallId(forecast_day_strings[i],12);
                    break;
            }
            if (drawable_id != 0){
                imageViews[i].setImageResource(drawable_id);
            }
            forecast_night_views[i].setTextColor(Color.WHITE);
            forecast_day_views[i].setTextColor(Color.WHITE);
            forecast_day_views[i].setTextSize(13);
            forecast_night_views[i].setTextSize(13);

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

        forecastTable.setJiange(70);
        forecastTable.setPointCount(7);
        forecastTable.setData(forecast_high_temp, forecast_low_temp);
        forecastTable.setHeight(250);
        if (maxTemp != minTemp){
            float y = 100 / (maxTemp - minTemp);
            for (int i = 0; i < 7; i++){
                forecastTable.addMaxPoint(i, i * 70 + 35, (maxTemp - forecast_high_temp[i]) * y + 75);
                forecastTable.addMinPoint(i, i * 70 + 35, (maxTemp - forecast_low_temp[i]) * y + 75);
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
        for (int i = 0; i < 7; i++){
            if (aqi_strings[i] != null && !aqi_strings[i].isEmpty() && !aqi_strings[i].equals("0")){
                aqi_layout_text_views[i].setText(aqi_strings[i]);
            } else if (i == 6){
                aqi_layout_text_views[i].setText("暂无数据");
            }
        }
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

}
