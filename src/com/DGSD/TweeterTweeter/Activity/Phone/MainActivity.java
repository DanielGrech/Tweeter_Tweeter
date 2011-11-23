package com.DGSD.TweeterTweeter.Activity.Phone;

import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.*;
import android.util.Log;
import android.widget.ProgressBar;
import com.DGSD.TweeterTweeter.Fragment.BaseFragment;
import com.DGSD.TweeterTweeter.Fragment.DashboardFragment;
import com.DGSD.TweeterTweeter.Fragment.HomeTimelineFragment;
import com.DGSD.TweeterTweeter.R;
import com.DGSD.TweeterTweeter.UI.TabsAdapter;

/**
 * Author: Daniel Grech
 * Date: 22/11/11 2:19 PM
 * Description :
 */
public class MainActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ViewPager mPager;

    private TabsAdapter mTabsAdapter;

    private FragmentManager mFragmentManager;

    private ActionBar mActionBar;

    private ProgressBar mProgressBar;

    private Fragment mTimelineFragment;

    private Fragment mMentionsFragment;

    private Fragment mDmFragment;

    private boolean mIsRefreshing = false;

    private static final String TIMELINE_FRAGMENT = "_timeline";

    private static final String MENTIONS_FRAGMENT = "_mentions";

    private static final String DM_FRAGMENT = "_dm";

    private static final String REFRESHING = "_refreshing";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mFragmentManager = getSupportFragmentManager();

        if(savedInstanceState != null) {
            mTimelineFragment = mFragmentManager.getFragment(savedInstanceState, TIMELINE_FRAGMENT);
            mMentionsFragment = mFragmentManager.getFragment(savedInstanceState, MENTIONS_FRAGMENT);
            mDmFragment = mFragmentManager.getFragment(savedInstanceState, DM_FRAGMENT);
        }

        if(mTimelineFragment == null) {
            Log.v(TAG, "Getting new timeline fragment");
            mTimelineFragment = HomeTimelineFragment.newInstance();
        }

        if(mMentionsFragment == null) {
            Log.v(TAG, "Getting new mentions fragment");
            mMentionsFragment = DashboardFragment.newInstance();
        }

        if(mDmFragment == null) {
            Log.v(TAG, "Getting new dm fragment");
            mDmFragment = DashboardFragment.newInstance();
        }

        mPager = (ViewPager) findViewById(R.id.pager);

        mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mTabsAdapter = new TabsAdapter(this, mActionBar, mPager);
        mTabsAdapter.setOnPageChangeListener(this);

        mTabsAdapter.addTab(mActionBar.newTab().setText("Timeline"), mTimelineFragment);
        mTabsAdapter.addTab(mActionBar.newTab().setText("Mentions"), mMentionsFragment);
        mTabsAdapter.addTab(mActionBar.newTab().setText("DM"), mDmFragment);

        //Progress spinner for our refresh menu item
        mProgressBar = new ProgressBar(this);
        mProgressBar.setIndeterminate(true);

        if(savedInstanceState != null) {
            mIsRefreshing = savedInstanceState.getBoolean(REFRESHING, false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);

        if(mTimelineFragment != null && mTimelineFragment.isAdded()) {
            mFragmentManager.putFragment(out, TIMELINE_FRAGMENT, mTimelineFragment);
        }

        if(mMentionsFragment != null && mMentionsFragment.isAdded()) {
            mFragmentManager.putFragment(out, MENTIONS_FRAGMENT, mMentionsFragment);
        }

        if(mDmFragment != null && mDmFragment.isAdded()) {
            mFragmentManager.putFragment(out, DM_FRAGMENT, mDmFragment);
        }

        out.putBoolean(REFRESHING, mIsRefreshing);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MenuRes.REFRESH, 0, "Refresh")
                .setIcon(R.drawable.ic_menu_refresh)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add(0, MenuRes.NEW_TWEET, 0, "New Tweet")
                .setIcon(R.drawable.ic_menu_compose)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add(0, MenuRes.SEARCH, 0, "Search")
                .setIcon(R.drawable.ic_menu_search)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(0, MenuRes.SETTINGS, 0, "Settings")
                .setIcon(R.drawable.ic_menu_settings);

        menu.add(0, MenuRes.ABOUT, 0, "About")
                .setIcon(R.drawable.ic_menu_help);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Update the refresh button.
        MenuItem item = menu.findItem(MenuRes.REFRESH);
        if(mIsRefreshing) {
            item.setActionView(mProgressBar);
        } else {
            item.setActionView(null);
        }

        return super.onPrepareOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case MenuRes.REFRESH:
                BaseFragment f = (BaseFragment) mTabsAdapter.getCurrentItem();
                f.setOnRefreshListener(new BaseFragment.OnRefreshListener(){
                    @Override
                    public void onRefreshData() {
                        mIsRefreshing = false;
                        invalidateOptionsMenu();
                    }

                    @Override
                    public void onRefreshNoData() {
                        mIsRefreshing = false;
                        invalidateOptionsMenu();
                    }

                    @Override
                    public void onRefreshError() {
                        mIsRefreshing = false;
                        invalidateOptionsMenu();
                    }
                });

                f.startRefresh();
                mIsRefreshing = true;
                invalidateOptionsMenu();
                return true;

            case MenuRes.SEARCH:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private static class MenuRes {
        public static final int NEW_TWEET = 0;

        public static final int SEARCH = 1;

        public static final int SETTINGS = 2;

        public static final int ABOUT = 3;

        public static final int REFRESH = 4;
    }
}

