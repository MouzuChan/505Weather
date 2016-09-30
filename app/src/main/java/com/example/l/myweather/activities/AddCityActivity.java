package com.example.l.myweather.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.*;

import com.example.l.myweather.*;
import com.example.l.myweather.base.BaseActivity;
import com.example.l.myweather.base.MyApplication;
import com.example.l.myweather.callback.CallBackListener;
import com.example.l.myweather.callback.LocationCallBack;
import com.example.l.myweather.util.HttpUtil;
import com.example.l.myweather.util.LocationCityId;
import com.example.l.myweather.util.MyLocation;
import com.example.l.myweather.view.adapter.ListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AddCityActivity extends BaseActivity implements View.OnClickListener{

    private EditText editText;
    private Button locationButton;
    private ListView cityListView;
    private com.example.l.myweather.view.adapter.ListAdapter adapter;
    private List<String> id_list;
    private List<String> city_list;
    private List<String> list;
    private Context context = MyApplication.getContext();
    private int errNum = 1;
    private Toolbar toolbar;
    private String location_city;
    private String location_city_id;
    private TextView location_city_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);
        compatNavigationBarColor();
        initView();
        initLocation();

    }

    public void initView(){
        editText = (EditText) findViewById(R.id.edit_text);
        locationButton = (Button)findViewById(R.id.location);
        cityListView = (ListView)findViewById(R.id.city_list);
        locationButton.setOnClickListener(this);
        city_list = new ArrayList<>();
        list = new ArrayList<>();
        id_list = new ArrayList<>();
        adapter = new ListAdapter(list);
        cityListView.setAdapter(adapter);
        toolbar = (Toolbar)findViewById(R.id.add_toolbar);
        setSupportActionBar(toolbar);
        location_city_view = (TextView) findViewById(R.id.location_city_view);
        location_city_view.setOnClickListener(this);
        locationButton.setVisibility(View.INVISIBLE);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                list.clear();
                String cityName = "";
                try {
                    String edit_name = editText.getText().toString();
                    if (edit_name.contains("市") || edit_name.contains("县") || edit_name.contains("区") || edit_name.contains("省")) {
                        if (edit_name.length() > 2) {
                            edit_name = edit_name.substring(0, edit_name.length() - 1);
                        }
                    }
                    cityName = URLEncoder.encode(edit_name, "UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!cityName.equals("")) {
                    if (MyApplication.isConnected()) {
                        getCityList(cityName);
                    } else {
                        list.add("请检查网络连接");
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (errNum == 0) {
                    Intent intent = new Intent("com.lha.weather.ADD_CITY");
                    intent.putExtra("city_name", city_list.get(position));
                    intent.putExtra("city_id", id_list.get(position));
                    sendBroadcast(intent);
                    Intent intent1 = new Intent();
                    setResult(1,intent1);
                    finish();
                }

            }
        });


    }


    public void getCityList(String cityName){
        id_list.clear();
        city_list.clear();
        if (!cityName.equals("") && cityName.length() >= 18) {
            String url = "http://apis.baidu.com/apistore/weatherservice/citylist?cityname=" + cityName;
            HttpUtil.makeBaiduHttpRequest(url, new CallBackListener() {
                @Override
                public void onFinish(final JSONObject jsonObject) {

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
                    Toast.makeText(context, "更新失败,网络超时", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.location:
                Toast.makeText(context,"定位中..",Toast.LENGTH_SHORT).show();
                initLocation();
                /*final MyLocation myLocation = new MyLocation();
                myLocation.getUserLocation();
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("定位中...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {

                        String city = myLocation.getCity();
                        final String district = myLocation.getDistrict();
                        if (city == null ){
                            Toast.makeText(context, "定位失败", Toast.LENGTH_SHORT).show();
                        }else {

                            //getLocationCity(city, district);
                            LocationCityId locationCityId = new LocationCityId();
                            locationCityId.getLocationCityId(city, district, new LocationCallBack() {
                                @Override
                                public void onFinish(String return_id,String city_name) {

                                    if (return_id != null){
                                        Toast.makeText(context, "定位成功:" + city_name,Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent("com.lha.weather.ADD_CITY");
                                        intent.putExtra("city_name",city_name);
                                        intent.putExtra("city_id",return_id);
                                        sendBroadcast(intent);
                                        finish();

                                    }
                                }
                            });


                        }
                        progressDialog.dismiss();



                    }
                };
                Timer timer = new Timer();
                timer.schedule(timerTask, 3000);*/
                break;
            case R.id.location_city_view :
                if (location_city_id != null && location_city != null){
                    Intent intent = new Intent("com.lha.weather.ADD_CITY");
                    intent.putExtra("city_name",location_city);
                    intent.putExtra("city_id",location_city_id);
                    sendBroadcast(intent);
                    Intent intent1 = new Intent();
                    setResult(1, intent1);
                    finish();
                } else {
                    initLocation();
                }
                break;
        }

    }



    public void initLocation(){

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},0);
        } else{
            location();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 0:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    location();
                } else {
                    location_city_view.setText("没有定位权限，点击授权");
                }
                break;
        }
    }

    public void location(){
        final MyLocation myLocation = new MyLocation();
        myLocation.setLocationListener(new MyLocation.LocationListener() {
            @Override
            public void onLocated(final String return_id, final String city_name) {
                if (city_name == null || return_id == null) {
                    return;
                }
                setLocationView(city_name, return_id);
            }

            @Override
            public void onError() {

            }
        });
        myLocation.getUserLocation();
    }

    private void setLocationView(String city_name, String return_id) {
        if (return_id != null){
            location_city = city_name;
            location_city_id = return_id;
            location_city_view.setText("当前位置：" + city_name);
            locationButton.setVisibility(View.VISIBLE);

        }
    }
}
