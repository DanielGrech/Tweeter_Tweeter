package com.DGSD.TweeterTweeter.Activity.Phone;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.DGSD.TweeterTweeter.Fragment.DashboardFragment;
import com.DGSD.TweeterTweeter.R;

/**
 * Author: Daniel Grech
 * Date: 22/11/11 2:19 PM
 * Description :
 */
public class DashboardActivity extends FragmentActivity {
    private static final String TAG = DashboardActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_dashboard);

        DashboardFragment f = DashboardFragment.newInstance();

        getSupportFragmentManager().beginTransaction()
                                   .add(android.R.id.content, f)
                                   .commit();

    }
}
