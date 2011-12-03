package com.DGSD.TweeterTweeter.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.widget.AdapterView;
import com.DGSD.TweeterTweeter.Data.Database;
import com.DGSD.TweeterTweeter.Data.FavouritesProvider;
import com.DGSD.TweeterTweeter.Data.FollowingProvider;
import com.DGSD.TweeterTweeter.Service.DownloadService;

/**
 * Author: Daniel Grech
 * Date: 22/11/11 2:47 PM
 * Description :
 */
public class FollowingFragment extends BasePeopleFragment {
    private static final String TAG = FollowingFragment.class.getSimpleName();

    public static FollowingFragment newInstance() {
        FollowingFragment f = new FollowingFragment();

        return f;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Get a cursor for all entry items
        return new CursorLoader(getActivity(), FollowingProvider.CONTENT_URI,
                ROWS_TO_RETURN, null, null,
                Database.Ordering.SCREEN_NAME + Database.Ordering.ASC);
    }

    @Override
    public void startRefresh() {
        if(getActivity() != null) {
            Log.i(TAG, "startRefresh()");
            if(mRefreshListener != null) {
                mRefreshListener.onStartRefresh();
            } else {
                Log.w(TAG, "Refresh listener was null");
            }

            Intent intent = new Intent(getActivity(), DownloadService.class);
            intent.putExtra(DownloadService.TYPE, DownloadService.Data.FOLLOWING);
            getActivity().startService(intent);
        } else {
            Log.w(TAG, "startRefresh():Activity was null");
        }
    }
}
