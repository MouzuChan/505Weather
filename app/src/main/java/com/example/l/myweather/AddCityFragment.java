package com.example.l.myweather;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.l.myweather.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddCityFragment extends android.support.v4.app.Fragment implements View.OnClickListener{

    private View view;
    private EditText editText;
    private Button locationButton;
    private ListView cityListView;
    private ListAdapter adapter;
    private List<String> id_list;
    private List<String> city_list;
    private List<String> list;
    private Context context = MyApplication.getContext();
    
    private int errNum = 1;
    private MainActivity mainActivity;
    public AddCityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_city,container,false);
        initView();
        return view;
    }
    public void initView(){
        editText = (EditText)view.findViewById(R.id.edit_text);
        editText.setText("");
        locationButton = (Button)view.findViewById(R.id.location);
        cityListView = (ListView)view.findViewById(R.id.city_list);
        mainActivity = (MainActivity)getActivity();
        locationButton.setOnClickListener(this);
        city_list = new ArrayList<String>();
        list = new ArrayList<String>();
        id_list = new ArrayList<String>();
        adapter = new ListAdapter(list);
        cityListView.setAdapter(adapter);
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
                    if (isConnected()) {
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

        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (errNum == 0) {
                    String return_id = id_list.get(position);
                    //Intent intent = new Intent();
                    //intent.putExtra("return_id", return_id);
                    //intent.putExtra("district", city_list.get(position));

                    String city = city_list.get(position);
                    mainActivity.addCity(city,return_id);
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.location){
            final MyLocation myLocation = new MyLocation();
            myLocation.getUserLocation();
            final ProgressDialog progressDialog = new ProgressDialog(mainActivity);
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
                                    mainActivity.addCity(city_name, return_id);

                                }
                            }
                        });


                    }
                    progressDialog.dismiss();



                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask,3000);

        }
    }

    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();

    }

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
                    Toast.makeText(context, "更新失败,网络超时", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            adapter.notifyDataSetChanged();
        }
    }

    public static AddCityFragment newInstance() {

        Bundle args = new Bundle();
        AddCityFragment fragment = new AddCityFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager inputMethodManager=(InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(),0);
    }

    @Override
    public void onResume() {
        super.onResume();
        InputMethodManager inputMethodManager=(InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

    }
}
