package com.DGSD.TweeterTweeter.Service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.DGSD.TweeterTweeter.Data.*;
import com.DGSD.TweeterTweeter.TTApplication;
import com.DGSD.TweeterTweeter.TwitterUtils.TwitterSession;
import twitter4j.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            case Data.FOLLOWERS:
                intent = new Intent(updateFollowers());
                intent.putExtra(TYPE, Data.FOLLOWERS);
                sendBroadcast(intent);
                break;
            case Data.FOLLOWING:
                intent = new Intent(updateFollowing());
                intent.putExtra(TYPE, Data.FOLLOWING);
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

        /* Followers Update */
        intent = new Intent(updateFollowers());
        intent.putExtra(TYPE, Data.FOLLOWERS);
        sendBroadcast(intent);

        /* Following Update */
        intent = new Intent(updateFollowing());
        intent.putExtra(TYPE, Data.FOLLOWING);
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

    private String updateFollowers() {
        ArrayList<Long> mIds = new ArrayList<Long>();
        long cursor = -1;

        //Get the ids of all followers..
        IDs ids;
        String user = mSession.getUsername();
        try {
            do{
                ids =  mTwitter.getFollowersIDs(user, cursor);

                if(ids == null) {
                    Log.w(TAG, "Couldnt find any followers");
                    return Result.NO_DATA;
                }

                long[] idArray = ids.getIDs();

                for(int i = 0, size=idArray.length; i<size ;i++) {
                    mIds.add(idArray[i]);
                }
            }while( (cursor = ids.getNextCursor()) != 0);
            int numIds = mIds.size();
            long tempIds[] = new long[numIds > 100 ? 100 : numIds];

            ResponseList<User> followers = null;

            int currentElement = 0;
            for(int i = 0, size = mIds.size(); i < size; i++) {
                tempIds[currentElement] = mIds.get(i);

                //This is the most the twitter API allows for the lookupUsers call
                if(i == 99 || i == (numIds - 1) ) {
                    currentElement = 0;
                    if(followers == null) {
                        followers = mTwitter.lookupUsers(tempIds);
                    } else {
                        followers.addAll(mTwitter.lookupUsers(tempIds));
                    }
                } else {
                    currentElement++;
                }
            }

            insertUserValues(followers, FollowersProvider.CONTENT_URI);
            return Result.DATA;
        } catch(Exception e) {
            Log.e(TAG, "Error updating followers", e);
            return Result.ERROR;
        }
    }

    private String updateFollowing() {
        ArrayList<Long> mIds = new ArrayList<Long>();
        long cursor = -1;

        //Get the ids of all followers..
        IDs ids;
        String user = mSession.getUsername();
        try {
            do{
                ids =  mTwitter.getFriendsIDs(user, cursor);

                if(ids == null) {
                    Log.w(TAG, "Couldnt find any followers");
                    return Result.NO_DATA;
                }

                long[] idArray = ids.getIDs();

                for(int i = 0, size=idArray.length; i<size ;i++) {
                    mIds.add(idArray[i]);
                }
            }while( (cursor = ids.getNextCursor()) != 0);

            int numIds = mIds.size();
            long tempIds[] = new long[numIds > 100 ? 100 : numIds];

            ResponseList<User> following = null;

            int currentElement = 0;
            for(int i = 0, size = mIds.size(); i < size; i++) {
                tempIds[currentElement] = mIds.get(i);

                //This is the most the twitter API allows for the lookupUsers call
                if(i == 99 || i == (numIds - 1) ) {
                    currentElement = 0;
                    if(following == null) {
                        following = mTwitter.lookupUsers(tempIds);
                    } else {
                        following.addAll(mTwitter.lookupUsers(tempIds));
                    }
                } else {
                    currentElement++;
                }
            }

            insertUserValues(following, FollowingProvider.CONTENT_URI);
            return Result.DATA;
        } catch(Exception e) {
            Log.e(TAG, "Error updating followers", e);
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

    /**
     *
     * @param users A list of users to insert
     * @param provider A provider to run the query against
     * @throws Exception when error occurs while inserting rows
     */
    private void insertUserValues(ResponseList<User> users, Uri provider) throws Exception {
        ContentResolver cr = getContentResolver();
        for(int i = 0, size = users.size(); i < size; i++) {
            User user = users.get(i);

            if(cr.insert(provider,
                    Database.createUserContentValues(null, user)) == null) {
                Log.w(TAG, "Didnt insert any new user values");
            }
        }
    }


    public static final class Data {
        public static final int ALL_DATA = -1;
        public static final int HOME_TIMELINE = 0;
        public static final int MENTIONS = 1;
        public static final int FAVOURITES = 2;
        public static final int FOLLOWERS = 3;
        public static final int FOLLOWING = 4;
    }

    public static class Result {
        public static final String DATA = "com.DGSD.TweeterTweeter.DATA";
        public static final String NO_DATA = "com.DGSD.TweeterTweeter.NO_DATA";
        public static final String ERROR = "com.DGSD.TweeterTweeter.ERROR";
    }
}
