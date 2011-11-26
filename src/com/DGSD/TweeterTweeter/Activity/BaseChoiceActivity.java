package com.DGSD.TweeterTweeter.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.DGSD.TweeterTweeter.Utils.Utils;

/**
 * Created By: Daniel Grech
 * Date: 6/11/11
 * Description:Base class which helps choose between 2 classes, one for tablets, one for phones
 */
public abstract class BaseChoiceActivity extends Activity {

    public abstract Intent getPhoneIntent();

    public abstract Intent getTabletIntent();

    @Override
    public void onCreate(Bundle  savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = null;

        if(Utils.isTablet(this)) {
            intent = getTabletIntent();
        } else {
            intent = getPhoneIntent();
        }

        //Pass on any extras we've already received
        intent.putExtras(getIntent());

        //Make our choice!
        startActivity(intent);
        finish();
    }
}
