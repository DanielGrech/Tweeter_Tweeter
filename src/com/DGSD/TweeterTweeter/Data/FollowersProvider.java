package com.DGSD.TweeterTweeter.Data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.util.LogWriter;
import android.util.Log;

/**
 * Created By: Daniel Grech
 * Date: 2/11/11
 * Description:
 */
public class FollowersProvider extends BaseProvider {
    private static final String TAG = FollowersProvider.class.getSimpleName();

    protected static final String AUTHORITY = "com.DGSD.TweeterTweeter.Data.FollowersProvider";

    private static final String BASE_PATH = Database.Table.FOLLOWERS;

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    static {
        mURIMatcher.addURI(AUTHORITY, BASE_PATH, MULTIPLE);
        mURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", SINGLE);
    }

    @Override
    public String getTable() {
        return Database.Table.FOLLOWERS;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = mURIMatcher.match(uri);

        if (uriType != MULTIPLE) {
            throw new IllegalArgumentException("Invalid URI for insert");
        }

        SQLiteDatabase sqlDB = mDatabase.getWriter();

        try {
            long newID = sqlDB.insertWithOnConflict(getTable(), null, values,
					SQLiteDatabase.CONFLICT_IGNORE);

            if (newID > 0) {
                Uri newUri = ContentUris.withAppendedId(uri, newID);
                getContext().getContentResolver().notifyChange(uri, null);
                return newUri;
            } else {
                Log.w(TAG, "Didnt insert any rows");
            }
        } catch (SQLiteConstraintException e) {
            Log.e(TAG, "ContentValues: " + values);
            Log.e(TAG, "Ignoring constraint failure", e);
        } catch(Exception e) {
            Log.e(TAG, "Error inserting into database", e);
        }
        return null;
    }
}
