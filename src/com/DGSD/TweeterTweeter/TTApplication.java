package com.DGSD.TweeterTweeter;

import android.app.Application;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.DGSD.TweeterTweeter.TwitterUtils.TwitterSession;

/**
 * Author: Daniel Grech
 * Date: 21/11/11 3:30 PM
 * Description :
 */
public class TTApplication extends Application {
    private static final String TAG = TTApplication.class.getSimpleName();

    public static String CONSUMER_KEY;

    public static String CONSUMER_SECRET;

    //Placeholder for our session information
    private TwitterSession mSession;

    @Override
    public void onCreate() {
        super.onCreate();

        //Set strict mode
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

        CONSUMER_KEY = getResources().getString(R.string.consumer_key);

		CONSUMER_SECRET = getResources().getString(R.string.consumer_secret);

        mSession = new TwitterSession(this);
    }

    public TwitterSession getSession() {
        return mSession;
    }
}
