package com.DGSD.TweeterTweeter.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.DGSD.TweeterTweeter.TTApplication;
import com.DGSD.TweeterTweeter.TwitterUtils.TwitterSession;

/**
 * Author: Daniel Grech
 * Date: 21/11/11 4:07 PM
 * Description : A class to decide whether the user has already logged in before or not.
 */
public class StartupChoiceActivity extends Activity {
    private static final String TAG = StartupChoiceActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = null;

        TTApplication app = (TTApplication) getApplication();

        TwitterSession session = app.getSession();

        if(session.getUsername() == null) {
            //Haven't logged in before
            Log.d(TAG, "Havent logged in before. Redirecting to login activity");
            intent = new Intent(this, LoginChoiceActivity.class);
        } else {
            Log.d(TAG, "Logged in before. Redirecting to main activity");
            intent = new Intent(this, MainChoiceActivity.class);
        }

        //Pass on any extras we've already received
        intent.putExtras(getIntent());

        //Make our choice!
        startActivity(intent);
        finish();
    }
}
