package com.DGSD.TweeterTweeter.Activity.Phone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.util.Log;
import com.DGSD.TweeterTweeter.Activity.DashboardChoiceActivity;
import com.DGSD.TweeterTweeter.Fragment.BaseFragment;
import com.DGSD.TweeterTweeter.Fragment.DashboardFragment;
import com.DGSD.TweeterTweeter.Fragment.FavouritesFragment;
import com.DGSD.TweeterTweeter.R;

/**
 * Author: Daniel Grech
 * Date: 22/11/11 2:19 PM
 * Description :
 */
public class FavouritesActivity extends FragmentActivity implements BaseFragment.OnRefreshListener{
    private static final String TAG = FavouritesActivity.class.getSimpleName();

    private ActionBar mActionBar;

    private FragmentManager mFragmentManager;

    private FavouritesFragment mFavouritesFragment;

    private static final String FAVOURITES = "_favourites";

    private boolean mIsRefreshing = false;

    private static final String REFRESHING = "_refreshing";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_dashboard);

        mFragmentManager = getSupportFragmentManager();

        if(savedInstanceState != null) {
            mFavouritesFragment = (FavouritesFragment) mFragmentManager.getFragment(savedInstanceState, FAVOURITES);
            mIsRefreshing = savedInstanceState.getBoolean(REFRESHING, false);
        }

        if(mFavouritesFragment == null) {
            mFavouritesFragment = FavouritesFragment.newInstance();

            mFragmentManager.beginTransaction()
                .replace(android.R.id.content, mFavouritesFragment)
                .commit();
        }

        mFavouritesFragment.setOnRefreshListener(this);

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);

        if(mFavouritesFragment != null && mFavouritesFragment.isAdded()) {
            mFragmentManager.putFragment(out, FAVOURITES, mFavouritesFragment);
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
                mFavouritesFragment.startRefresh();
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

    private static class MenuRes {
        public static final int SEARCH = 0;

        public static final int REFRESH = 1;
    }
}
