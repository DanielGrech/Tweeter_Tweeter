package com.DGSD.TweeterTweeter.Service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;
import com.DGSD.TweeterTweeter.Data.Database;
import com.DGSD.TweeterTweeter.Data.HomeTimelineProvider;
import com.DGSD.TweeterTweeter.TTApplication;
import com.DGSD.TweeterTweeter.TwitterUtils.TwitterSession;
import twitter4j.*;

/**
 * Author: Daniel Grech
 * Date: 22/11/11 4:29 PM
 * Description :
 */
public class DownloadService extends IntentService {
    private static final String TAG = DownloadService.class.getSimpleName();

    public static final String TYPE = "_type";

    private TTApplication mApp;

    private Twitter mTwitter;

    private Database mDb;

    private TwitterSession mSession;

    public DownloadService() {
        super(TAG);
        Log.d(TAG, "DownloadService()");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mApp = (TTApplication) getApplication();

        mSession = mApp.getSession();

        mDb = new Database(this);

        if(mSession != null && mSession.getAccessToken() != null) {
            mTwitter = new TwitterFactory(TTApplication.TWITTER_CONFIG).getInstance(mSession.getAccessToken());
        }

        Log.d(TAG, "onCreate()");
    }

    @Override
    protected void onHandleIntent(Intent inIntent) {
        if(mTwitter == null) {
            //No point continuing
            Log.w(TAG, "onHandleIntent(): Twitter Factory was null. Exiting");
            return;
        }

        int dataType = inIntent.getIntExtra(TYPE, Data.ALL_DATA);

        Intent intent;
        switch(dataType) {
            case Data.ALL_DATA : //Update all data.
                fullUpdate();
                break;
            case Data.HOME_TIMELINE:
                intent = new Intent(updateHomeTimeline());
                intent.putExtra(TYPE, Data.HOME_TIMELINE);
                sendBroadcast(intent);
                break;
        }
    }

    private void fullUpdate() {
        /* Home Timeline Update */
        Intent intent = new Intent(updateHomeTimeline());
        intent.putExtra(TYPE, Data.HOME_TIMELINE);
        sendBroadcast(intent);
    }

    private String updateHomeTimeline() {
        try {
            Paging p = new Paging(1, TTApplication.ELEMENTS_PER_PAGE);

            long latestTweet = mDb.getLastTweetId(Database.Table.HOME_TIMELINE);

            if(latestTweet > 0) {
                p.sinceId(latestTweet);
            }

            ResponseList<Status> timeline = mTwitter.getHomeTimeline(p);

            if(timeline.size() > 0) {
                ContentResolver cr = getContentResolver();
                for(int i = 0, size = timeline.size(); i < size; i++) {
                    Status status = timeline.get(i);

                    if(cr.insert(HomeTimelineProvider.CONTENT_URI,
                            Database.createTimelineContentValues(null, status)) == null) {
                        throw new Exception("Insert return URI was null");
                    }
                }

                return Result.DATA;
            } else {
                return Result.NO_DATA;
            }
        } catch(Exception e) {
            Log.e(TAG, "Error updating home timeline", e);
            return Result.ERROR;
        }
    }

    public static final class Data {
        public static final int ALL_DATA = -1;
        public static final int HOME_TIMELINE = 0;
    }

    public static class Result {
        public static final String DATA = "com.DGSD.TweeterTweeter.DATA";
        public static final String NO_DATA = "com.DGSD.TweeterTweeter.NO_DATA";
        public static final String ERROR = "com.DGSD.TweeterTweeter.ERROR";
    }
}
