package com.DGSD.TweeterTweeter.Activity;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;

/**
 * Author: Daniel Grech
 * Date: 21/11/11 4:10 PM
 * Description :
 */
public class LoginChoiceActivity extends BaseChoiceActivity {
    private static final String TAG = LoginChoiceActivity.class.getSimpleName();

    @Override
    public Intent getPhoneIntent() {
        return new Intent(this, com.DGSD.TweeterTweeter.Activity.Phone.LoginActivity.class);
    }

    @Override
    public Intent getTabletIntent() {
        return null;
    }
}
