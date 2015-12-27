package com.example.l.myweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView listView;
    private EditText editText;
    private List<String> list;
    private int errNum;
    private List<String> id_list;
    private ArrayAdapter adapter;
    private Button location;
    private RelativeLayout relativeLayout;
    private List<String> city_list;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_search);
        listView = (ListView) findViewById(R.id.list_view);
        editText = (EditText) findViewById(R.id.edit_text);
        location = (Button)findViewById(R.id.location);
        relativeLayout = (RelativeLayout)findViewById(R.id.relative_layout);

        location.setOnClickListener(this);

        city_list = new ArrayList<String>();

        list = new ArrayList<String>();
        id_list = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                list.clear();
                String cityName = "";
                try {
                    String edit_name = editText.getText().toString();
                    if (edit_name.contains("市") || edit_name.contains("县") || edit_name.contains("区") || edit_name.contains("省")){
                        if (edit_name.length() > 2){
                            edit_name = edit_name.substring(0,edit_name.length() - 1);
                        }
                    }
                    cityName = URLEncoder.encode(edit_name, "UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!cityName.equals("")){
                    if (isConnected()){
                        getCityList(cityName);
                    } else {
                        list.add("请检查网络连接");
                    }
                }
                adapter.notifyDataSetChanged();


            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (errNum == 0){
                    String return_id = id_list.get(position);
                    Intent intent = new Intent();
                    intent.putExtra("return_id",return_id);
                    intent.putExtra("district",city_list.get(position));
                    setResult(1, intent);
                    finish();
                }

            }
        });
    }

    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.location:
                final MyLocation myLocation = new MyLocation();
                myLocation.getUserLocation();

                final ProgressDialog progressDialog = new ProgressDialog(SearchActivity.this);
                progressDialog.setMessage("定位中...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {

                        String city = myLocation.getCity();
                        String district = myLocation.getDistrict();
                        Log.d("FF", city + district);

                        if (city == null ){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SearchActivity.this, "定位失败", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }else {
                            //getLocationCity(city, district);
                            LocationCityId locationCityId = new LocationCityId();
                            locationCityId.getLocationCityId(city, district, new LocationCallBack() {
                                @Override
                                public void onFinish(String return_id,String city_name) {

                                    if (return_id != null){
                                        Intent intent = new Intent();
                                        intent.putExtra("return_id",return_id);
                                        intent.putExtra("district",city_name);
                                        setResult(2, intent);
                                        Log.d("ID",return_id);
                                        finish();
                                    } else {
                                        Log.d("DD","NULL");
                                    }
                                }
                            });


                        }
                        progressDialog.dismiss();



                    }
                };
                Timer timer = new Timer();
                timer.schedule(timerTask,2000);


        }
    }

   /* public void getLocationCity(final String city,final String district){
        String url = "http://apis.baidu.com/apistore/weatherservice/citylist?cityname=" + city;
        HttpUtil.makeHttpRequest(url, new CallBackListener() {
            @Override
            public void onFinish(JSONObject jsonObject) {
                try {
                    String errMsg = jsonObject.getString("errMsg");
                    if (errMsg.equals("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("retData");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jo = jsonArray.getJSONObject(i);
                            String name_cn = jo.getString("name_cn");
                            if (name_cn.equals(city)) {
                                String return_id = jo.getString("area_id");
                                Intent intent = new Intent();
                                intent.putExtra("district", city);
                                intent.putExtra("return_id", return_id);
                                setResult(2, intent);
                                finish();
                                break;
                            } else if (name_cn.equals(district)) {
                                String return_id = jo.getString("area_id");
                                Intent intent = new Intent();
                                intent.putExtra("district", district);
                                intent.putExtra("return_id", return_id);
                                setResult(2, intent);
                                finish();
                                break;
                            } else if (i == jsonArray.length() - 1) {
                                Toast.makeText(SearchActivity.this, "定位失败", Toast.LENGTH_SHORT).show();
                            }

                        }
                    } else {
                        Toast.makeText(SearchActivity.this, "定位失败", Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Exception e) {

            }
        });*/
    //}


    public void getCityList(String cityName){
        id_list.clear();
        city_list.clear();
        if (!cityName.equals("") && cityName.length() >= 18) {
            String url = "http://apis.baidu.com/apistore/weatherservice/citylist?cityname=" + cityName;
            HttpUtil.makeBaiduHttpRequest(url, new CallBackListener() {
                @Override
                public void onFinish(final JSONObject jsonObject) {

                    Log.d("FF", jsonObject.toString());
                    try {
                        String errMsg = jsonObject.getString("errMsg");
                        if (errMsg.equals("success")) {
                            errNum = 0;
                            JSONArray jsonArray = jsonObject.getJSONArray("retData");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject oj = jsonArray.getJSONObject(i);
                                String city1 = oj.getString("name_cn");
                                String city = oj.getString("province_cn") + "-" + oj.getString("district_cn") + "-" + oj.getString("name_cn");
                                String id = oj.getString("area_id");
                                city_list.add(city1);
                                id_list.add(id);
                                list.add(city);
                            }
                        } else {
                            errNum = 1;
                            list.add(errMsg);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onError(String e) {
                    Toast.makeText(SearchActivity.this,"更新失败,网络超时",Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        String color = sharedPreferences.getString("style_color","青色");
        switch (color){
            case "蓝色":
                relativeLayout.setBackgroundColor(Color.parseColor("#104d8e"));
                setTheme(R.style.lanseTheme);
                break;
            case "灰色":
                setTheme(R.style.huiseTheme);
                relativeLayout.setBackgroundColor(Color.GRAY);
                break;
            case "青色":
                setTheme(R.style.qingseTheme);
                relativeLayout.setBackgroundColor(Color.parseColor("#FF00786F"));
                break;
            case "绿色":
                setTheme(R.style.lvseTheme);
                relativeLayout.setBackgroundColor(Color.parseColor("#2e8b57"));
                break;
            case "黑色":
                setTheme(R.style.heiseTheme);
                relativeLayout.setBackgroundColor(Color.BLACK);
                break;
            case "咖啡色":
                setTheme(R.style.kafeiseTheme);
                relativeLayout.setBackgroundColor(Color.parseColor("#5f4421"));
                break;
        }
    }
}
