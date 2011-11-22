package com.DGSD.TweeterTweeter.Activity;

import android.content.Intent;

/**
 * Author: Daniel Grech
 * Date: 22/11/11 2:15 PM
 * Description :
 */
public class DashboardChoiceActivity extends BaseChoiceActivity {
    private static final String TAG = DashboardChoiceActivity.class.getSimpleName();

    @Override
    public Intent getPhoneIntent() {
        return new Intent(this, com.DGSD.TweeterTweeter.Activity.Phone.DashboardActivity.class);
    }

    @Override
    public Intent getTabletIntent() {
        return null;
    }
}
