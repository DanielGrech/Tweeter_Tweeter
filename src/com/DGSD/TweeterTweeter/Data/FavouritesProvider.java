package com.DGSD.TweeterTweeter.Data;

import android.net.Uri;

/**
 * Created By: Daniel Grech
 * Date: 2/11/11
 * Description:
 */
public class FavouritesProvider extends BaseProvider {
    private static final String TAG = FavouritesProvider.class.getSimpleName();

    protected static final String AUTHORITY = "com.DGSD.TweeterTweeter.Data.FavouritesProvider";

    private static final String BASE_PATH = Database.Table.FAVOURITES;

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    static {
        mURIMatcher.addURI(AUTHORITY, BASE_PATH, MULTIPLE);
        mURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", SINGLE);
    }

    @Override
    public String getTable() {
        return Database.Table.FAVOURITES;
    }
}
