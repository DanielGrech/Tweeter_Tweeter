package com.DGSD.TweeterTweeter.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import twitter4j.*;

/**
 * Author: Daniel Grech
 * Date: 22/11/11 3:23 PM
 * Description :
 */
public class Database {
    private static final String TAG = Database.class.getSimpleName();

    private static final int VERSION = 1;

    public static final String DATABASE = "tweetertweeter.db";

    private final DbHelper mDb;

    public Database(Context c) {
        mDb = new DbHelper(c);
    }

    public SQLiteDatabase getReader() {
        return mDb.getReadableDatabase();
    }

    public SQLiteDatabase getWriter() {
        return mDb.getWritableDatabase();
    }

    /**
     * Generates a ContentValues object from a Status object
     * which can be used to insert into the database 
     * @param user The username for the current user
     * @param status The status object containing the required data
     * @return A ContentValues object ready for database insertion
     */
    public static synchronized ContentValues createTimelineContentValues(String user, Status status) {
        ContentValues values = new ContentValues();


        String mediaEntities = "";
        String hashtagEntities = "";
        String urlEntities = "";
        String userEntities = "";
        String placeName = "";
        String latitude = "";
        String longitude = "";
        String retweetedScreenName = "";
        String retweetedUserImg = "";

        if(status.getMediaEntities() != null) {
            for(MediaEntity me: status.getMediaEntities()) {
                mediaEntities += me.getMediaURL().toString() + ",";
            }
        }

        if(status.getHashtagEntities() != null) {
            for(HashtagEntity ht: status.getHashtagEntities()) {
                hashtagEntities += ht.getText()+ ",";
            }
        }

        if(status.getURLEntities() != null) {
            for(URLEntity url: status.getURLEntities()) {
                urlEntities += url.getExpandedURL() + ",";
            }
        }

        if(status.getUserMentionEntities() != null) {
            for(UserMentionEntity um : status.getUserMentionEntities()) {
                userEntities += um.getScreenName()+ ",";
            }
        }

        if( status.getPlace() != null ) {
            placeName = status.getPlace().getName();
        }

        if(status.getGeoLocation() != null) {
            latitude = Double.toString(status.getGeoLocation().getLatitude());
            longitude = Double.toString(status.getGeoLocation().getLongitude());
        }

        if(status.getRetweetedStatus() != null) {
            retweetedScreenName =
                    status.getRetweetedStatus().getUser().getScreenName();

            retweetedUserImg =
                    status.getRetweetedStatus().getUser().getProfileImageURL().toString().replace("_normal.", "_bigger.");
        }

        if(user != null) {
            values.put(Field.USER, user);
        }

        values.put(Field.ID, Long.toString(status.getId()));
        values.put(Field.CREATED_AT, Long.toString(status.getCreatedAt().getTime()));
        values.put(Field.TEXT, status.getText());
        values.put(Field.USER_NAME, status.getUser().getName());
        values.put(Field.SCREEN_NAME, status.getUser().getScreenName());
        values.put(Field.IMG, status.getUser().getProfileImageURL().toString().replace("_normal.", "_bigger."));
        values.put(Field.FAV, status.isFavorited() ? 1 : 0);
        values.put(Field.SRC, status.getSource());
        values.put(Field.IN_REPLY, status.getInReplyToScreenName());
        values.put(Field.ORIG_TWEET, retweetedScreenName);
        values.put(Field.ORIG_TWEET_IMG, retweetedUserImg);
        values.put(Field.RETWEET_COUNT, status.getRetweetCount());
        values.put(Field.PLACE_NAME, placeName);
        values.put(Field.LAT, latitude);
        values.put(Field.LONG, longitude);
        values.put(Field.MEDIA_ENT, mediaEntities);
        values.put(Field.HASH_ENT, hashtagEntities);
        values.put(Field.URL_ENT, urlEntities);
        values.put(Field.USER_ENT, userEntities);


        return values;
    }

    /**
     *
     * @return Timestamp of the latest status we have it the database
     */
    public Long getLastTweetId(final String table) {
        SQLiteDatabase db = mDb.getReadableDatabase();
        try {
            String[] max_created = new String[] {
                    new StringBuilder().append("MAX(")
                            .append(Field.ID)
                            .append(")")
                            .toString()
            };

            Cursor cursor = db.query(table, max_created,
                    null, null, null,null, null);

            try {
                return cursor.moveToFirst() ? cursor.getLong(0) : -1;
            } finally {
                cursor.close();
            }
        } finally {
            db.close();
        }
    }

    private class DbHelper extends SQLiteOpenHelper {
        public DbHelper(Context context) {
            super(context, DATABASE, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, "Creating database: " + DATABASE);

            String temp = "create table " + Table.TEMPLATE + " (" +
                    Field.USER + " text, " +
                    Field.ID + " text primary key, " +
                    Field.CREATED_AT + " text, " +
                    Field.TEXT + " text, " +
                    Field.USER_NAME + " text, " +
                    Field.SCREEN_NAME + " text, "+
                    Field.IMG + " text, " +
                    Field.FAV + " int, " +
                    Field.SRC + " text, " +
                    Field.IN_REPLY + " text, " +
                    Field.ORIG_TWEET + " text, " +
                    Field.ORIG_TWEET_IMG + " text, " +
                    Field.RETWEET_COUNT + " int, " +
                    Field.PLACE_NAME + " text, " +
                    Field.LAT + " text, " +
                    Field.LONG + " text, " +
                    Field.MEDIA_ENT + " text, " +
                    Field.HASH_ENT + " text, " +
                    Field.URL_ENT + " text, " +
                    Field.USER_ENT + " text)";

            db.execSQL(temp.replace(Table.TEMPLATE, Table.HOME_TIMELINE));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table " + Table.HOME_TIMELINE);
            this.onCreate(db);
        }
    }

    public static class Table {
        public static final String TEMPLATE = "<!@#Template!@#>";

        public static final String HOME_TIMELINE = "home_timeline";
    }

    public static class Field {
        public static final String ID = "_id";
        public static final String CREATED_AT = "created_at";
        public static final String TEXT = "txt";
        public static final String USER = "user";
        public static final String USER_NAME = "user_name";
        public static final String IMG = "imageurl";
        public static final String FAV = "isFavourite";
        public static final String SRC = "source";
        public static final String SCREEN_NAME = "screen_name";

        public static final String IN_REPLY = "in_reply_to_screenname";
        public static final String ORIG_TWEET = "orig_tweeter_name";
        public static final String ORIG_TWEET_IMG = "orig_tweeter_img";
        public static final String RETWEET_COUNT = "retweet_count";
        public static final String PLACE_NAME = "place_name";
        public static final String LAT = "latitude";
        public static final String LONG = "longitude";
        public static final String MEDIA_ENT = "media_entities";
        public static final String HASH_ENT = "hastag_entities";
        public static final String URL_ENT = "url_entities";
        public static final String USER_ENT = "user_entities";

        public static final String DESC = "description";
        public static final String FOLLOWERS = "follower_count";
        public static final String FRIENDS = "friends_count";
        public static final String NUM_STAT = "num_of_status";
        public static final String NAME = "user_name";
    }

    public static class Ordering {
        public static final String ASC = " ASC";

        public static final String DESC = " DESC";

        public static final String DATE = Field.CREATED_AT;

        public static final String ID = Field.ID;
    }

}
