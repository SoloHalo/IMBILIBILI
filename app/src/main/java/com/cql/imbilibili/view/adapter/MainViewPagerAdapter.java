package com.cql.imbilibili.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cql.imbilibili.view.BaseFragment;

import java.util.List;

/**
 * Created by CQL on 2016/7/6.
 */
public class MainViewPagerAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> fragments;

    public MainViewPagerAdapter(FragmentManager fm, List<BaseFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getTitle();
    }
}
