package com.DGSD.TweeterTweeter.Fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.DGSD.TweeterTweeter.Data.Database;
import com.DGSD.TweeterTweeter.R;
import com.DGSD.TweeterTweeter.UI.PeopleDataHolder;
import com.DGSD.TweeterTweeter.UI.PopupItem;
import com.DGSD.TweeterTweeter.UI.QuickPopup;
import com.github.droidfu.widgets.WebImageView;

/**
 * Author: Daniel Grech
 * Date: 27/11/11 10:06 AM
 * Description :
 */
public abstract class BasePeopleFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SimpleCursorAdapter.ViewBinder, AdapterView.OnItemLongClickListener, AbsListView.OnScrollListener
{
    protected static final String TAG = BasePeopleFragment.class.getSimpleName();

    /* Toggle to indicate whether our list is currently scrolling or not */
    private boolean mBusy;

    protected ListView mListView;

    protected SimpleCursorAdapter mAdapter;

    protected static final String[] FROM = { Database.Field.SCREEN_NAME, Database.Field.IMG };

    protected static final int[] TO = { R.id.name, R.id.image };

    protected static String[] ROWS_TO_RETURN = {
            Database.Field.SCREEN_NAME,
            Database.Field.IMG,
            Database.Field.ID,
    };

    protected static class CursorCols {
        public static int screen_name = -1;
        public static int img = -1;
        public static int id = -1;

    }

    protected PeopleDataHolder mLastLongClickItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startRefresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        mListView = (ListView) v.findViewById(R.id.list);
        mListView.setOnItemLongClickListener(this);
        mListView.setOnScrollListener(this);
        mListView.setDivider(null);
        mListView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation));

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item_people, null, FROM, TO, 0);
        mAdapter.setViewBinder(this);

        mListView.setAdapter(mAdapter);

        // Prepare the loader.  Either re-connect with an existing one, or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean setViewValue(View view, Cursor cursor, int col) {
        if(CursorCols.id < 0) {
            //We need to get the column numbers
            CursorCols.screen_name = cursor.getColumnIndex(Database.Field.SCREEN_NAME);
            CursorCols.img = cursor.getColumnIndex(Database.Field.IMG);
            CursorCols.id = cursor.getColumnIndex(Database.Field.ID);
        }

        if(col == CursorCols.img) {
            //Only load the image if we are not currently flinging the listview
            WebImageView img = (WebImageView) view;
            if(!mBusy) {
                img.setImageUrl(cursor.getString(CursorCols.img));
                img.loadImage();
            } else {
                img.reset();
            }

            //Set the tag of this row
            ViewGroup parent = (ViewGroup) view.getParent();
            PeopleDataHolder holder = null;

            if(parent.getTag() == null) {
                holder = new PeopleDataHolder(cursor.getLong(CursorCols.id),
                        cursor.getString(CursorCols.screen_name),
                        cursor.getString(CursorCols.img),
                        img);
            } else {
                holder = (PeopleDataHolder) parent.getTag();
                holder.id = cursor.getLong(CursorCols.id);
                holder.name = cursor.getString(CursorCols.screen_name);
                holder.img = cursor.getString(CursorCols.img);
            }

            parent.setTag(holder);

            return true;
        }

        return false;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long id) {
        mLastLongClickItem = (PeopleDataHolder) view.getTag();

        return true;
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
    public void onScrollStateChanged(AbsListView listview, int scrollState) {
        mBusy = (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING);

        if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            int first = listview.getFirstVisiblePosition();
            int count = listview.getChildCount();
            for (int i=0; i<count; i++) {
                if(listview.getChildAt(i) != null) {
                    PeopleDataHolder holder = (PeopleDataHolder) listview.getChildAt(i).getTag();
                    if(holder != null) {
                        holder.webimageview.setImageUrl(holder.img);
                        holder.webimageview.loadImage();
                    }
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView listview, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

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
