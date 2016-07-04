package com.example.l.myweather.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.l.myweather.R;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.mail).setOnClickListener(this);
        findViewById(R.id.we_chat).setOnClickListener(this);
        TextView textView = (TextView) findViewById(R.id.version);
        try{
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(),0);
            String version = packageInfo.versionName;
            int versionCode = packageInfo.versionCode;
            textView.setText("Version " + version + "   ( " + versionCode + " ) ");
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mail:
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData.Item item = new ClipData.Item("liuhongan6@gmail.com");
                ClipData clipData = new ClipData("",new String[]{"text"},item);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(AboutActivity.this,"邮箱复制成功",Toast.LENGTH_SHORT).show();
                break;
            case R.id.we_chat:
                ClipboardManager clipboardManager1 = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData.Item item1 = new ClipData.Item("liuhongan__");
                ClipData clipData1 = new ClipData("",new String[]{"text"},item1);
                clipboardManager1.setPrimaryClip(clipData1);
                Toast.makeText(AboutActivity.this,"微信复制成功",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_about,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_rate:
                Intent intent = new Intent("android.intent.action.VIEW",Uri.parse("market://details?id=com.lha.weather"));
                startActivity(intent);
                break;
        }


        return super.onOptionsItemSelected(item);
    }
}
