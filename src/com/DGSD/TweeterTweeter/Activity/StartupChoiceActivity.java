package com.DGSD.TweeterTweeter.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.DGSD.TweeterTweeter.Utils;

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

        /*
            if(hasLoggedInBefore) {
                intent = ListActivity();
            } else {
                intent = LoginChoiceActivity();
            }
         */

        intent = new Intent(this, LoginChoiceActivity.class);

        //Pass on any extras we've already received
        intent.putExtras(getIntent());

        //Make our choice!
        startActivity(intent);
        finish();
    }
}
