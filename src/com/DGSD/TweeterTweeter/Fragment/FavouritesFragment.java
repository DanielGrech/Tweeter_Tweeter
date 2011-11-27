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
import com.DGSD.TweeterTweeter.Data.MentionsProvider;
import com.DGSD.TweeterTweeter.Service.DownloadService;

/**
 * Author: Daniel Grech
 * Date: 22/11/11 2:47 PM
 * Description :
 */
public class FavouritesFragment extends BaseStatusFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SimpleCursorAdapter.ViewBinder, AdapterView.OnItemLongClickListener {
    private static final String TAG = FavouritesFragment.class.getSimpleName();

    public static FavouritesFragment newInstance() {
        FavouritesFragment f = new FavouritesFragment();

        return f;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Get a cursor for all entry items
        return new CursorLoader(getActivity(), FavouritesProvider.CONTENT_URI,
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
            intent.putExtra(DownloadService.TYPE, DownloadService.Data.FAVOURITES);
            getActivity().startService(intent);
        } else {
            Log.w(TAG, "startRefresh():Activity was null");
        }
    }
}
