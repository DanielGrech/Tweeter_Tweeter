package com.DGSD.TweeterTweeter.Activity;

import android.content.Intent;

/**
 * Author: Daniel Grech
 * Date: 22/11/11 2:15 PM
 * Description :
 */
public class PeopleChoiceActivity extends BaseChoiceActivity {
    private static final String TAG = PeopleChoiceActivity.class.getSimpleName();

    @Override
    public Intent getPhoneIntent() {
        return new Intent(this, com.DGSD.TweeterTweeter.Activity.Phone.PeopleActivity.class);
    }

    @Override
    public Intent getTabletIntent() {
        return null;
    }

    public static class EXTRA {
        public static final String SHOW_SCREEN = "_screen";

        public static final int FOLLOWING = 1;

        public static final int FOLLOWERS = 2;
    }
}
