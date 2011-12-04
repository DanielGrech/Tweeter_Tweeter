package com.DGSD.TweeterTweeter;

import android.app.AlarmManager;
import android.app.Application;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Config;
import android.util.Log;
import com.DGSD.TweeterTweeter.TwitterUtils.TwitterSession;
import com.github.droidfu.DroidFuApplication;
import twitter4j.Twitter;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.conf.PropertyConfiguration;
import twitter4j.media.MediaProvider;

/**
 * Author: Daniel Grech
 * Date: 21/11/11 3:30 PM
 * Description :
 */
public class TTApplication extends DroidFuApplication {
    private static final String TAG = TTApplication.class.getSimpleName();

    public static String CONSUMER_KEY;

    public static String CONSUMER_SECRET;

    public static Configuration TWITTER_CONFIG;

    public static int ELEMENTS_PER_PAGE = 100;

    public static int UPDATE_INTERVAL_NEVER = -1;

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

        TWITTER_CONFIG = new ConfigurationBuilder().setOAuthConsumerKey(CONSUMER_KEY)
                .setOAuthConsumerSecret(CONSUMER_SECRET)
                .setIncludeEntitiesEnabled(true)
                .build();

        mSession = new TwitterSession(this);
    }

    public TwitterSession getSession() {
        return mSession;
    }

    public long getUpdateInterval() {
        return AlarmManager.INTERVAL_HOUR;
    }
}
