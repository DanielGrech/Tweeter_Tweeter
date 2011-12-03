package com.DGSD.TweeterTweeter.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FilterQueryProvider;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import com.DGSD.TweeterTweeter.Data.Database;
import com.DGSD.TweeterTweeter.Data.FollowersProvider;
import com.DGSD.TweeterTweeter.Data.FollowingProvider;
import com.DGSD.TweeterTweeter.R;
import com.DGSD.TweeterTweeter.Utils.MentionsTokenizer;
import com.DGSD.TweeterTweeter.Utils.Utils;
import com.github.droidfu.widgets.WebImageView;

/**
 * Author: Daniel Grech
 * Date: 26/11/11 6:03 PM
 * Description :
 */
public class NewTweetFragment extends DialogFragment  implements LoaderManager.LoaderCallbacks<Cursor>, SimpleCursorAdapter.ViewBinder {
    private static final String TAG = NewTweetFragment.class.getSimpleName();

    protected static final String[] FROM = { Database.Field.SCREEN_NAME, Database.Field.IMG };

    protected static final int[] TO = { R.id.name, R.id.image };

    protected static String[] ROWS_TO_RETURN = {
            Database.Field.SCREEN_NAME,
            Database.Field.IMG,
            Database.Field.ID
    };

    protected static class CursorCols {
        public static int img = -1;
    }

    public static final int MAX_TWEET_SIZE = 140;

    private static final String TWEET_TEXT = "_tweet_text";

    private static final int GET_GALLERY_IMAGE = 0;

    private static final int GET_CAMERA_IMAGE = 1;

    private TextView mCharacterCount;

    private MenuItem mSubmitAction;

    private MultiAutoCompleteTextView mTweetText;

    private SimpleCursorAdapter mAdapter;

    private String mCurrentFilter;

    public static NewTweetFragment newInstance() {
        final NewTweetFragment f = new NewTweetFragment();

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_tweet, container, false);

        if(getDialog() != null) {
            getDialog().setTitle("New Tweet");
        }

        mTweetText =
                (MultiAutoCompleteTextView) root.findViewById(R.id.text);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCharacterCount = new TextView(getActivity());
        mCharacterCount.setText("140");

        if(savedInstanceState != null) {
            addToTweet(mTweetText, savedInstanceState.getString(TWEET_TEXT, ""));
        } else {
            if(getArguments() != null) {
                addToTweet(mTweetText, getArguments().getString(Extra.TEXT));
            }
        }

        setupMentionCompletion();

        // Prepare the loader.  Either re-connect with an existing one, or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        setupCharacterCounter();

        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(mTweetText != null) {
            outState.putString(TWEET_TEXT, mTweetText.getText().toString());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(Menu.NONE, MenuRes.CHAR_COUNT, Menu.NONE, "Character Count")
                .setActionView(mCharacterCount)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(Menu.NONE, MenuRes.IMG, Menu.NONE, "Images")
                .setIcon(R.drawable.ic_menu_camera)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add(Menu.NONE, MenuRes.LOCATION, Menu.NONE, "Location")
                .setIcon(R.drawable.ic_menu_mylocation)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add(Menu.NONE, MenuRes.CONTACTS, Menu.NONE, "Add Contact")
                .setIcon(R.drawable.ic_menu_add_contact)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add(Menu.NONE, MenuRes.SHORTEN_URL, Menu.NONE, "Shorten Urls")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        mSubmitAction = menu.add(Menu.NONE, MenuRes.SUBMIT, Menu.NONE, "Send")
                .setIcon(R.drawable.ic_menu_send);

        if(mCharacterCount != null && mCharacterCount.getText().length() > 0) {
            try {
                if(Integer.parseInt(mCharacterCount.getText().toString()) < 0) {
                    mSubmitAction.setEnabled(false);
                }
            } catch(NumberFormatException e) {

            }
        }

        mSubmitAction.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case MenuRes.IMG:
                final CharSequence[] choices = {"Camera Picture", "Gallery image"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Attach an image");

                builder.setItems(choices, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch(item) {
                            case 0:
                                //Camera Picture
                                takePhoto();
                                break;
                            case 1:
                                //Gallery Picture
                                getGalleryPhoto();
                                break;
                        }
                    }
                });

                builder.create().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupCharacterCounter() {
        mCharacterCount.setText( Integer.toString( MAX_TWEET_SIZE -
                mTweetText.getText().length() ) );

        if(mTweetText.getText().length()  > MAX_TWEET_SIZE){
            mCharacterCount.setTextColor(Color.RED);
            mSubmitAction.setEnabled(false);
        }
        else{
            mCharacterCount.setTextColor(Color.BLACK);
            mSubmitAction.setEnabled(true);
        }

        //Listen for changes in tweet text..
        mTweetText.addTextChangedListener(new TweetLengthWatcher());
    }

    private void getGalleryPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                GET_GALLERY_IMAGE);
    }

    private void takePhoto(){
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(Utils.getTempFile(getActivity())) );

        startActivityForResult(intent, GET_CAMERA_IMAGE);
    }

    public static void addToTweet(TextView tv, String text) {
        if(text == null) {
            return;
        }

        String currentText = tv.getText().toString();

        if(currentText.length() == 0) {
            tv.append(text);
        }
        else if( currentText.charAt(currentText.length()-1) == ' ' ) {
            tv.append(text);
        }
        else {
            tv.append(" " + text);
        }
    }

    /**
     * Setup for completion of '@' mentions
     */
    private void setupMentionCompletion() {
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item_people, null, FROM, TO, 0);

        mAdapter.setViewBinder(this);

        mAdapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            @Override
            public CharSequence convertToString(Cursor cursor) {
                return new StringBuilder().append('@')
                                          .append(cursor.getString(cursor.getColumnIndex(Database.Field.SCREEN_NAME)))
                                          .toString();
            }
        });

        //Filter as we type
        mAdapter.setFilterQueryProvider(new FilterQueryProvider() {

            @Override
            public Cursor runQuery(CharSequence constraint) {
                if(constraint != null) {
                    mCurrentFilter = new StringBuilder().append("\"@\" ||")
                                                        .append(Database.Ordering.SCREEN_NAME)
                                                        .append(" LIKE \"")
                                                        .append(constraint)
                                                        .append("%\"")
                                                        .toString();
                    getLoaderManager().restartLoader(0, null, NewTweetFragment.this);
                    mCurrentFilter = null;
                }

                return mAdapter.getCursor();
            }
        });


        mTweetText.setAdapter(mAdapter);

        mTweetText.setThreshold(1);

        mTweetText.setTokenizer(new MentionsTokenizer());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Get a cursor for all entry items
        return new CursorLoader(getActivity(), FollowingProvider.CONTENT_URI,
                ROWS_TO_RETURN, mCurrentFilter, null,
                Database.Ordering.SCREEN_NAME + Database.Ordering.ASC);
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
    public boolean setViewValue(View view, Cursor cursor, int col) {
        if(CursorCols.img < 0) {
            //We need to get the column numbers
            CursorCols.img = cursor.getColumnIndex(Database.Field.IMG);
        }

        if(col == CursorCols.img) {
            //Only load the image if we are not currently flinging the listview
            WebImageView img = (WebImageView) view;
            //img.reset();
            img.setImageUrl(cursor.getString(CursorCols.img));
            img.loadImage();

            return true;
        }

        return false;
    }

    private class TweetLengthWatcher implements TextWatcher {
        @Override
        public void afterTextChanged(Editable s) {
            int size = s.length();
            mCharacterCount.setText(Integer.toString( MAX_TWEET_SIZE - size ) );

            if(size > MAX_TWEET_SIZE){
                mCharacterCount.setTextColor(Color.RED);
                mSubmitAction.setEnabled(false);
            }
            else{
                mCharacterCount.setTextColor(Color.BLACK);
                mSubmitAction.setEnabled(true);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
    }

    private static class MenuRes {
        public static final int SUBMIT = 0;

        public static final int LOCATION = 1;

        public static final int IMG = 2;

        public static final int SHORTEN_URL = 3;

        public static final int CONTACTS = 4;

        public static final int CHAR_COUNT = 5;

    }

    public static class Extra {
        public static final String TEXT = "_text";
    }
}
