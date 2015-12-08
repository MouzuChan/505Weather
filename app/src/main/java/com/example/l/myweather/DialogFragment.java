package com.example.l.myweather;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DialogFragment extends android.support.v4.app.DialogFragment{

    private String title;
    private String content;
    private ImageView life_image;
    private TextView content_view,title_view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_layout,null);

        content_view = (TextView) view.findViewById(R.id.life_content);
        title_view = (TextView) view.findViewById(R.id.life_title);
        life_image = (ImageView) view.findViewById(R.id.life_image);
        title_view.setText(title);
        content_view.setText(content);
        switch (title){
            case "舒适度" :
                life_image.setImageResource(R.drawable.co1);
                break;
            case "感冒":
                life_image.setImageResource(R.drawable.gm1);
                break;
            case "出行":
                life_image.setImageResource(R.drawable.jt1);
                break;
            case "洗车":
                life_image.setImageResource(R.drawable.xc1);
                break;
            case "运动":
                life_image.setImageResource(R.drawable.yd1);
                break;
            case "穿衣":
                life_image.setImageResource(R.drawable.ct1);
                break;

        }
        builder.setView(view);

        return builder.create();
    }

    public void setTitle(String title){
        this.title = title;
    }
    public void setContent(String content){
        this.content = content;
    }
}
