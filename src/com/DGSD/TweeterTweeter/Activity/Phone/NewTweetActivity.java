package com.DGSD.TweeterTweeter.Activity.Phone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItem;
import com.DGSD.TweeterTweeter.Activity.DashboardChoiceActivity;
import com.DGSD.TweeterTweeter.Fragment.NewTweetFragment;
import com.DGSD.TweeterTweeter.R;

/**
 * Author: Daniel Grech
 * Date: 22/11/11 2:19 PM
 * Description :
 */
public class NewTweetActivity extends FragmentActivity {
    private static final String TAG = NewTweetActivity.class.getSimpleName();

    private FragmentManager mFragmentManager;

    private ActionBar mActionBar;

    private NewTweetFragment mNewTweetFragment;

    private static final String NEW_TWEET = "_new_tweet";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFragmentManager = getSupportFragmentManager();

        if(savedInstanceState != null) {
            mNewTweetFragment = (NewTweetFragment) mFragmentManager.getFragment(savedInstanceState, NEW_TWEET);
        }

        if(mNewTweetFragment == null) {
            mNewTweetFragment = NewTweetFragment.newInstance();
        }

        mFragmentManager.beginTransaction()
                        .replace(android.R.id.content, mNewTweetFragment)
                        .commit();

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);

        if(mNewTweetFragment != null && mNewTweetFragment.isAdded()) {
            mFragmentManager.putFragment(out, NEW_TWEET, mNewTweetFragment);
        }
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

            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

}
