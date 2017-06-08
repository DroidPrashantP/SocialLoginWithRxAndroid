package com.paddy.edcastdemo.app.ui;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by prashant on 6/7/17.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = ViewPagerAdapter.class.getSimpleName();

    private List<Fragment> mFragments;
    private List<String> mTabTitles;

    public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
        super(fm);
        mFragments = fragments;
        mTabTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles.get(position);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
