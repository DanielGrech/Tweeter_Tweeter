package com.DGSD.TweeterTweeter.Activity.Phone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.ViewPager;
import com.DGSD.TweeterTweeter.Activity.DashboardChoiceActivity;
import com.DGSD.TweeterTweeter.Activity.MainChoiceActivity;
import com.DGSD.TweeterTweeter.Activity.PeopleChoiceActivity;
import com.DGSD.TweeterTweeter.Fragment.BaseFragment;
import com.DGSD.TweeterTweeter.Fragment.FollowersFragment;
import com.DGSD.TweeterTweeter.Fragment.FollowingFragment;
import com.DGSD.TweeterTweeter.R;
import com.DGSD.TweeterTweeter.UI.TabsAdapter;

/**
 * Author: Daniel Grech
 * Date: 22/11/11 2:19 PM
 * Description :
 */
public class PeopleActivity extends FragmentActivity implements  BaseFragment.OnRefreshListener{
    private static final String TAG = PeopleActivity.class.getSimpleName();

    private ActionBar mActionBar;

    private ViewPager mPager;

    private TabsAdapter mTabsAdapter;

    private FragmentManager mFragmentManager;

    private BaseFragment mFollowingFragment;

    private BaseFragment mFollowersFragment;

    private static final String FOLLOWING = "_following";

    private static final String FOLLOWERS = "_followers";

    private boolean mIsRefreshing = false;

    private static final String REFRESHING = "_refreshing";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        mFragmentManager = getSupportFragmentManager();

        if(savedInstanceState != null) {
            mFollowingFragment = (BaseFragment) mFragmentManager.getFragment(savedInstanceState, FOLLOWING);
            mFollowersFragment = (BaseFragment) mFragmentManager.getFragment(savedInstanceState, FOLLOWERS);
        }

        if(mFollowingFragment == null) {
            mFollowingFragment = FollowingFragment.newInstance();
        }

        if(mFollowersFragment == null) {
            mFollowersFragment = FollowersFragment.newInstance();
        }

        mFollowingFragment.setOnRefreshListener(this);
        mFollowersFragment.setOnRefreshListener(this);

        mPager = (ViewPager) findViewById(R.id.pager);

        mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        mTabsAdapter = new TabsAdapter(this, mActionBar, mPager);

        mTabsAdapter.addTab(mActionBar.newTab().setText("Following"), mFollowingFragment);
        mTabsAdapter.addTab(mActionBar.newTab().setText("Followers"), mFollowersFragment);

        if(savedInstanceState != null) {
            mIsRefreshing = savedInstanceState.getBoolean(REFRESHING, false);
        } else {
            //Check if we need to show a default tab
            switch(getIntent().getIntExtra(PeopleChoiceActivity.EXTRA.SHOW_SCREEN, -1)) {
                case PeopleChoiceActivity.EXTRA.FOLLOWING:
                    mPager.setCurrentItem(0, true); //Set to following tab
                    break;
                case PeopleChoiceActivity.EXTRA.FOLLOWERS:
                    mPager.setCurrentItem(1, true); //Set to followers tab
                    break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);

        if(mFollowingFragment != null && mFollowingFragment.isAdded()) {
            mFragmentManager.putFragment(out, FOLLOWING, mFollowingFragment);
        }

        if(mFollowersFragment != null && mFollowersFragment.isAdded()) {
            mFragmentManager.putFragment(out, FOLLOWERS, mFollowersFragment);
        }

        out.putBoolean(REFRESHING, mIsRefreshing);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MenuRes.REFRESH, 0, "Refresh")
                .setIcon(R.drawable.ic_menu_refresh)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(0, MenuRes.SEARCH, 0, "Search")
                .setIcon(R.drawable.ic_menu_search)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Update the refresh button.
        MenuItem item = menu.findItem(MenuRes.REFRESH);
        if(mIsRefreshing) {
            item.setActionView(R.layout.action_bar_indeterminate_progress);
        } else {
            item.setActionView(null);
        }

        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home: {
                Intent i = new Intent(this, DashboardChoiceActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                return true;
            }
            case MenuRes.REFRESH: {
                refreshCurrentFragment();
                return true;
            }

            case MenuRes.SEARCH: {

                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

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

    @Override
    public void onStartRefresh() {
        mIsRefreshing = true;
        invalidateOptionsMenu();
    }

    private void refreshCurrentFragment() {
        BaseFragment f = (BaseFragment) mTabsAdapter.getCurrentItem();
        f.startRefresh();
    }

    private static class MenuRes {
        public static final int SEARCH = 0;

        public static final int REFRESH = 1;
    }
}
