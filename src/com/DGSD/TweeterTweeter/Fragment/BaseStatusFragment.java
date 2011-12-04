package com.DGSD.TweeterTweeter.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ActionMode;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.DGSD.TweeterTweeter.Data.Database;
import com.DGSD.TweeterTweeter.R;
import com.DGSD.TweeterTweeter.UI.QuickPopup;
import com.DGSD.TweeterTweeter.UI.StatusDataHolder;
import com.github.droidfu.widgets.WebImageView;

/**
 * Author: Daniel Grech
 * Date: 27/11/11 10:06 AM
 * Description :
 */
public abstract class BaseStatusFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SimpleCursorAdapter.ViewBinder, AdapterView.OnItemLongClickListener, AbsListView.OnScrollListener
{
    protected static final String TAG = BaseStatusFragment.class.getSimpleName();

    /* Toggle to indicate whether our list is currently scrolling or not */
    private boolean mBusy;

    protected ListView mListView;

    protected SimpleCursorAdapter mAdapter;

    protected static final String[] FROM = { Database.Field.CREATED_AT, Database.Field.TEXT,
            Database.Field.SCREEN_NAME, Database.Field.IMG, Database.Field.ORIG_TWEET };

    protected static final int[] TO = { R.id.date, R.id.tweet, R.id.name, R.id.image, R.id.status };

    protected static String[] ROWS_TO_RETURN = {
            Database.Field.CREATED_AT,
            Database.Field.TEXT,
            Database.Field.SCREEN_NAME,
            Database.Field.IMG,
            Database.Field.ID,
            Database.Field.FAV,
            Database.Field.ORIG_TWEET,
            Database.Field.ORIG_TWEET_IMG
    };

    protected static class CursorCols {
        public static int created_at = -1;
        public static int text = -1;
        public static int screen_name = -1;
        public static int img = -1;
        public static int id = -1;
        public static int fav = -1;
        public static int orig_tweeter = -1;
        public static int orig_tweeter_img = -1;
    }

    protected StatusDataHolder mLastLongClickItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set up popup action items
        startRefresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        mListView = (ListView) v.findViewById(R.id.list);
        mListView.setOnItemLongClickListener(this);
        mListView.setOnScrollListener(this);
        mListView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation));

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
    public boolean setViewValue(View view, Cursor cursor, int col) {
        if(CursorCols.id < 0) {
            //We need to get the column numbers
            CursorCols.created_at = cursor.getColumnIndex(Database.Field.CREATED_AT);
            CursorCols.text = cursor.getColumnIndex(Database.Field.TEXT);
            CursorCols.screen_name = cursor.getColumnIndex(Database.Field.SCREEN_NAME);
            CursorCols.img = cursor.getColumnIndex(Database.Field.IMG);
            CursorCols.id = cursor.getColumnIndex(Database.Field.ID);
            CursorCols.fav = cursor.getColumnIndex(Database.Field.FAV);
            CursorCols.orig_tweeter = cursor.getColumnIndex(Database.Field.ORIG_TWEET);
            CursorCols.orig_tweeter_img = cursor.getColumnIndex(Database.Field.ORIG_TWEET_IMG);
        }

        if(col == CursorCols.screen_name) {
            TextView tv = (TextView) view;
            String orig_tweeter = cursor.getString(CursorCols.orig_tweeter);

            if(orig_tweeter.length() > 0) {
                tv.setText(orig_tweeter);
            } else {;
                tv.setText(cursor.getString(CursorCols.screen_name));
            }

            return true;
        } else if(col == CursorCols.img) {
            //Only load the image if we are not currently flinging the listview
            WebImageView img = (WebImageView) view;
            if(!mBusy) {
                //If this is a retweet, show the original tweeters img, else show the regular img
                img.setImageUrl(cursor.getString(CursorCols.orig_tweeter_img).length() > 0 ?
                        cursor.getString(CursorCols.orig_tweeter_img) : cursor.getString(CursorCols.img));
                img.loadImage();
            }  else {
                img.reset();
            }

            //Set the tag of this row
            ViewGroup parent = (ViewGroup) view.getParent();
            StatusDataHolder holder = null;

            if(parent.getTag() == null) {
                holder = new StatusDataHolder(cursor.getLong(CursorCols.id),
                        cursor.getString(CursorCols.screen_name),
                        cursor.getString(CursorCols.text),
                        cursor.getString(CursorCols.img),
                        img);
            } else {
                holder = (StatusDataHolder) parent.getTag();
                holder.id = cursor.getLong(CursorCols.id);
                holder.user = cursor.getString(CursorCols.orig_tweeter).length() > 0 ?
                        cursor.getString(CursorCols.orig_tweeter) : cursor.getString(CursorCols.screen_name);
                holder.text = cursor.getString(CursorCols.text);
                holder.img = cursor.getString(CursorCols.orig_tweeter_img).length() > 0 ?
                        cursor.getString(CursorCols.orig_tweeter_img) : cursor.getString(CursorCols.img);
            }

            parent.setTag(holder);

            //Set the bg color if favourite
            if(cursor.getInt(CursorCols.fav) == 1) {
                parent.setBackgroundResource(R.drawable.list_item_bg_favourite);
            } else {
                parent.setBackgroundColor(android.R.color.transparent);
            }

            return true;
        } else if(col == CursorCols.created_at) {
            TextView tv = (TextView) view;
            tv.setText(DateUtils.getRelativeTimeSpanString(mActivity,
                    Long.valueOf(cursor.getString(CursorCols.created_at))));
            return true;
        } else if(col == CursorCols.text) {
            TextView tv = (TextView) view;
            String orig_tweeter = cursor.getString(CursorCols.orig_tweeter);

            //If this is a retweet, get rid of the 'RT @username' at the beginning of a string
            String text = cursor.getString(CursorCols.text);
            if(orig_tweeter.length() > 0) {
                text = text.replaceFirst("^RT @[A-Za-z0-9]+: ", "");
            }

            tv.setText(text);
            return true;
        } else if( col == CursorCols.orig_tweeter) {
            TextView tv = (TextView) view;
            String orig_tweeter = cursor.getString(CursorCols.orig_tweeter);

            if(orig_tweeter.length() > 0) {
                tv.setText(new StringBuilder().append("RT by @").append(cursor.getString(CursorCols.screen_name)).toString());
                tv.setVisibility(View.VISIBLE);
            } else {
                tv.setVisibility(View.GONE);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long id) {
        mLastLongClickItem = (StatusDataHolder) view.getTag();
        mActivity.startActionMode(new StatusItemCallback());
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
                    StatusDataHolder holder = (StatusDataHolder) listview.getChildAt(i).getTag();
                    if(holder != null) {
                        holder.webimageview.setImageUrl(holder.img);
                        holder.webimageview.loadImage();
                    }
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

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

    protected class StatusItemCallback implements ActionMode.Callback {

        private long mTweetId;

        private String mScreenName;

        private String mTweetText;

        private boolean hasError;

        public StatusItemCallback() {
            hasError = false;

            if(mLastLongClickItem == null) {
                //We couldn't get a tweet id :(
                Toast.makeText(getActivity(),
                        "Error getting data for tweet", Toast.LENGTH_SHORT).show();

                hasError = true;
            } else {
                mTweetId = mLastLongClickItem.id;

                mScreenName = mLastLongClickItem.user;

                mTweetText = mLastLongClickItem.text;
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            if(hasError) {
                return false;
            } else {
                actionMode.setTitle(mScreenName + "'s tweet");

                menu.add(Menu.NONE, ActionModeItemId.RETWEET, Menu.NONE, "Retweet" )
                        .setIcon(R.drawable.ic_menu_retweet);

                menu.add(Menu.NONE, ActionModeItemId.REPLY, Menu.NONE, "Reply" )
                        .setIcon(R.drawable.ic_menu_reply);

                menu.add(Menu.NONE, ActionModeItemId.FAVOURITE, Menu.NONE, "Favourite" )
                        .setIcon(R.drawable.ic_menu_favourite);

                menu.add(Menu.NONE, ActionModeItemId.SHARE, Menu.NONE, "Share" )
                        .setIcon(R.drawable.ic_menu_share);

                return true;
            }
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

            boolean handled = false;
            switch (menuItem.getItemId()) {
                case ActionModeItemId.SHARE:
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, mTweetText);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Tweet by " + mScreenName);
                    mActivity.startActivity(Intent.createChooser(sharingIntent, "Share tweet"));
                    break;
            }

            actionMode.finish();

            return handled;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            //mCurrentActionMode = null;
        }
    }
}