package com.DGSD.TweeterTweeter.Activity;

import android.content.Intent;

/**
 * Author: Daniel Grech
 * Date: 22/11/11 2:17 PM
 * Description :
 */
public class MainChoiceActivity extends BaseChoiceActivity {
    private static final String TAG = MainChoiceActivity.class.getSimpleName();

    @Override
    public Intent getPhoneIntent() {
        return new Intent(this, com.DGSD.TweeterTweeter.Activity.Phone.MainActivity.class);
    }

    @Override
    public Intent getTabletIntent() {
        return null;
    }
}
