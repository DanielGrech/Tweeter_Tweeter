package com.DGSD.TweeterTweeter.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import com.DGSD.TweeterTweeter.Data.Database;
import com.DGSD.TweeterTweeter.Data.HomeTimelineProvider;
import com.DGSD.TweeterTweeter.Service.DownloadService;

/**
 * Author: Daniel Grech
 * Date: 22/11/11 2:47 PM
 * Description :
 *
 * TODO:
 *       - Set an image as empty list view
 *       - Long press on list item = <IF phone THEN quick_action ELSE IF tablet THEN action_bar_contextual>
 *       - Tap on list item = new activity with single tweet
 *
 */
public class HomeTimelineFragment extends BaseStatusFragment {
    private static final String TAG = HomeTimelineFragment.class.getSimpleName();

    public static HomeTimelineFragment newInstance() {
        HomeTimelineFragment f = new HomeTimelineFragment();

        return f;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Get a cursor for all entry items
        return new CursorLoader(getActivity(), HomeTimelineProvider.CONTENT_URI,
                ROWS_TO_RETURN, null, null,
                Database.Ordering.DATE + Database.Ordering.DESC);
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
            intent.putExtra(DownloadService.TYPE, DownloadService.Data.HOME_TIMELINE);
            getActivity().startService(intent);
        } else {
            Log.w(TAG, "startRefresh():Activity was null");
        }
    }
}
