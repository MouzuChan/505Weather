package com.example.l.myweather;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.example.l.myweather.ui.ContentFragment;

import java.util.ArrayList;

/**
 * Created by L on 2015/10/9.
 */
public class MyFragmentAdapter extends FragmentStatePagerAdapter {

    private ArrayList<ContentFragment> list;

    public MyFragmentAdapter(FragmentManager fm,ArrayList<ContentFragment> list){
        super(fm);
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
