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
        Intent intent = new Intent(this, com.DGSD.TweeterTweeter.Activity.Phone.MainActivity.class);

        return intent;
    }

    @Override
    public Intent getTabletIntent() {
        return null;
    }

    public static class EXTRA {
        public static final String SHOW_SCREEN = "_screen";

        public static final int HOME_TIMELINE = 1;

        public static final int MENTIONS = 2;

        public static final int DM = 3;
    }
}
