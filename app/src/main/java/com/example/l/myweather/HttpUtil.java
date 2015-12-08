package com.example.l.myweather;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by L on 2015/10/2.
 */
public class HttpUtil {



   private static RequestQueue mQueue = Volley.newRequestQueue(MyApplication.getContext());


    public static void makeBaiduHttpRequest(String url,final CallBackListener callBackListener){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject != null){
                    if (callBackListener != null){
                        callBackListener.onFinish(jsonObject);;
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("TAG",volleyError.getMessage(),volleyError);
                callBackListener.onError("error");
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<String,String>();
                headers.put("apikey","760191bf6221fde4d14e5019d3704e15");
                return headers;
            }

        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(3 * 1000,0,1.0f));
        mQueue.add(jsonObjectRequest);
    }

   /* public static void makeImageRequest(final String url,final ImageCallBack imageCallBack){

        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                if (imageCallBack != null){
                    imageCallBack.onFinish(bitmap);
                } else {
                    imageCallBack.onError();
                }
            }
        }, 0, 0, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //Toast.makeText(MyApplication.getContext(),volleyError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(imageRequest);
    }*/

}
