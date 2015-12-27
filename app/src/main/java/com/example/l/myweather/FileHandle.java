package com.example.l.myweather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by L on 2015/10/20.
 */
public class FileHandle {

    private static Context context = MyApplication.getContext();



    static JSONObject getJSONObject(final String city_id){
        JSONObject jsonObject = null;
        BufferedReader reader = null;
        try {
            StringBuilder data = new StringBuilder();
            FileInputStream in = context.openFileInput(city_id);
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null){
                data.append(line);
            }
            jsonObject = new JSONObject(data.toString());
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                if (reader != null){
                    reader.close();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    static void saveJSONObject(JSONObject jsonObject,String city_id){
        String data = jsonObject.toString();
        BufferedWriter writer = null;
        try {
            FileOutputStream out = context.openFileOutput(city_id,Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(data);

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                if (writer != null){
                    writer.close();
                }
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    /*static Bitmap getImage(String fileName){
        File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.lha.weather/files", fileName + ".png");
        if (file.exists()){
            Bitmap bm = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/Android/data/com.lha.weather/files/" + fileName + ".png");
            if(bm != null){
                return bm;
            }
        }
        return null;
    }*/

    /*static void saveImage(Bitmap bitmap,String fileName){
        File fordl = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.lha.weather/files");
        File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.lha.weather/files", fileName + ".png");
        BufferedOutputStream outputStream = null;
        try {
            if (!fordl.exists()){
                fordl.mkdirs();
            }
            if (!file.exists()){
                file.createNewFile();
            }
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            outputStream.flush();

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null){
                    outputStream.close();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }*/

}
