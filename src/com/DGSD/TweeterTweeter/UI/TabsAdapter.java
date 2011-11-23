package com.DGSD.TweeterTweeter.UI;

import android.content.Context;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.util.Log;

import java.util.ArrayList;

/**
 * Author: Daniel Grech
 * Date: 15/11/11 11:22 AM
 * Description :
 */
public class TabsAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener, ActionBar.TabListener  {
    private static final String TAG = TabsAdapter.class.getSimpleName();
    private final Context mContext;
    private final ActionBar mActionBar;
    private final ViewPager mViewPager;
    private ArrayList<Fragment> mFragments;
    private ViewPager.OnPageChangeListener onChangeListener;

    public TabsAdapter(Object context, ActionBar actionBar, ViewPager pager) {
        super(((SupportActivity) context).getSupportFragmentManager());
        mContext = (Context) context;
        mActionBar = actionBar;
        mFragments = new ArrayList<Fragment>();
        mViewPager = pager;
        if(pager != null) {
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }
    }

    public void addTab(ActionBar.Tab tab, Fragment f) {
        mFragments.add(f);
        mActionBar.addTab(tab.setTabListener(this));
        notifyDataSetChanged();
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        onChangeListener = listener;
    }

    @Override
    public int getCount() {

        return mFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mActionBar.setSelectedNavigationItem(position);

        //Hack to force ICS devices to resume fragments when scrolling
     /*   Fragment f = mFragments.get(position);
        if(!f.isResumed()) {
            f.onResume();
        } */

        if(onChangeListener != null) {
            onChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        if(mViewPager != null) {
            mViewPager.setCurrentItem(tab.getPosition());
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    public Fragment getCurrentItem() {
        return mFragments.get( mViewPager.getCurrentItem() );
    }
}
