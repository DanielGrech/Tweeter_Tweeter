package com.DGSD.TweeterTweeter.Activity.Phone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.Window;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.DGSD.TweeterTweeter.Activity.MainChoiceActivity;
import com.DGSD.TweeterTweeter.Fragment.LoginFragment;
import com.DGSD.TweeterTweeter.R;
import com.DGSD.TweeterTweeter.TTApplication;
import com.DGSD.TweeterTweeter.TwitterUtils.TwDialogListener;
import com.DGSD.TweeterTweeter.TwitterUtils.TwitterConnection;
import com.DGSD.TweeterTweeter.UI.TabsAdapter;
import com.google.android.maps.MapView;
import twitter4j.*;

/**
 * Author: Daniel Grech
 * Date: 21/11/11 4:12 PM
 * Description :
 */
public class LoginActivity extends FragmentActivity implements TwDialogListener {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private ActionBar mActionBar;

    private TabsAdapter mTabsAdapter;

    private LoginFragment mFragment;

    private FragmentManager mFragmentManager;

    private TwitterConnection mTwitterConnection;

    private static final String LOGIN_FRAGMENT = "_login_fragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        mFragmentManager = getSupportFragmentManager();

        if(savedInstanceState != null) {
            mFragment = (LoginFragment) mFragmentManager.getFragment(savedInstanceState, LOGIN_FRAGMENT);
        }

        if(mFragment == null) {
            Log.v(TAG, "Fragment was null, creating a new one");
            mFragment = LoginFragment.newInstance();
        } else {
            Log.v(TAG, "Fragment was not null");
        }

        if(!mFragment.isAdded()) {
            Log.v(TAG, "Fragment hasnt been added. Adding now");
            mFragmentManager.beginTransaction().replace(android.R.id.content, mFragment).commit();
        }

        mActionBar = getSupportActionBar();

        //Make sure the progress bar is hidden
        setProgressBarIndeterminateVisibility(Boolean.FALSE);

        //Initialize the twitter connection
        mTwitterConnection = new TwitterConnection((TTApplication) this.getApplication(), this);

        mTwitterConnection.setListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        if(mFragment != null) {
            mFragmentManager.putFragment(out, LOGIN_FRAGMENT, mFragment);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MenuRes.SIGN_IN, 0, "Sign In")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        menu.add(0, MenuRes.SEARCH, 0, "Search")
                .setIcon(R.drawable.ic_menu_search)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(0, MenuRes.SETTINGS, 0, "Settings")
                .setIcon(R.drawable.ic_menu_settings)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        menu.add(0, MenuRes.ABOUT, 0, "About")
                .setIcon(R.drawable.ic_menu_help)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case MenuRes.SIGN_IN:
                mTwitterConnection.authorize();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onComplete(String value) {
        Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, MainChoiceActivity.class);

        startActivity(intent);
        finish();
    }

    @Override
    public void onError(String value) {
        Log.e(TAG, "Error logging in: " + value);

        Toast.makeText(this, "Error logging in. Please try again", Toast.LENGTH_LONG).show();
    }

    private static class MenuRes {
        public static final int SIGN_IN = 0;

        public static final int SEARCH = 1;

        public static final int SETTINGS = 2;

        public static final int ABOUT = 3;
    }
}
