package com.example.l.myweather;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Created by L on 2015/10/6.
 */
public class MyLocation {

    private LocationClient locationClient;
    private String city;
    private String district;
    private int locType;


    public void getUserLocation(){

        BDLocationListener listener = new MyLocationListener();
        locationClient = new LocationClient(MyApplication.getContext());
        initLocation();
        locationClient.registerLocationListener(listener);
        locationClient.start();

    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setIsNeedLocationDescribe(true);
        locationClient.setLocOption(option);

    }


    public class MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            locType = bdLocation.getLocType();
            //Log.d("City",bdLocation.getCity());
            if (locType == BDLocation.TypeGpsLocation || locType == BDLocation.TypeNetWorkLocation){
                city = bdLocation.getCity();
                district = bdLocation.getDistrict();

                if (city.contains("自治区") || city.contains("自治县")){
                    city = city.replace("自治区","");
                    city = city.replace("自治县","");
                } else{
                    city = city.substring(0,city.length() - 1);
                }


                if (district.contains("自治区") || district.contains("自治县")){
                    district = district.substring(0,district.length() - 3);
                }
                else {
                    district = district.substring(0,district.length() - 1);
                }
                Log.d("City",city);Log.d("District",district);


            }
            else {
                Toast.makeText(MyApplication.getContext(),"定位失败",Toast.LENGTH_SHORT).show();
            }


        }

    }

    public String getCity(){
        return city;
    }
    public String getDistrict(){
        return district;
    }

}

/*StringBuffer sb = new StringBuffer(256);
        sb.append("time : ");
        sb.append(location.getTime());
        sb.append("\nerror code : ");
        sb.append(location.getLocType());
        sb.append("\nlatitude : ");
        sb.append(location.getLatitude());
        sb.append("\nlontitude : ");
        sb.append(location.getLongitude());
        sb.append("\nradius : ");
        sb.append(location.getRadius());
        if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
            sb.append("\nspeed : ");
            sb.append(location.getSpeed());// 单位：公里每小时
            sb.append("\nsatellite : ");
            sb.append(location.getSatelliteNumber());
            sb.append("\nheight : ");
            sb.append(location.getAltitude());// 单位：米
            sb.append("\ndirection : ");
            sb.append(location.getDirection());// 单位度
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            sb.append("\ndescribe : ");
            sb.append("gps定位成功");

        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            //运营商信息
            sb.append("\noperationers : ");
            sb.append(location.getOperators());
            sb.append("\ndescribe : ");
            sb.append("网络定位成功");
        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
            sb.append("\ndescribe : ");
            sb.append("离线定位成功，离线定位结果也是有效的");
        } else if (location.getLocType() == BDLocation.TypeServerError) {
            sb.append("\ndescribe : ");
            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
            sb.append("\ndescribe : ");
            sb.append("网络不同导致定位失败，请检查网络是否通畅");
        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
            sb.append("\ndescribe : ");
            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
        }
        sb.append("\nlocationdescribe : ");
        sb.append(location.getLocationDescribe());// 位置语义化信息
        List<Poi> list = location.getPoiList();// POI数据
        if (list != null) {
            sb.append("\npoilist size = : ");
            sb.append(list.size());
            for (Poi p : list) {
                sb.append("\npoi= : ");
                sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
            }
        }
        Log.d("BaiduLocationApiDem", sb.toString());*/