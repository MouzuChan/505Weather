package com.example.l.myweather.activities;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.l.myweather.base.BaseActivity;
import com.example.l.myweather.base.MyApplication;
import com.example.l.myweather.R;
import com.example.l.myweather.callback.CheckUpdateCallBack;
import com.example.l.myweather.util.CheckUpdate;

import java.io.File;

public class AboutActivity extends BaseActivity{


    private CoordinatorLayout container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        compatNavigationBarColor();
        initView();
        getFragmentManager().beginTransaction().add(R.id.content, new AboutPreference()).commit();

    }




    public static class AboutPreference extends PreferenceFragment implements Preference.OnPreferenceClickListener{


        private Preference version,checkUpdate,appGhAddress,rate,mail,myGhAddress;
        private String versionName;
        private int versionCode;
        AboutActivity aboutActivity;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.about_preference);

            initView();
            initVersionInfo();
            version.setSummary(versionName + "  (" + versionCode + ")");


        }

        public void initView(){

            aboutActivity = (AboutActivity)getActivity();
            version = findPreference("version");
            checkUpdate = findPreference("check_update");
            appGhAddress = findPreference("app_gh_address");
            rate = findPreference("rate");
            mail = findPreference("mail");
            myGhAddress = findPreference("my_gh_address");

            version.setOnPreferenceClickListener(this);
            checkUpdate.setOnPreferenceClickListener(this);
            appGhAddress.setOnPreferenceClickListener(this);
            rate.setOnPreferenceClickListener(this);
            mail.setOnPreferenceClickListener(this);
            myGhAddress.setOnPreferenceClickListener(this);
        }

        public void initVersionInfo(){
            try{
                PackageManager packageManager = getActivity().getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(getActivity().getPackageName(),0);
                versionName = packageInfo.versionName;
                versionCode = packageInfo.versionCode;
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            switch (preference.getKey()){
                case "check_update":
                    aboutActivity.checkUpdate();
                    break;
                case "app_gh_address":
                    aboutActivity.copySomething("https://github.com/Liuhongan/505Weather");
                    break;
                case "rate":
                    Intent intent = new Intent("android.intent.action.VIEW",Uri.parse("market://details?id=com.lha.weather"));
                    startActivity(intent);
                    break;
                case "mail":
                    aboutActivity.copySomething("liuhongan6@gmail.com");
                    break;
                case "my_gh_address":
                    aboutActivity.copySomething("https://github.com/Liuhongan");
                    break;
            }
            return true;
        }
    }




    public void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        container = (CoordinatorLayout)findViewById(R.id.container);
    }





    public void copySomething(String content){

        ClipboardManager clipboardManager1 = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData.Item item = new ClipData.Item(content);
        ClipData clipData = new ClipData("",new String[]{"text"},item);
        clipboardManager1.setPrimaryClip(clipData);
        showSnackbar(container,"已复制...");
    }


    public void checkUpdate(){
        final ProgressDialog dialog = new ProgressDialog(AboutActivity.this);
        dialog.setMessage("正在检查更新...");
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(800);
                    CheckUpdate cu = new CheckUpdate();
                    cu.sendHttpRequest(new CheckUpdateCallBack() {
                        @Override
                        public void hasUpdate(String newVersionName, String changelog, final String url) {
                            dialog.dismiss();
                            android.support.v7.app.AlertDialog.Builder builder =
                                    new android.support.v7.app.AlertDialog.Builder(AboutActivity.this);
                            builder.setTitle("发现新版本:" + newVersionName)
                                    .setMessage(changelog)
                                    .setPositiveButton("下载", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DownloadManager dm = (DownloadManager) AboutActivity.this.getSystemService(DOWNLOAD_SERVICE);
                                    Uri uri = Uri.parse(url);
                                    File file = new File(MyApplication.getContext().getExternalFilesDir(null) + "/apk/505Weather.apk");
                                    if (file.exists()){
                                        file.delete();
                                    }
                                    DownloadManager.Request request = new DownloadManager.Request(uri);
                                    request.setDestinationInExternalFilesDir(MyApplication.getContext(),"apk","505Weather.apk");
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    dm.enqueue(request);
                                    Toast.makeText(MyApplication.getContext(),"正在下载新版本..",Toast.LENGTH_SHORT).show();
                                }
                            });
                            builder.setNegativeButton("取消", null);
                            builder.show();
                        }

                        @Override
                        public void noUpdate() {
                            dialog.dismiss();
                            Toast.makeText(AboutActivity.this,"已经是最新版本了",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(int errorCode) {
                            dialog.dismiss();
                            if (errorCode == 1){
                                Toast.makeText(AboutActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                            } else {

                                Toast.makeText(AboutActivity.this,"未知错误",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });





                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).start();

    }


}
