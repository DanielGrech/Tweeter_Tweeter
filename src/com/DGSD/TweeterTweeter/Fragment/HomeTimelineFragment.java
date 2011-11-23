package com.DGSD.TweeterTweeter.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.DGSD.TweeterTweeter.Data.Database;
import com.DGSD.TweeterTweeter.Data.HomeTimelineProvider;
import com.DGSD.TweeterTweeter.R;
import com.DGSD.TweeterTweeter.Service.DownloadService;
import com.github.droidfu.widgets.WebImageView;

/**
 * Author: Daniel Grech
 * Date: 22/11/11 2:47 PM
 * Description :
 *
 * TODO: - Set an image as empty list view
 *       - Bug when pressing refresh after rotate (while dm tab is selected)
 */
public class HomeTimelineFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, SimpleCursorAdapter.ViewBinder {
    private static final String TAG = HomeTimelineFragment.class.getSimpleName();

    private ListView mListView;

    private SimpleCursorAdapter mAdapter;

    private static final String[] FROM = { Database.Field.CREATED_AT, Database.Field.TEXT,
            Database.Field.SCREEN_NAME, Database.Field.IMG, Database.Field.ID };

    private static final int[] TO = { R.id.date, R.id.tweet, R.id.name, R.id.image };

    private static String[] ROWS_TO_RETURN = {
            Database.Field.CREATED_AT,
            Database.Field.TEXT,
            Database.Field.SCREEN_NAME,
            Database.Field.IMG,
            Database.Field.ID
    };

    private static class CursorCols {
        public static int created_at = -1;
        public static int text = -1;
        public static int screen_name = -1;
        public static int img = -1;
        public static int id = -1;
    }

    public static HomeTimelineFragment newInstance() {
        HomeTimelineFragment f = new HomeTimelineFragment();

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        mListView = (ListView) v.findViewById(R.id.list);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item_timeline, null, FROM, TO, 0);
        mAdapter.setViewBinder(this);

        mListView.setAdapter(mAdapter);

        // Prepare the loader.  Either re-connect with an existing one, or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }

    @Override
    public boolean setViewValue(View view, Cursor cursor, int col) {
        if(CursorCols.id < 0) {
            //We need to get the column numbers
            CursorCols.created_at = cursor.getColumnIndex(Database.Field.CREATED_AT);
            CursorCols.text = cursor.getColumnIndex(Database.Field.TEXT);
            CursorCols.screen_name = cursor.getColumnIndex(Database.Field.SCREEN_NAME);
            CursorCols.img = cursor.getColumnIndex(Database.Field.IMG);
            CursorCols.id = cursor.getColumnIndex(Database.Field.ID);
        }

        if(col == CursorCols.img) {
            WebImageView img = (WebImageView) view;
            img.setImageUrl(cursor.getString(CursorCols.img));
            img.loadImage();

            return true;
        } else if(col == CursorCols.id) {
            //Set the tag of this row to our id
            ((ViewGroup)view.getParent()).setTag(cursor.getInt(CursorCols.id));

            return true;
        } else if(col == CursorCols.created_at) {
            TextView tv = (TextView) view;
            tv.setText(DateUtils.getRelativeTimeSpanString(mActivity,
                    Long.valueOf(cursor.getString(CursorCols.created_at))));
            return true;
        }

        return false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Get a cursor for all entry items
        return new CursorLoader(getActivity(), HomeTimelineProvider.CONTENT_URI,
                ROWS_TO_RETURN, null, null,
                Database.Ordering.DATE + Database.Ordering.DESC);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Swap the new cursor in. (Old cursor is automatically closed)
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }

    @Override
    public void startRefresh() {
        if(getActivity() != null) {
            Intent intent = new Intent(getActivity(), DownloadService.class);
            intent.putExtra(DownloadService.TYPE, DownloadService.Data.HOME_TIMELINE);
            getActivity().startService(intent);
        } else {
            Log.w(TAG, "startRefresh():Activity was null");
        }
    }

    @Override
    public void onRefreshData() {

    }

    @Override
    public void onRefreshNoData() {

    }

    @Override
    public void onRefreshError() {

    }
}
