package com.example.l.myweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;

public class DayTable extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    private int[] maxTemp;
    private int[] minTemp;
    private NetworkImageView[] dayNetworkImageViews;
    private NetworkImageView[] nightNetworkImageViews;
    private TextView first_night_weather_txt,second_night_weather_txt,third_night_weather_txt,four_night_weather_txt,fifth_night_weather_txt,sixth_night_weather_txt,seventh_night_weather_txt;
    private TextView first_day_weather_txt,second_day_weather_txt,third_day_weather_txt,four_day_weather_txt,fifth_day_weather_txt,sixth_day_weather_txt,seventh_day_weather_txt;
    private NetworkImageView first_day_image,second_day_image,third_day_image,four_day_image,fifth_day_image,sixth_day_image,seventh_day_image;
    private NetworkImageView first_night_image,second_night_image,third_night_image,four_night_image,fifth_night_image,sixth_night_image,seventh_night_image;

    TextView third_day_week;
    TextView four_day_week;
    TextView fifth_day_week;
    TextView sixth_day_week;
    TextView seventh_day_week;

    private TextView title_view;

    private String[] dayWeatherPic;
    private String[] nightWeatherPic;
    private String[] dayWeather;
    private String[] nightWeather;
    private TextView[] riqi_textViews;

    private RequestQueue mQueue;
    private ImageLoader imageLoader;
    private Table table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_day_table);
        table = (Table) findViewById(R.id.table);
        Intent intent = getIntent();
        maxTemp = intent.getIntArrayExtra("maxTemp");
        minTemp = intent.getIntArrayExtra("minTemp");
        initTableView();
        initView();
        initDate();
        setWeatherData(intent);

    }
    public void initView(){
        mQueue = Volley.newRequestQueue(this);
        imageLoader = new ImageLoader(mQueue,new BitmapCache());
        dayNetworkImageViews = new NetworkImageView[7];
        nightNetworkImageViews = new NetworkImageView[7];
        riqi_textViews = new TextView[7];
        relativeLayout = (RelativeLayout) findViewById(R.id.relative_layout);

        title_view = (TextView) findViewById(R.id.title);

        TextView first_day= (TextView) findViewById(R.id.first_day);
        TextView second_day = (TextView) findViewById(R.id.second_day);
        TextView third_day = (TextView) findViewById(R.id.third_day);
        TextView four_day = (TextView) findViewById(R.id.four_day);
        TextView fifth_day = (TextView) findViewById(R.id.fifth_day);
        TextView sixth_day = (TextView) findViewById(R.id.sixth_day);
        TextView seventh_day = (TextView) findViewById(R.id.seventh_day);

        third_day_week = (TextView) findViewById(R.id.third_day_week);
        four_day_week = (TextView) findViewById(R.id.four_day_week);
        fifth_day_week = (TextView) findViewById(R.id.fifth_day_week);
        sixth_day_week = (TextView) findViewById(R.id.sixth_day_week);
        seventh_day_week = (TextView) findViewById(R.id.seventh_day_week);

        riqi_textViews[0] = first_day; riqi_textViews[1] = second_day;
        riqi_textViews[2] = third_day; riqi_textViews[3] = four_day;
        riqi_textViews[4] = fifth_day; riqi_textViews[5] = sixth_day;
        riqi_textViews[6] = seventh_day;
        first_night_weather_txt = (TextView) findViewById(R.id.first_night_weather_txt);
        second_night_weather_txt = (TextView) findViewById(R.id.second_night_weather_txt);
        third_night_weather_txt = (TextView) findViewById(R.id.third_night_weather_txt);
        four_night_weather_txt = (TextView) findViewById(R.id.four_night_weather_txt);
        fifth_night_weather_txt = (TextView) findViewById(R.id.fifth_night_weather_txt);
        sixth_night_weather_txt = (TextView) findViewById(R.id.sixth_night_weather_txt);
        seventh_night_weather_txt = (TextView) findViewById(R.id.seventh_night_weather_txt);

        first_day_weather_txt = (TextView) findViewById(R.id.first_day_weather_txt);
        second_day_weather_txt = (TextView) findViewById(R.id.second_day_weather_txt);
        third_day_weather_txt = (TextView) findViewById(R.id.third_day_weather_txt);
        four_day_weather_txt = (TextView) findViewById(R.id.four_day_weather_txt);
        fifth_day_weather_txt = (TextView) findViewById(R.id.fifth_day_weather_txt);
        sixth_day_weather_txt = (TextView) findViewById(R.id.sixth_day_weather_txt);
        seventh_day_weather_txt = (TextView) findViewById(R.id.seventh_day_weather_txt);

        first_day_image = (NetworkImageView) findViewById(R.id.first_day_image);
        second_day_image = (NetworkImageView) findViewById(R.id.second_day_image);
        third_day_image = (NetworkImageView) findViewById(R.id.third_day_image);
        four_day_image = (NetworkImageView) findViewById(R.id.four_day_image);
        fifth_day_image = (NetworkImageView) findViewById(R.id.fifth_day_image);
        sixth_day_image = (NetworkImageView) findViewById(R.id.sixth_day_image);
        seventh_day_image = (NetworkImageView) findViewById(R.id.seventh_day_image);

        dayNetworkImageViews[0] = first_day_image; dayNetworkImageViews[1] = second_day_image;
        dayNetworkImageViews[2] = third_day_image; dayNetworkImageViews[3] = four_day_image;
        dayNetworkImageViews[4] = fifth_day_image; dayNetworkImageViews[5] = sixth_day_image;
        dayNetworkImageViews[6] = seventh_day_image;


        first_night_image = (NetworkImageView) findViewById(R.id.first_night_image);
        second_night_image = (NetworkImageView) findViewById(R.id.second_night_image);
        third_night_image = (NetworkImageView) findViewById(R.id.third_night_image);
        four_night_image = (NetworkImageView) findViewById(R.id.four_night_image);
        fifth_night_image = (NetworkImageView) findViewById(R.id.fifth_night_image);
        sixth_night_image = (NetworkImageView) findViewById(R.id.sixth_night_image);
        seventh_night_image = (NetworkImageView) findViewById(R.id.seventh_night_image);

        nightNetworkImageViews[0] = first_night_image; nightNetworkImageViews[1] = second_night_image;
        nightNetworkImageViews[2] = third_night_image; nightNetworkImageViews[3] = four_night_image;
        nightNetworkImageViews[4] = fifth_night_image; nightNetworkImageViews[5] = sixth_night_image;
        nightNetworkImageViews[6] = seventh_night_image;
    }
    public void setWeatherData(Intent intent){
        nightWeather = intent.getStringArrayExtra("nightWeather");
        dayWeather = intent.getStringArrayExtra("dayWeather");
        dayWeatherPic = intent.getStringArrayExtra("dayWeatherPic");
        nightWeatherPic = intent.getStringArrayExtra("nightWeatherPic");
        String title = intent.getStringExtra("city") + "  " + "七天预报";
        title_view.setText(title);

        getImage();
        first_night_weather_txt.setText(nightWeather[0]);
        second_night_weather_txt.setText(nightWeather[1]);
        third_night_weather_txt.setText(nightWeather[2]);
        four_night_weather_txt.setText(nightWeather[3]);
        fifth_night_weather_txt.setText(nightWeather[4]);
        sixth_night_weather_txt.setText(nightWeather[5]);
        seventh_night_weather_txt.setText(nightWeather[6]);

        first_day_weather_txt.setText(dayWeather[0]);
        second_day_weather_txt.setText(dayWeather[1]);
        third_day_weather_txt.setText(dayWeather[2]);
        four_day_weather_txt.setText(dayWeather[3]);
        fifth_day_weather_txt.setText(dayWeather[4]);
        sixth_day_weather_txt.setText(dayWeather[5]);
        seventh_day_weather_txt.setText(dayWeather[6]);

    }

    public void getImage(){
        for (int i = 0; i < dayWeather.length; i++){
            if (dayWeatherPic[i] != null){
                dayNetworkImageViews[i].setImageUrl(dayWeatherPic[i],imageLoader);
            }
            if (nightWeatherPic[i] != null){
                nightNetworkImageViews[i].setImageUrl(nightWeatherPic[i],imageLoader);
            }
        }
    }
    public void initDate(){
        Calendar calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < riqi_textViews.length; i++ ){
            riqi_textViews[i].setText((calendar.get(Calendar.MONTH) + 1) + "." + day);
            calendar.set(Calendar.DAY_OF_MONTH, day + 1);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }

        Calendar calendar1 = Calendar.getInstance();
        int week = calendar1.get(Calendar.DAY_OF_WEEK) - 1;
        switch (week){
            case 1:
                third_day_week.setText("周三");
                four_day_week.setText("周四");
                fifth_day_week.setText("周五");
                sixth_day_week.setText("周六");
                seventh_day_week.setText("周日");
                break;
            case 2:
                third_day_week.setText("周四");
                four_day_week.setText("周五");
                fifth_day_week.setText("周六");
                sixth_day_week.setText("周日");
                seventh_day_week.setText("周一");
                break;
            case 3:
                third_day_week.setText("周五");
                four_day_week.setText("周六");
                fifth_day_week.setText("周日");
                sixth_day_week.setText("周一");
                seventh_day_week.setText("周二");
                break;
            case 4:
                third_day_week.setText("周六");
                four_day_week.setText("周日");
                fifth_day_week.setText("周一");
                sixth_day_week.setText("周二");
                seventh_day_week.setText("周三");
                break;
            case 5:
                third_day_week.setText("周日");
                four_day_week.setText("周一");
                fifth_day_week.setText("周二");
                sixth_day_week.setText("周三");
                seventh_day_week.setText("周四");
                break;
            case 6:
                third_day_week.setText("周一");
                four_day_week.setText("周二");
                fifth_day_week.setText("周三");
                sixth_day_week.setText("周四");
                seventh_day_week.setText("周五");
                break;
            case 0:
                third_day_week.setText("周二");
                four_day_week.setText("周三");
                fifth_day_week.setText("周四");
                sixth_day_week.setText("周五");
                seventh_day_week.setText("周六");
                break;
        }
    }

    public void initTableView(){
        int max = -100;
        int min = 100;
        for (int i : maxTemp){
            if (max < i){
                max = i;
            }
        }
        for (int i : minTemp){
            if (min > i){
                min = i;
            }
        }
        int c = max - min;
        if (c != 0) {
            table.setLineCount(2);
            table.setPointCount(7);
            table.setJiange(60);

            float x = 60;
            float y = 100 / c;

            for (int i = 0; i < 7; i++){
                table.addMaxPoint(i,i * x + 30,(max - maxTemp[i]) * y + 50);
                table.addMinPoint(i,i * x + 30,(max - minTemp[i]) * y + 50);
                table.setMaxTemp(i,maxTemp[i]);
                table.setMinTemp(i,minTemp[i]);
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        String color = sharedPreferences.getString("style_color", "");
        switch (color){
            case "蓝色":
                relativeLayout.setBackgroundColor(Color.parseColor("#104d8e"));
                break;
            case "灰色":
                relativeLayout.setBackgroundColor(Color.GRAY);
                break;
            case "青色":
                relativeLayout.setBackgroundColor(Color.parseColor("#FF00786F"));
                break;
            case "绿色":
                relativeLayout.setBackgroundColor(Color.parseColor("#2e8b57"));
                break;
            case "黑色":
                relativeLayout.setBackgroundColor(Color.BLACK);
                break;
            case "咖啡色":
                relativeLayout.setBackgroundColor(Color.parseColor("#5f4421"));
                break;
        }
    }

}
