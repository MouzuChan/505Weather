package com.example.l.myweather;


import android.app.AlarmManager;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.IllegalFormatCodePointException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends android.support.v4.app.Fragment implements View.OnClickListener{
    private View view;
    private TextView view_temp;
    private TextView view_weather;
    private TextView view_max_temp;
    private NetworkImageView view_image;
    private SwipeRefreshLayout refreshLayout;
    private String id;
    private int i;
    private MainActivity mainActivity;
    private TextView view_aqi;
    private TextView view_qlty;
    private TextView view_third;
    private TextView view_four;
    //private TextView view_third_night;
    //private TextView view_four_night;

    private TextView view_update;
    private TextView view_fengli;
    private TextView first_day_weather_txt;
    private TextView second_day_weather_txt;
    private TextView third_day_weather_txt;
    private TextView four_day_weather_txt;
    private TextView first_day_max_min_temp;
    private TextView second_day_max_min_temp;
    private TextView third_day_max_min_temp;
    private TextView four_day_max_min_temp;


    private TextView shidu;
    private TextView richu_riluo;
    private TextView qiya;
    private TextView jiangshui;
    private TextView ziwaixian;
    private TextView pm2_5;

    private TextView shushidu;
    private TextView ganmao;
    private TextView chuanyi;
    private TextView yundong;
    private TextView chuxing;
    private TextView xiche;

    private String shushidu_content,ganmao_content,xiche_content,chuanyi_content,chuxing_content,yundong_content;

    private LinearLayout card_life,card_yubao,card_shikuang;
    private LinearLayout card_qita;

    private String city;

    private int[] maxTemp;
    private int[] minTemp;
    private String[] nightWeather;
    private String[] dayWeather;
    private String[] dayWeatherPic;
    private String[] nightWeatherPic;
    private String weather_pic;
    static final int REFRESH = 1;
    static final int START_REFRESH = 2;
    static final int LOCAL_DATA =3;
    private RequestQueue mQueue;
    private ImageLoader imageLoader;
    private Context context = MyApplication.getContext();

    private NetworkImageView[] dayNetworkImageViews;
    //private NetworkImageView[] nightNetworkImageViews;

    private NetworkImageView first_day_image,second_day_image,third_day_image,four_day_image;

    private LinearLayout shushiduLayout,ganmaoLayout,chuanyiLayout,yundongLayout,chuxingLayout,xicheLayout;

    private DialogFragment dialogFragment;

    private SharedPreferences updatePreferences;
    private SharedPreferences sharedPreferences;

    private DayTableFragment dayTableFragment;

    public BlankFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_blank, container, false);
        maxTemp = new int[7];
        minTemp = new int[7];
        nightWeather = new String[7];
        dayWeather = new String[7];
        dayWeatherPic = new String[7];
        nightWeatherPic = new String[7];
        dayNetworkImageViews = new NetworkImageView[4];
        Bundle bundle = getArguments();
        id = bundle.getString("city_id");
        i = bundle.getInt("i");
        Log.d("CreateView",i + "");
        initView();
        initDate();
        initRefreshLayout();
        return view;
    }

    public void initView(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        mQueue = Volley.newRequestQueue(context);
        imageLoader = new ImageLoader(mQueue,new BitmapCache());
        view_temp = (TextView)view.findViewById(R.id.text_temp);
        view_weather = (TextView)view.findViewById(R.id.text_weather);
        view_max_temp = (TextView)view.findViewById(R.id.max_temp);
        view_image = (NetworkImageView)view.findViewById(R.id.image_weather);
        refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(onRefreshListener);
        mainActivity = (MainActivity)getActivity();
        view_third = (TextView) view.findViewById(R.id.third);
        view_four = (TextView) view.findViewById(R.id.four);
        view_update = (TextView) view.findViewById(R.id.view_update);
        view_aqi = (TextView) view.findViewById(R.id.aqi_txt);
        view_fengli = (TextView) view.findViewById(R.id.fengli);
        view_qlty = (TextView) view.findViewById(R.id.aqi);
        first_day_weather_txt = (TextView) view.findViewById(R.id.first_day_weather_txt);
        first_day_max_min_temp = (TextView) view.findViewById(R.id.first_day_max_min_temp);
        second_day_weather_txt = (TextView) view.findViewById(R.id.second_day_weather_txt);
        second_day_max_min_temp = (TextView) view.findViewById(R.id.second_day_max_min_temp);
        third_day_weather_txt = (TextView) view.findViewById(R.id.third_day_weather_txt);
        third_day_max_min_temp = (TextView) view.findViewById(R.id.third_day_max_min_temp);
        four_day_weather_txt = (TextView) view.findViewById(R.id.four_day_weather_txt);
        four_day_max_min_temp = (TextView) view.findViewById(R.id.four_day_max_min_temp);

        first_day_image = (NetworkImageView) view.findViewById(R.id.first_day_weather_image);
        second_day_image = (NetworkImageView) view.findViewById(R.id.second_day_weather_image);
        third_day_image = (NetworkImageView) view.findViewById(R.id.third_day_weather_image);
        four_day_image = (NetworkImageView) view.findViewById(R.id.four_day_weather_image);
        dayNetworkImageViews[0] = first_day_image;
        dayNetworkImageViews[1] = second_day_image;
        dayNetworkImageViews[2] = third_day_image;
        dayNetworkImageViews[3] = four_day_image;
        qiya = (TextView) view.findViewById(R.id.qiya);
        shidu = (TextView) view.findViewById(R.id.shidu);
        richu_riluo = (TextView) view.findViewById(R.id.richu_riluo);
        jiangshui = (TextView) view.findViewById(R.id.jiangshui);
        pm2_5 = (TextView) view.findViewById(R.id.pm2_5);
        ziwaixian = (TextView) view.findViewById(R.id.ziwaixian);

        shushidu = (TextView) view.findViewById(R.id.shushidu);
        ganmao = (TextView) view.findViewById(R.id.ganmao);
        chuanyi = (TextView) view.findViewById(R.id.chuanyi);
        yundong = (TextView) view.findViewById(R.id.yundong);
        chuxing = (TextView) view.findViewById(R.id.chuxing);
        xiche = (TextView) view.findViewById(R.id.xiche);

        card_life = (LinearLayout) view.findViewById(R.id.card_life);
        card_qita = (LinearLayout) view.findViewById(R.id.cart_qita);
        card_yubao = (LinearLayout) view.findViewById(R.id.card_yubao);
        card_shikuang = (LinearLayout) view.findViewById(R.id.shikuang_layout);
        card_shikuang.setOnClickListener(this);
        card_yubao.setOnClickListener(this);
        shushiduLayout = (LinearLayout) view.findViewById(R.id.shushidu_layout);
        ganmaoLayout = (LinearLayout) view.findViewById(R.id.ganmao_layout);
        xicheLayout = (LinearLayout) view.findViewById(R.id.xiche_layout);
        chuxingLayout = (LinearLayout) view.findViewById(R.id.chuxing_layout);
        chuanyiLayout = (LinearLayout) view.findViewById(R.id.chuanyi_layout);
        yundongLayout = (LinearLayout) view.findViewById(R.id.yundong_layout);
        shushiduLayout.setOnClickListener(this);
        ganmaoLayout.setOnClickListener(this);
        xicheLayout.setOnClickListener(this);
        chuxingLayout.setOnClickListener(this);
        chuanyiLayout.setOnClickListener(this);
        yundongLayout.setOnClickListener(this);
        updatePreferences = context.getSharedPreferences("updateTime",Context.MODE_APPEND);
        dialogFragment = new DialogFragment();

    }

    public void getDataFromId(String city_id,final int i){
        String url = "http://apis.baidu.com/showapi_open_bus/weather_showapi/address?areaid="  + city_id + "&needMoreDay=1&needIndex=1";
        HttpUtil.makeBaiduHttpRequest(url, new CallBackListener() {
            @Override
            public void onFinish(JSONObject jsonObject) {
                if (jsonObject != null) {
                    judgeUpdate(jsonObject, i);
                }
            }

            @Override
            public void onError(String e) {
                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                    Toast.makeText(MyApplication.getContext(), "网络错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static BlankFragment newInstance(String city_id,int i){
        BlankFragment blankFragment = new BlankFragment();
        Bundle bundle = new Bundle();
        bundle.putString("city_id",city_id);
        bundle.putInt("i",i);
        blankFragment.setArguments(bundle);
        return blankFragment;
    }

    //初始化日期
    public void initDate(){
        Calendar calendar = Calendar.getInstance();
        String week = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
        if (week.equals("1")){
            view_third.setText("周二");
            view_four.setText("周三");
        } else if (week.equals("2")){
            view_third.setText("周三");
            view_four.setText("周四");
        } else if (week.equals("3")){
            view_third.setText("周四");
            view_four.setText("周五");
        } else if (week.equals("4")){
            view_third.setText("周五");
            view_four.setText("周六");
        } else if (week.equals("5")){
            view_third.setText("周六");
            view_four.setText("周日");
            //view_third_night.setText("周六");
            //view_four_night.setText("周日");
        } else if (week.equals("6")){
            view_third.setText("周日");
            view_four.setText("周一");
            //view_third_night.setText("周日");
            //view_four_night.setText("周一");
        } else if (week.equals("7")){
            view_third.setText("周一");
            view_four.setText("周二");
            //view_third_night.setText("周一");
            //view_four_night.setText("周二");
        }

    }


    public void initRefreshLayout(){
        getDataFromLocal();
        if (id != null && !id.equals("null")){
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                }
            });
            onRefreshListener.onRefresh();
        }
    }



    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getDataFromId(id,REFRESH);

                        }
                    });
                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask,2000);


        }
    };

    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager)mainActivity.getSystemService(mainActivity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public boolean hasUpdate(String city,String time){
        Calendar calendar = Calendar.getInstance();
        int mount = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String oldTime = updatePreferences.getString("update_time" + city, "");
        String newTime = mount + "" + day + time;
        if (oldTime.equals(newTime)){
            return false;
        } else {
            updatePreferences.edit().putString("update_time" + city,newTime).apply();
            return true;
        }


    }


    public void judgeUpdate(JSONObject jsonObject,int i){
        HandleJson handleJson = new HandleJson();
        handleJson.handleJson(jsonObject);
        String city = handleJson.getCity();
        String time = handleJson.getLoc_time();
        if (handleJson.getErr_code().equals("0")){
            if (i == LOCAL_DATA){
                setView(handleJson);
            } else if (i == REFRESH){
                setView(handleJson);
                if (hasUpdate(city,time)){
                    FileHandle.saveJSONObject(jsonObject, id);
                    Toast.makeText(MyApplication.getContext(),"更新成功",Toast.LENGTH_SHORT).show();
                    refreshLayout.setRefreshing(false);
                    updateAppWidget();
                } else {
                    Toast.makeText(MyApplication.getContext(),"数据已最新",Toast.LENGTH_SHORT).show();
                    refreshLayout.setRefreshing(false);
                }

            }
        }  else {
            Toast.makeText(context,handleJson.getErr_msg(),Toast.LENGTH_SHORT).show();
            refreshLayout.setRefreshing(false);
        }

    }

    public void setView(HandleJson handleJson){
        String update_time = handleJson.getLoc_time();
        city = handleJson.getCity();
        String temp = handleJson.getTemp();
        final String weather_txt = handleJson.getWeather_txt();
        String max_temp = handleJson.getMax_tmp();
        weather_pic = handleJson.getWeather_pic();
        String aqi = handleJson.getAqi();
        String fengli = handleJson.getFengli();
        view_fengli.setText("       " + fengli);
        view_weather.setText(weather_txt);
        view_temp.setText(temp);
        view_max_temp.setText(max_temp);
        final String first_day_weather = handleJson.getFirst_weather();
        final String second_day_weather = handleJson.getSecond_weather();
        final String third_day_weather = handleJson.getThird_weather();
        final String four_day_weather = handleJson.getFour_weather();

        final String first_night_weather = handleJson.getFirst_night_weather_txt();
        final String second_night_weather = handleJson.getSecond_night_weather_txt();
        final String third_night_weather = handleJson.getThird_night_weather_txt();
        final String four_night_weather = handleJson.getFour_night_weather_txt();

        String first_day_temp = handleJson.getFirst_day_temp();

        String second_day_temp = handleJson.getSecond_day_temp();
        Log.d("ssss",second_day_temp + "JJJJ");
        String third_day_temp = handleJson.getThird_day_temp();
        String four_day_temp = handleJson.getFour_day_temp();
        String fifth_day_temp = handleJson.getFifth_day_temp();
        String sixth_day_temp = handleJson.getSixth_day_temp();
        String seventh_day_temp = handleJson.getSeventh_day_temp();

        String first_night_temp = handleJson.getFirst_night_temp();
        String second_night_temp = handleJson.getSecond_night_temp();
        String third_night_temp = handleJson.getThird_night_temp();
        String four_night_temp = handleJson.getFour_night_temp();
        String fifth_night_temp = handleJson.getFifth_night_temp();
        String sixth_night_temp = handleJson.getSixth_night_temp();
        String seventh_night_temp = handleJson.getSeventh_night_temp();

        first_day_weather_txt.setText(first_day_weather);
        second_day_weather_txt.setText(second_day_weather);
        third_day_weather_txt.setText(third_day_weather);
        four_day_weather_txt.setText(four_day_weather);

        first_day_max_min_temp.setText(first_day_temp + "°" + "/" + first_night_temp + "°");
        second_day_max_min_temp.setText(second_day_temp + "°" + "/" + second_night_temp + "°");
        third_day_max_min_temp.setText(third_day_temp + "°" + "/" + third_night_temp + "°");
        four_day_max_min_temp.setText(four_day_temp + "°" + "/" + four_night_temp + "°");

        if (first_day_temp != null){
            maxTemp[0] = Integer.valueOf(first_day_temp);
        }
        if (second_day_temp != null){
            maxTemp[1] = Integer.valueOf(second_day_temp);
        }
        if (third_day_temp != null){
            maxTemp[2] = Integer.valueOf(third_day_temp);
        }
        if (four_day_temp != null){
            maxTemp[3] = Integer.valueOf(four_day_temp);
        }
        if (fifth_day_temp != null){
            maxTemp[4] = Integer.valueOf(fifth_day_temp);
        }
        if (sixth_day_temp != null){
            maxTemp[5] = Integer.valueOf(sixth_day_temp);
        }
        if (seventh_day_temp != null){
            maxTemp[6] = Integer.valueOf(seventh_day_temp);
        }


        if (first_night_temp != null){
            minTemp[0] = Integer.valueOf(first_night_temp);
        }
        if (second_night_temp != null){
            minTemp[1] = Integer.valueOf(second_night_temp);
        }
        if (third_night_temp != null){
            minTemp[2] = Integer.valueOf(third_night_temp);
        }
        if (four_night_temp != null){
            minTemp[3] = Integer.valueOf(four_night_temp);
        }
        if (fifth_night_temp != null){
            minTemp[4] = Integer.valueOf(fifth_night_temp);
        }
        if (sixth_night_temp != null){
            minTemp[5] = Integer.valueOf(sixth_night_temp);
        }
        if (seventh_night_temp != null){
            minTemp[6] = Integer.valueOf(seventh_night_temp);
        }

        dayWeather[0] = first_day_weather;
        dayWeather[1] = second_day_weather;
        dayWeather[2] = third_day_weather;
        dayWeather[3] = four_day_weather;
        dayWeather[4] = handleJson.getFifth_day_weather_txt();
        dayWeather[5] = handleJson.getSixth_day_weather_txt();
        dayWeather[6] = handleJson.getSeventh_day_weather_txt();

        nightWeather[0] = first_night_weather;
        nightWeather[1] = second_night_weather;
        nightWeather[2] = third_night_weather;
        nightWeather[3] = four_night_weather;
        nightWeather[4] = handleJson.getFifth_night_weather_txt();
        nightWeather[5] = handleJson.getSixth_night_weather_txt();
        nightWeather[6] = handleJson.getSeventh_night_weather_txt();

        dayWeatherPic = handleJson.getDayWeatherPic().clone();
        nightWeatherPic = handleJson.getNightWeatherPic().clone();

        shidu.setText(handleJson.getShidu());
        qiya.setText(handleJson.getQiya());
        richu_riluo.setText(handleJson.getRichu_riluo());
        jiangshui.setText(handleJson.getJiangshui());
        ziwaixian.setText(handleJson.getZiwaixian());
        pm2_5.setText(handleJson.getPm2_5());

        shushidu.setText(handleJson.getShushidu());
        ganmao.setText(handleJson.getGanmao());
        chuxing.setText(handleJson.getChuxing());
        chuanyi.setText(handleJson.getChuanyi());
        yundong.setText(handleJson.getYundong());
        xiche.setText(handleJson.getXiche());
        view_update.setText("发布于:" + update_time);

        if (aqi != null && !aqi.equals("null")){
            view_aqi.setText("空气指数： " + aqi);
            view_qlty.setText(handleJson.getQlty());
        }
        view_image.setImageUrl(weather_pic, imageLoader);
        getImage();

        xiche_content = handleJson.getXiche_content();
        ganmao_content = handleJson.getGanmao_content();
        shushidu_content = handleJson.getShushidu_content();
        chuxing_content = handleJson.getChuxing_content();
        chuanyi_content = handleJson.getChuanyi_content();
        yundong_content = handleJson.getYundong_content();

        mainActivity.setView(i,weather_txt,weather_pic);



    }


    public void getImage(){
        Log.d("ddda", dayWeatherPic.length + "");
        //Bitmap dayBitmap;
        //Bitmap nightBitmap;
        for (int i = 0; i < 4; i++){
            //dayBitmap = FileHandle.getImage(dayWeather[i]);
            //nightBitmap = FileHandle.getImage(nightWeather[i] + "night");
            //setImage(dayBitmap,nightBitmap,i);
            dayNetworkImageViews[i].setImageUrl(dayWeatherPic[i],imageLoader);

           // nightNetworkImageViews[i].setImageUrl(nightWeatherPic[i], imageLoader);

        }


    }

    @Override
    public void onResume() {
        super.onResume();
        if (!sharedPreferences.getBoolean("life_switch",true)){
            card_life.setVisibility(View.GONE);
        } else {
            card_life.setVisibility(View.VISIBLE);
        }
        if (!sharedPreferences.getBoolean("qita_switch",true)){
            card_qita.setVisibility(View.GONE);
        } else {
            card_qita.setVisibility(View.VISIBLE);
        }
    }
    public void updateAppWidget(){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,NewAppWidget.class));
        if (appWidgetIds.length != 0){
            for (int in = 0; in < appWidgetIds.length; in++){
                NewAppWidget.updateAppWidget(context,appWidgetManager,appWidgetIds[in]);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.card_yubao:
                String color = sharedPreferences.getString("style_color", "蓝色");
                if (sharedPreferences.getBoolean("full_screen",false)){
                    Intent intent = new Intent(context,DayTable.class);
                    intent.putExtra("maxTemp",maxTemp);
                    intent.putExtra("minTemp",minTemp);
                    intent.putExtra("dayWeather",dayWeather);
                    intent.putExtra("dayWeatherPic",dayWeatherPic);
                    intent.putExtra("nightWeather",nightWeather);
                    intent.putExtra("nightWeatherPic",nightWeatherPic);
                    intent.putExtra("city",city);
                    intent.putExtra("color",color);
                    startActivity(intent);
                }
                else {
                    dayTableFragment = DayTableFragment.newInstance(maxTemp,minTemp,dayWeather,dayWeatherPic,nightWeather,nightWeatherPic,city,color);
                    dayTableFragment.show(getFragmentManager(),"di");
                }
                break;
            case R.id.shushidu_layout:
                dialogFragment.setTitle("舒适度");
                dialogFragment.setContent(shushidu_content);
                dialogFragment.show(getFragmentManager(), "Dialog");
                break;
            case R.id.ganmao_layout:
                dialogFragment.setTitle("感冒");
                dialogFragment.setContent(ganmao_content);
                dialogFragment.show(getFragmentManager(),"dialog");
                break;
            case R.id.chuanyi_layout:
                dialogFragment.setTitle("穿衣");
                dialogFragment.setContent(chuanyi_content);
                dialogFragment.show(getFragmentManager(),"dialog");
                break;
            case R.id.chuxing_layout:
                dialogFragment.setTitle("出行");
                dialogFragment.setContent(chuxing_content);
                dialogFragment.show(getFragmentManager(),"dialog");
                break;
            case R.id.yundong_layout:
                dialogFragment.setTitle("运动");
                dialogFragment.setContent(yundong_content);
                dialogFragment.show(getFragmentManager(),"dialog");
                break;
            case R.id.xiche_layout:
                dialogFragment.setTitle("洗车");
                dialogFragment.setContent(xiche_content);
                dialogFragment.show(getFragmentManager(),"dialog");
                break;
        }
    }

    public void getDataFromLocal(){
        JSONObject jsonObject = FileHandle.getJSONObject(id);
        if (jsonObject != null) {
            judgeUpdate(jsonObject,LOCAL_DATA);
            Log.d("KKK", jsonObject.toString());
        }
    }

}
