package com.DGSD.TweeterTweeter.Service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.DGSD.TweeterTweeter.Data.Database;
import com.DGSD.TweeterTweeter.Data.FavouritesProvider;
import com.DGSD.TweeterTweeter.Data.HomeTimelineProvider;
import com.DGSD.TweeterTweeter.Data.MentionsProvider;
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
            case Data.MENTIONS:
                intent = new Intent(updateMentions());
                intent.putExtra(TYPE, Data.MENTIONS);
                sendBroadcast(intent);
                break;
            case Data.FAVOURITES:
                intent = new Intent(updateFavourites());
                intent.putExtra(TYPE, Data.FAVOURITES);
                sendBroadcast(intent);
                break;
        }
    }

    private void fullUpdate() {
        Intent intent = null;

        /* Home Timeline Update */
        intent = new Intent(updateHomeTimeline());
        intent.putExtra(TYPE, Data.HOME_TIMELINE);
        sendBroadcast(intent);

        /* Mentions Update */
        intent = new Intent(updateMentions());
        intent.putExtra(TYPE, Data.MENTIONS);
        sendBroadcast(intent);

        /* Favourites Update */
        intent = new Intent(updateFavourites());
        intent.putExtra(TYPE, Data.FAVOURITES);
        sendBroadcast(intent);

    }

    private String updateHomeTimeline() {
        try {
            ResponseList<Status> timeline = mTwitter.getHomeTimeline(getPagingSinceLast(Database.Table.HOME_TIMELINE));

            if(timeline != null && timeline.size() > 0) {
                insertTimelineValues(timeline, HomeTimelineProvider.CONTENT_URI);
                return Result.DATA;
            } else {
                return Result.NO_DATA;
            }
        } catch(Exception e) {
            Log.e(TAG, "Error updating home timeline", e);
            return Result.ERROR;
        }
    }

    private String updateMentions() {
        try {
            ResponseList<Status> mentions = mTwitter.getMentions(getPagingSinceLast(Database.Table.MENTIONS));

            if(mentions != null && mentions.size() > 0) {
                insertTimelineValues(mentions, MentionsProvider.CONTENT_URI);
                return Result.DATA;
            } else {
                return Result.NO_DATA;
            }
        } catch(Exception e) {
            Log.e(TAG, "Error updating mentions", e);
            return Result.ERROR;
        }
    }

    private String updateFavourites() {
        try {
            ResponseList<Status> favourites = mTwitter.getFavorites(getPagingSinceLast(Database.Table.FAVOURITES));

            if(favourites != null && favourites.size() > 0) {
                insertTimelineValues(favourites, FavouritesProvider.CONTENT_URI);
                return Result.DATA;
            } else {
                return Result.NO_DATA;
            }
        } catch(Exception e) {
            Log.e(TAG, "Error updating favourites", e);
            return Result.ERROR;
        }
    }

    /**
     * Get the most recent id in a table
     *
     * @param table Table to check
     * @return The most recent entry id in the table
     */
    private Paging getPagingSinceLast(String table) {
        Paging p = new Paging(1, TTApplication.ELEMENTS_PER_PAGE);

        long latestTweet = mDb.getLastTweetId(table);

        if(latestTweet > 0) {
            p.sinceId(latestTweet);
        }

        return p;
    }

    /**
     *
     * @param statuses A list of statuses to insert
     * @param provider A provider to run the query against
     * @throws Exception when error occurs while inserting rows
     */
    private void insertTimelineValues(ResponseList<Status> statuses, Uri provider) throws Exception {
        ContentResolver cr = getContentResolver();
        for(int i = 0, size = statuses.size(); i < size; i++) {
            Status status = statuses.get(i);

            if(cr.insert(provider,
                    Database.createTimelineContentValues(null, status)) == null) {
                throw new Exception("Insert return URI was null");
            }
        }
    }

    public static final class Data {
        public static final int ALL_DATA = -1;
        public static final int HOME_TIMELINE = 0;
        public static final int MENTIONS = 1;
        public static final int FAVOURITES = 2;
    }

    public static class Result {
        public static final String DATA = "com.DGSD.TweeterTweeter.DATA";
        public static final String NO_DATA = "com.DGSD.TweeterTweeter.NO_DATA";
        public static final String ERROR = "com.DGSD.TweeterTweeter.ERROR";
    }
}
