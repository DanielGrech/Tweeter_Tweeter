package com.DGSD.TweeterTweeter.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created By: Daniel Grech
 * Date: 7/11/11
 * Description: Base instance for all providers
 */
public abstract class BaseProvider extends ContentProvider {
    protected static final String TAG = BaseProvider.class.getSimpleName();

    public static final int MULTIPLE = 100;

    public static final int SINGLE = 110;

    protected static final UriMatcher mURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private Database mDatabase;

    public abstract String getTable();

    @Override
    public boolean onCreate() {
        mDatabase = new Database(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(getTable());

        int uriType = mURIMatcher.match(uri);

        if(uriType == SINGLE) {
            //We want to get a single item
            queryBuilder.appendWhere( new StringBuilder().append(Database.Field.ID).append("=").append(uri.getLastPathSegment()).toString());
        } else if(uriType == MULTIPLE) {
            //No filter. Return all fields
        } else {
           throw new IllegalArgumentException("Unknown URI");
        }

        try {
            Cursor cursor = queryBuilder.query(mDatabase.getReader(),
                projection, selection, selectionArgs, null, null, sort);

            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        } catch(Exception e) {
            Log.e(TAG, "Error querying database: " + e.toString());
            return null;
        }
    }

    @Override
    public String getType(Uri uri) {
        int uriType = mURIMatcher.match(uri);

        if(uriType == SINGLE || uriType == MULTIPLE) {
            return String.valueOf(uriType);
        } else {
            return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = mURIMatcher.match(uri);

        if (uriType != MULTIPLE) {
            throw new IllegalArgumentException("Invalid URI for insert");
        }

        SQLiteDatabase sqlDB = mDatabase.getWriter();

        try {
            long newID = sqlDB.insertOrThrow(getTable(), null, values);

            if (newID > 0) {
                Uri newUri = ContentUris.withAppendedId(uri, newID);
                getContext().getContentResolver().notifyChange(uri, null);
                return newUri;
            } else {
                throw new SQLException("Failed to insert row into " + uri);
            }
        } catch (SQLiteConstraintException e) {
            Log.e(TAG, "ContentValues: " + values);
            Log.e(TAG, "Ignoring constraint failure", e);
        } catch(Exception e) {
            Log.e(TAG, "Error inserting into database", e);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = mURIMatcher.match(uri);

        SQLiteDatabase sqlDB = mDatabase.getWriter();

        try {
            int rowsAffected = 0;
            if(uriType == SINGLE) {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsAffected = sqlDB.delete(getTable(),
                            new StringBuilder().append(Database.Field.ID)
                                               .append("=")
                                               .append(id)
                                               .toString(),
                            null);
                } else {
                    rowsAffected = sqlDB.delete(getTable(),
                            new StringBuilder().append(selection)
                                               .append(" and ")
                                               .append(Database.Field.ID)
                                               .append("=")
                                               .append(id)
                                               .toString(),
                            selectionArgs);
                }
            } else if(uriType == MULTIPLE) {
                rowsAffected = sqlDB.delete(getTable(), selection, selectionArgs);
            } else {
                throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
            }

            getContext().getContentResolver().notifyChange(uri, null);
            return rowsAffected;
        } catch(Exception e) {
            Log.e(TAG, "Error deleting item: " + e.toString());
            return 0;
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int uriType = mURIMatcher.match(uri);

        SQLiteDatabase sqlDB = mDatabase.getWriter();

        try {
            int rowsAffected = 0;
            if(uriType == SINGLE) {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsAffected = sqlDB.update(getTable(), contentValues,
                            new StringBuilder().append(Database.Field.ID)
                                               .append("=")
                                               .append(id)
                                               .toString(),
                            null);
                } else {
                    rowsAffected = sqlDB.update(getTable(), contentValues,
                            new StringBuilder().append(selection)
                                               .append(" and ")
                                               .append(Database.Field.ID)
                                               .append("=")
                                               .append(id)
                                               .toString(),
                            selectionArgs);
                }
            } else if(uriType == MULTIPLE) {
                rowsAffected = sqlDB.update(getTable(), contentValues,
                            selection, selectionArgs);
            } else {
                throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
            }

            getContext().getContentResolver().notifyChange(uri, null);
            return rowsAffected;
        }catch(Exception e) {
            Log.e(TAG, "Error updating entry: " + e.toString());
            return 0;
        }
    }
}
